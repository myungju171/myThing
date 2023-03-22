package com.project.mything.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mything.config.SecurityTestConfig;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.exception.ExceptionController;
import com.project.mything.security.jwt.service.JwtTokenProvider;
import com.project.mything.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.project.mything.config.ApiDocumentUtils.getDocumentRequest;
import static com.project.mything.config.ApiDocumentUtils.getDocumentResponse;
import static com.project.mything.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
@WithMockUser
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("프로필 이미지와 상태메세지 포함 수정 성공 201")
    public void editProfile_suc() throws Exception {
        //given
        given(userService.editProFile(any(), any())).willReturn(RESPONSE_DETAIL_USER);
        String content = objectMapper.writeValueAsString(REQUEST_EDIT_PRO_FILE);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JWT_HEADER, JWT_TOKEN)
                        .content(content)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(RESPONSE_DETAIL_USER.getUserId()))
                .andExpect(jsonPath("$.name").value(RESPONSE_DETAIL_USER.getName()))
                .andExpect(jsonPath("$.phone").value(RESPONSE_DETAIL_USER.getPhone()))
                .andExpect(jsonPath("$.birthday").value(RESPONSE_DETAIL_USER.getBirthday().toString()))
                .andExpect(jsonPath("$.infoMessage").value(RESPONSE_DETAIL_USER.getInfoMessage()))
                .andExpect(jsonPath("$.avatar.imageId").value(RESPONSE_DETAIL_USER.getAvatar().getImageId()))
                .andExpect(jsonPath("$.avatar.remotePath").value(RESPONSE_DETAIL_USER.getAvatar().getRemotePath()))
                .andDo(document("프로필_수정_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestFields(
                                fieldWithPath("name").description("변경할 유저의 이름입니다."),
                                fieldWithPath("infoMessage").description("변경할 유저의 상태메세지 입니다."),
                                fieldWithPath("birthday").description("변경할 유저의 생일 입니다."),
                                fieldWithPath("avatar.imageId").description("변경할 유저의 프로필 이미지 아이디 입니다. Nullable"),
                                fieldWithPath("avatar.remotePath").description("변경할 유저의 프로필 이미지 url 입니다. Nullable")
                        ),
                        responseFields(
                                fieldWithPath("userId").description("유저 아이디 입니다."),
                                fieldWithPath("name").description("유저의 이름 입니다."),
                                fieldWithPath("phone").description("유저의 핸드폰 번호 입니다."),
                                fieldWithPath("infoMessage").description("유저의 상태메세지 입니다."),
                                fieldWithPath("birthday").description("유저의 생일 입니다."),
                                fieldWithPath("avatar.imageId").description("유저의 프로필 이미지 아이디 입니다. Nullable"),
                                fieldWithPath("avatar.remotePath").description("유저의 프로필 이미지 url 입니다. Nullable")
                        )
                ));
    }

    @Test
    @DisplayName("프로필 변경시 존재하지 않는 이미지 id를 전달할 경우 404")
    public void editProfile_fail() throws Exception {
        //given
        given(userService.editProFile(any(), any())).willThrow(new BusinessLogicException(ErrorCode.IMAGE_NOT_FOUND));
        String content = objectMapper.writeValueAsString(REQUEST_EDIT_PRO_FILE);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/users")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("프로필_수정_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestFields(
                                fieldWithPath("name").description("변경할 유저의 이름입니다."),
                                fieldWithPath("infoMessage").description("변경할 유저의 상태메세지 입니다."),
                                fieldWithPath("birthday").description("변경할 유저의 생일입니다."),
                                fieldWithPath("avatar.imageId").description("변경할 유저의 프로필 이미지 아이디 입니다. Nullable"),
                                fieldWithPath("avatar.remotePath").description("변경할 유저의 프로필 이미지 url 입니다ㅏ. Nullable")
                        )
                ));
    }

    @Test
    @DisplayName("프로필 변경시 이름, 상태 메세지, 생일을 전달하지 않을 경우 400")
    public void editProfile_fail2() throws Exception {
        //given
        String content = objectMapper.writeValueAsString(ERROR_EDIT_PRO_FILE);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/users")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("프로필_수정_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestFields(
                                fieldWithPath("name").description("변경할 유저의 이름입니다."),
                                fieldWithPath("infoMessage").description("변경할 유저의 상태메세지 입니다."),
                                fieldWithPath("birthday").description("변경할 유저의 생일입니다."),
                                fieldWithPath("avatar.imageId").description("변경할 유저의 프로필 이미지 아이디 입니다. Nullable"),
                                fieldWithPath("avatar.remotePath").description("변경할 유저의 프로필 이미지 url 입니다ㅏ. Nullable")
                        )
                ));
    }

    @Test
    @DisplayName("본인의 개인정보 확인하기 성공 200")
    public void getUserInfo_suc() throws Exception {
        //given
        given(userService.getUserInfo(any())).willReturn(RESPONSE_DETAIL_USER);
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/users")
                        .param("user-id", String.valueOf(ID1))
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(RESPONSE_DETAIL_USER.getUserId()))
                .andExpect(jsonPath("$.name").value(RESPONSE_DETAIL_USER.getName()))
                .andExpect(jsonPath("$.phone").value(RESPONSE_DETAIL_USER.getPhone()))
                .andExpect(jsonPath("$.birthday").value(RESPONSE_DETAIL_USER.getBirthday().toString()))
                .andExpect(jsonPath("$.infoMessage").value(RESPONSE_DETAIL_USER.getInfoMessage()))
                .andExpect(jsonPath("$.avatar.imageId").value(RESPONSE_DETAIL_USER.getAvatar().getImageId()))
                .andExpect(jsonPath("$.avatar.remotePath").value(RESPONSE_DETAIL_USER.getAvatar().getRemotePath()))
                .andDo(document("개인_프로필_정보_확인_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        responseFields(
                                fieldWithPath("userId").description("유저 아이디 입니다."),
                                fieldWithPath("name").description("유저의 이름 입니다."),
                                fieldWithPath("phone").description("유저의 핸드폰 번호 입니다."),
                                fieldWithPath("infoMessage").description("유저의 상태메세지 입니다."),
                                fieldWithPath("birthday").description("유저의 생일 입니다."),
                                fieldWithPath("avatar.imageId").description("유저의 프로필 이미지 아이디 입니다. Nullable"),
                                fieldWithPath("avatar.remotePath").description("유저의 프로필 이미지 url 입니다. Nullable")
                        )));
    }
}