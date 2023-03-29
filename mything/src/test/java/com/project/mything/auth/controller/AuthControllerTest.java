package com.project.mything.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mything.auth.dto.AuthDto;
import com.project.mything.auth.service.AuthService;
import com.project.mything.config.SecurityTestConfig;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.exception.ExceptionController;
import com.project.mything.security.jwt.service.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.project.mything.config.ApiDocumentUtils.getDocumentRequest;
import static com.project.mything.config.ApiDocumentUtils.getDocumentResponse;
import static com.project.mything.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({AuthController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
@WithMockUser
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthService authService;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("인증번호를 요청시 성공하면 201코드와 true를 리턴한다.")
    public void requestAuthNumber_suc() throws Exception {
        //given
        String content = objectMapper.writeValueAsString(REQUEST_AUTH_NUMBER);
        given(authService.sendAuthNumber(any())).willReturn(Boolean.TRUE);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));
        //then
        perform.andExpect(status().isCreated())
                .andExpect(content().string("true"))
                .andDo(document("인증번호_요청_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(fieldWithPath("phone").description("핸드폰 번호 11자리를 입력해주세요. \n '-' 을 붙이지 말아주세요.")),
                        responseBody()
                ));
    }

    @Test
    @DisplayName("인증번호를 요청시 11자리의 핸드폰 번호가 11자리 이상일시 400 Bad_Request 가 리턴된다. ")
    public void requestAuthNumber_fail1() throws Exception {
        //given
        AuthDto.RequestAuthNumber phone = AuthDto.RequestAuthNumber.builder().phone(INVALID_PHONE).build();
        String content = objectMapper.writeValueAsString(phone);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("인증번호_요청_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(fieldWithPath("phone").description("핸드폰 번호 11자리를 입력해주세요. \n '-' 을 붙이지 말아주세요."))
                ));
    }

    @Test
    @DisplayName("인증번호를 요청시 이미 동일한 핸드폰 번호가 존재할시 409 ")
    public void requestAuthNumber_fail2() throws Exception {
        //given
        given(authService.sendAuthNumber(any())).willThrow(new BusinessLogicException(ErrorCode.PHONE_ALREADY_EXIST));
        String content = objectMapper.writeValueAsString(REQUEST_AUTH_NUMBER);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("인증번호_요청_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(fieldWithPath("phone").description("이미 한번 인증 받은 번호는 인증번호 요청을 할 수 없습니다."))
                ));
    }

    @Test
    @DisplayName("인증번호를 요청시 이미 인증번호를 3분 이내 받았다면 409 ")
    public void requestAuthNumber_fail3() throws Exception {
        //given
        given(authService.sendAuthNumber(any())).willThrow(new BusinessLogicException(ErrorCode.MESSAGE_ALREADY_SEND));
        String content = objectMapper.writeValueAsString(REQUEST_AUTH_NUMBER);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("인증번호_요청_실패3",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(fieldWithPath("phone").description("인증번호 요청후 3분이내 인증번호 요청을 할 수 없습니다."))
                ));
    }

    @Test
    @DisplayName("회원가입 요청시 인증번호가 동일하다면 201코드와 userId를 리턴한다.")
    public void requestJoin_suc() throws Exception {
        //given
        String content = objectMapper.writeValueAsString(REQUEST_JOIN);

        given(authService.join(any())).willReturn(RESPONSE_LOGIN);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(RESPONSE_LOGIN.getUserId()))
                .andExpect(jsonPath("$.accessToken").value(RESPONSE_LOGIN.getAccessToken()))
                .andDo(document("회원가입_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일 @를 이용한이메일형식을 지켜주세요."),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호 영문+숫자+특수문자 8자 이상 20자 이하 입니다."),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("유저 이름 한글 2자 이상 16자 이하입니다."),
                                        fieldWithPath("birthday").type(JsonFieldType.STRING).description("유저의 생년월일 형식 2023-01-21"),
                                        fieldWithPath("phone").type(JsonFieldType.STRING).description("유저의 핸드폰 번호 '-'을 제거해서 010 포함 11자리 입니다."),
                                        fieldWithPath("authNumber").type(JsonFieldType.STRING).description("전달받은 동일한 인증번호를 입력하셔야 합니다.")
                                )),
                        responseFields(
                                List.of(
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원가입에 성공한 유저의 아이디 번호"),
                                        fieldWithPath("accessToken").type(JsonFieldType.STRING).description("회원가입에 성공한 유저의 엑세스 토큰")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("회원가입 요청시 해당 인증번호를 받은 핸드폰 번호가 다를시 404 No_Match_Phone_Number 리턴")
    public void requestJoin_fail1() throws Exception {
        //given
        given(authService.join(any())).willThrow(new BusinessLogicException(ErrorCode.NO_MATCH_PHONE_NUMBER));
        String content = objectMapper.writeValueAsString(REQUEST_JOIN);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("회원가입_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일 @를 이용한이메일형식을 지켜주세요."),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호 영문+숫자+특수문자 8자 이상 20자 이하 입니다."),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("유저 이름 한글 2자 이상 16자 이하입니다."),
                                        fieldWithPath("birthday").type(JsonFieldType.STRING).description("유저의 생년월일 형식 2023-01-21"),
                                        fieldWithPath("phone").type(JsonFieldType.STRING).description("유저의 핸드폰 번호 '-'을 제거해서 010 포함 11자리 입니다."),
                                        fieldWithPath("authNumber").type(JsonFieldType.STRING).description("전달받은 동일한 인증번호를 입력하셔야 합니다.")
                                ))
                ));
    }

    @Test
    @DisplayName("회원가입 요청시 인증번호가 동일하지 않다면 NO_Match_Auth_Number 404 에러를 리턴한다. ")
    public void requestJoin_fail12() throws Exception {
        //given
        given(authService.join(any())).willThrow(new BusinessLogicException(ErrorCode.NO_MATCH_AUTH_NUMBER));
        String content = objectMapper.writeValueAsString(REQUEST_JOIN);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("회원가입_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일 @를 이용한이메일형식을 지켜주세요."),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호 영문+숫자+특수문자 8자 이상 20자 이하 입니다."),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("유저 이름 한글 2자 이상 16자 이하입니다."),
                                        fieldWithPath("birthday").type(JsonFieldType.STRING).description("유저의 생년월일 형식 2023-01-21"),
                                        fieldWithPath("phone").type(JsonFieldType.STRING).description("유저의 핸드폰 번호 '-'을 제거해서 010 포함 11자리 입니다."),
                                        fieldWithPath("authNumber").type(JsonFieldType.STRING).description("전달받은 동일한 인증번호를 입력하셔야 합니다.")
                                ))
                ));
    }

    @Test
    @DisplayName("회원가입 요청시 비밀번호 규칙이 지켜지지 않았을 경우 400에러를 리턴한다. ")
    public void requestJoin_fail13() throws Exception {
        //given
        AuthDto.RequestJoin INVALID_PASSWORD_REQUEST_JOIN = AuthDto.RequestJoin.builder()
                .email(EMAIL)
                .name(NAME)
                .phone(PHONE)
                .password(INVALID_PASSWORD)
                .birthday(BIRTHDAY)
                .authNumber(AUTH_NUMBER).build();
        String content = objectMapper.writeValueAsString(INVALID_PASSWORD_REQUEST_JOIN);

        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("회원가입_실패3",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일 @를 이용한이메일형식을 지켜주세요."),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호 영문+숫자+특수문자 8자 이상 20자 이하 입니다."),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("유저 이름 한글 2자 이상 16자 이하입니다."),
                                        fieldWithPath("birthday").type(JsonFieldType.STRING).description("유저의 생년월일 형식 2023-01-21"),
                                        fieldWithPath("phone").type(JsonFieldType.STRING).description("유저의 핸드폰 번호 '-'을 제거해서 010 포함 11자리 입니다."),
                                        fieldWithPath("authNumber").type(JsonFieldType.STRING).description("전달받은 동일한 인증번호를 입력하셔야 합니다.")
                                ))
                ));
    }

    @Test
    @DisplayName("회원가입 요청시 이메일 형식이 아닐 경우 400에러를 리턴한다. ")
    public void requestJoin_fail14() throws Exception {
        //given
        AuthDto.RequestJoin INVALID_EMAIL_REQUEST_JOIN = AuthDto.RequestJoin.builder()
                .email(INVALID_EMAIL)
                .name(NAME)
                .phone(PHONE)
                .password(PASSWORD)
                .birthday(BIRTHDAY)
                .authNumber(AUTH_NUMBER).build();
        String content = objectMapper.writeValueAsString(INVALID_EMAIL_REQUEST_JOIN);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("회원가입_실패4",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일 @를 이용한이메일형식을 지켜주세요."),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호 영문+숫자+특수문자 8자 이상 20자 이하 입니다."),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("유저 이름 한글 2자 이상 16자 이하입니다."),
                                        fieldWithPath("birthday").type(JsonFieldType.STRING).description("유저의 생년월일 형식 2023-01-21"),
                                        fieldWithPath("phone").type(JsonFieldType.STRING).description("유저의 핸드폰 번호 '-'을 제거해서 010 포함 11자리 입니다."),
                                        fieldWithPath("authNumber").type(JsonFieldType.STRING).description("전달받은 동일한 인증번호를 입력하셔야 합니다.")
                                ))
                ));
    }

    @Test
    @DisplayName("회원가입 요청시 이름 규칙을 어긋날 경우 400에러를 리턴한다. ")
    public void requestJoin_fail15() throws Exception {
        //given
        AuthDto.RequestJoin INVALID_NAME_REQUEST_JOIN = AuthDto.RequestJoin.builder()
                .email(EMAIL)
                .name(INVALID_NAME)
                .phone(PHONE)
                .password(PASSWORD)
                .birthday(BIRTHDAY)
                .authNumber(AUTH_NUMBER).build();
        String content = objectMapper.writeValueAsString(INVALID_NAME_REQUEST_JOIN);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("회원가입_실패5",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일 @를 이용한이메일형식을 지켜주세요."),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호 영문+숫자+특수문자 8자 이상 20자 이하 입니다."),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("유저 이름 한글 2자 이상 16자 이하입니다."),
                                        fieldWithPath("birthday").type(JsonFieldType.STRING).description("유저의 생년월일 형식 2023-01-21"),
                                        fieldWithPath("phone").type(JsonFieldType.STRING).description("유저의 핸드폰 번호 '-'을 제거해서 010 포함 11자리 입니다."),
                                        fieldWithPath("authNumber").type(JsonFieldType.STRING).description("전달받은 동일한 인증번호를 입력하셔야 합니다.")
                                ))
                ));
    }

    @Test
    @DisplayName("로그인 요청시 아이디와 비밀번호가 이상 없다면, 201코드와 유저아이디와 엑세스 토큰을 리턴한다.")
    public void login_suc() throws Exception {
        //given
        given(authService.login(any())).willReturn(RESPONSE_LOGIN);
        String content = objectMapper.writeValueAsString(REQUEST_LOGIN);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));
        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("userId").value(RESPONSE_LOGIN.getUserId()))
                .andExpect(jsonPath("accessToken").value(RESPONSE_LOGIN.getAccessToken()))
                .andDo(document("로그인_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").description("이메일 형식으로된 아이디 입니다."),
                                fieldWithPath("password").description("유저의 비밀번호 입니다.")),
                        responseFields(
                                fieldWithPath("userId").description("로그인 한 유저의 아이디 입니다."),
                                fieldWithPath("accessToken").description("로그인 한 유저의 엑세스 토큰입니다."))
                ));
    }

    @Test
    @DisplayName("로그인 요청시 아이디가 존재하지 않을 경우 409를 리턴한다. ")
    public void login_fail1() throws Exception {
        //given
        given(authService.login(any())).willThrow(new BusinessLogicException(ErrorCode.NO_CORRECT_ACCOUNT));
        String content = objectMapper.writeValueAsString(REQUEST_LOGIN);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("로그인_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").description("잘못된 유저의 아이디 입니다. "),
                                fieldWithPath("password").description("유저의 비밀번호 입니다."))
                ));
    }

    @Test
    @DisplayName("로그인 요청시 패스워드가 다를경우 409를 리턴한다. ")
    public void login_fail2() throws Exception {
        //given
        given(authService.login(any())).willThrow(new BusinessLogicException(ErrorCode.NO_CORRECT_ACCOUNT));
        String content = objectMapper.writeValueAsString(REQUEST_LOGIN);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("로그인_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").description("유저의 아이디 입니다. "),
                                fieldWithPath("password").description("잘못된 유저의 비밀번호 입니다."))
                ));
    }

    @Test
    @DisplayName("회원가입시 동일한 이메일이 존재하지 않을시 검증이 통과한다.")
    public void duplicateEmail_suc() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                get("/auth/email")
                        .param("email", EMAIL)
        );
        //then
        verify(authService, times(1)).duplicateEmail(any());
        perform.andExpect(status().isNoContent())
                .andDo(document("이메일_중복검사_성공",
                        requestParameters(
                                parameterWithName("email").description("회원가입할 이메일입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("동일한 이메일이 존재하는지 확인시 이메일이 이메일 형식이 아닐시 400 리턴.")
    public void duplicateEmail_fail() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                get("/auth/email")
                        .param("email", INVALID_EMAIL)
        );
        //then
        verify(authService, times(0)).duplicateEmail(any());
        perform.andExpect(status().isBadRequest())
                .andDo(document("이메일_중복검사_실패1",
                        requestParameters(
                                parameterWithName("email").description("@를 이용한 이메일 형식을 지켜주세요.")
                        )
                ));
    }

    @Test
    @DisplayName("동일한 이메일이 존재하는지 확인시 이메일이 이미 존재하면 409 리턴.")
    public void duplicateEmail_fail1() throws Exception {
        //given
        doThrow(new BusinessLogicException(ErrorCode.EMAIL_ALREADY_EXIST)).when(authService).duplicateEmail(any());
        //when
        ResultActions perform = mockMvc.perform(
                get("/auth/email")
                        .param("email", EMAIL)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("이메일_중복검사_실패2",
                        requestParameters(
                                parameterWithName("email").description("이미 존재하는 이메일입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("비밀번호 찾기 성공")
    public void findPassword_suc() throws Exception {
        //given
        String content = objectMapper.writeValueAsString(REQUEST_FIND_PASSWORD);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        verify(authService, times(1)).findPassword(any());
        perform.andExpect(status().isNoContent())
                .andDo(document("비밀번호_찾기_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("phone").description("비밀번호를 찾을 핸드폰 번호 입니다. 핸드폰 번호는 '-'를 제외하고 11자리 입니다."),
                                fieldWithPath("authNumber").description("핸드폰 인증번호를 통해 받은 인증번호 4자리 입니다."),
                                fieldWithPath("newPassword").description("변경할 새로운 비밀번호 입니다. 영문+숫자+특수문자 8자 이상 20자 이하 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("비밀번호 찾기 시 인증번호가 틀릴시 실패 409")
    public void findPassword_fail1() throws Exception {
        //given
        doThrow(new BusinessLogicException(ErrorCode.NO_MATCH_AUTH_NUMBER)).when(authService).findPassword(any());
        String content = objectMapper.writeValueAsString(REQUEST_FIND_PASSWORD);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("비밀번호_찾기_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("phone").description("비밀번호를 찾을 핸드폰 번호 입니다. 핸드폰 번호는 '-'를 제외하고 11자리 입니다."),
                                fieldWithPath("authNumber").description("핸드폰 인증번호를 통해 받은 인증번호 4자리 입니다."),
                                fieldWithPath("newPassword").description("변경할 새로운 비밀번호 입니다. 영문+숫자+특수문자 8자 이상 20자 이하 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("비밀번호 찾기 시 핸드폰 번호가 존재하지 않는 유저일시 실패 404")
    public void findPassword_fail2() throws Exception {
        //given
        doThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND)).when(authService).findPassword(any());
        String content = objectMapper.writeValueAsString(REQUEST_FIND_PASSWORD);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("비밀번호_찾기_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("phone").description("비밀번호를 찾을 핸드폰 번호 입니다. 핸드폰 번호는 '-'를 제외하고 11자리 입니다."),
                                fieldWithPath("authNumber").description("핸드폰 인증번호를 통해 받은 인증번호 4자리 입니다."),
                                fieldWithPath("newPassword").description("변경할 새로운 비밀번호 입니다. 영문+숫자+특수문자 8자 이상 20자 이하 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("비밀번호 찾기 시 핸드폰번호, 인증번호, 새로운 비밀번호 유효성 검사 실패 400")
    public void findPassword_fail3() throws Exception {
        //given
        String content = objectMapper.writeValueAsString(INVALID_REQUEST_FIND_PASSWORD);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("비밀번호_찾기_실패3",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("phone").description("비밀번호를 찾을 핸드폰 번호 입니다. 핸드폰 번호는 '-'를 제외하고 11자리 입니다."),
                                fieldWithPath("authNumber").description("핸드폰 인증번호를 통해 받은 인증번호 4자리 입니다."),
                                fieldWithPath("newPassword").description("변경할 새로운 비밀번호 입니다. 영문+숫자+특수문자 8자 이상 20자 이하 입니다.")
                        )
                ));
    }

}