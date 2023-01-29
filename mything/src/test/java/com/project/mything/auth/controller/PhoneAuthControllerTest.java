package com.project.mything.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mything.auth.dto.PhoneAuthDto;
import com.project.mything.auth.service.PhoneAuthService;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.exception.ExceptionController;
import com.project.mything.user.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static com.project.mything.config.ApiDocumentUtils.getDocumentRequest;
import static com.project.mything.config.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({PhoneAuthController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class PhoneAuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PhoneAuthService phoneAuthService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("인증번호를 요청시 성공하면 200코드와 true를 리턴한다. ")
    public void requestAuthNumber_suc() throws Exception {
        //given
        PhoneAuthDto.RequestAuthNumber requestAuthNumber = PhoneAuthDto.RequestAuthNumber.builder()
                .phone("01011112222")
                .build();

        given(phoneAuthService.sendAuthNumber(any())).willReturn("true");

        String content = objectMapper.writeValueAsString(requestAuthNumber);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isCreated())
                .andExpect(content().string("true"))
                .andDo(document("인증번호_요청_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("phone").description("유저의 핸드폰 번호")
                                )
                        ),
                        responseBody()
                ));

    }


    @Test
    @DisplayName("인증번호를 요청시 11자리의 핸드폰 번호가 아닌 다른 번호를 요청시 400 Bad_Request 가 리턴된다. ")
    public void requestAuthNumber_fail1() throws Exception {
        //given
        PhoneAuthDto.RequestAuthNumber requestAuthNumber = PhoneAuthDto.RequestAuthNumber.builder()
                .phone("0101111")
                .build();
        String content = objectMapper.writeValueAsString(requestAuthNumber);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("11자리의_휴대폰_번호가_아닐시_실패_400",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("phone").description("유저의 핸드폰 번호")
                                )
                        ),
                        responseBody()
                ));
    }

    @Test
    @DisplayName("회원가입 요청시 인증번호가 동일하다면 200코드와 userId를 리턴한다.")
    public void requestJoin_suc() throws Exception {
        //given
        PhoneAuthDto.RequestJoin requestJoin = PhoneAuthDto.RequestJoin.builder()
                .name("홍길동")
                .phone("01011112222")
                .birthDay(LocalDate.of(1999, 11, 11))
                .authNumber("1234")
                .build();
        String content = objectMapper.writeValueAsString(requestJoin);
        UserDto.ResponseUserId responseUserId = UserDto.ResponseUserId.builder()
                .userId(1L)
                .build();

        given(phoneAuthService.join(any())).willReturn(responseUserId);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(responseUserId.getUserId()))
                .andDo(document("회원가입_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(fieldWithPath("name").type(JsonFieldType.STRING).description("유저 이름"),
                                        fieldWithPath("birthDay").type(JsonFieldType.STRING).description("유저의 생년월일 형식 2023-1-21"),
                                        fieldWithPath("phone").type(JsonFieldType.STRING).description("유저의 핸드폰 번호"),
                                        fieldWithPath("authNumber").type(JsonFieldType.STRING).description("유저의 핸드폰 인증번호")
                                )),
                        responseFields(
                                List.of(
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원가입에 성공한 유저의 아이디 번호")
                                )
                        )
                ));
    }
    @Test
    @DisplayName("회원가입 요청시 기존 회원 유저 라면 200코드와 userId를 리턴한다.")
    public void requestJoin_suc2() throws Exception {
        //given
        PhoneAuthDto.RequestJoin requestJoin = PhoneAuthDto.RequestJoin.builder()
                .name("홍길동")
                .phone("01011112222")
                .birthDay(LocalDate.of(1999, 11, 11))
                .authNumber("1234")
                .build();
        String content = objectMapper.writeValueAsString(requestJoin);
        UserDto.ResponseUserId responseUserId = UserDto.ResponseUserId.builder()
                .userId(1L)
                .build();

        given(phoneAuthService.join(any())).willReturn(responseUserId);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(responseUserId.getUserId()))
                .andDo(document("기존회원_유저아이디_리턴_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(fieldWithPath("name").type(JsonFieldType.STRING).description("유저 이름"),
                                        fieldWithPath("birthDay").type(JsonFieldType.STRING).description("유저의 생년월일 형식 2023-1-21"),
                                        fieldWithPath("phone").type(JsonFieldType.STRING).description("유저의 핸드폰 번호"),
                                        fieldWithPath("authNumber").type(JsonFieldType.STRING).description("유저의 핸드폰 인증번호")
                                )),
                        responseFields(
                                List.of(
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원가입에 성공한 유저의 아이디 번호")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("회원가입 요청시 인증번호가 동일하지 않다면 NO_Match_Auth_Number 404 에러를 리턴한다. ")
    public void requestJoin_fail1() throws Exception {
        //given
        PhoneAuthDto.RequestJoin requestJoin = PhoneAuthDto.RequestJoin.builder()
                .name("홍길동")
                .phone("01011112222")
                .birthDay(LocalDate.of(1999, 11, 11))
                .authNumber("1234")
                .build();
        String content = objectMapper.writeValueAsString(requestJoin);

        given(phoneAuthService.join(any())).willThrow(new BusinessLogicException(ErrorCode.NO_MATCH_AUTH_NUMBER));
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("회원가입시_인증번호가_다름_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(fieldWithPath("name").description("유저 이름"),
                                        fieldWithPath("birthDay").description("유저의 생년월일"),
                                        fieldWithPath("phone").description("유저의 핸드폰 번호"),
                                        fieldWithPath("authNumber").description("유저의 핸드폰 인증번호")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("회원가입 요청시 해당 인증번호를 받은 핸드폰 번호가 다를시 404 No_Match_Phone_Number 리턴")
    public void requestJoin_fail2() throws Exception {
        //given
        PhoneAuthDto.RequestJoin requestJoin = PhoneAuthDto.RequestJoin.builder()
                .name("홍길동")
                .phone("01011112222")
                .birthDay(LocalDate.of(1999, 11, 11))
                .authNumber("1234")
                .build();
        String content = objectMapper.writeValueAsString(requestJoin);

        given(phoneAuthService.join(any())).willThrow(new BusinessLogicException(ErrorCode.NO_MATCH_PHONE_NUMBER));
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("회원가입시_해당_핸드폰으로_인증번호를_받은_기록이_없음_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("name").description("유저 이름"),
                                        fieldWithPath("birthDay").description("유저의 생년월일"),
                                        fieldWithPath("phone").description("유저의 핸드폰 번호"),
                                        fieldWithPath("authNumber").description("유저의 핸드폰 인증번호")
                                ))
                ));
    }

}
