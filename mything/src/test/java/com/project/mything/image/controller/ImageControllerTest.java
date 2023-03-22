package com.project.mything.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mything.config.SecurityTestConfig;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.exception.ExceptionController;
import com.project.mything.image.service.ImageService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.project.mything.config.ApiDocumentUtils.getDocumentRequest;
import static com.project.mything.config.ApiDocumentUtils.getDocumentResponse;
import static com.project.mything.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ImageController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
@WithMockUser
class ImageControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    ImageService imageService;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("이미지 서버에 업로드 성공 201")
    public void upload_suc() throws Exception {
        //given
        given(imageService.uploadImage(any())).willReturn(SIMPLE_IMAGE_DTO);
        //when
        ResultActions perform = mockMvc.perform(
                multipart("/users/avatars")
                        .file(MOCK_MULTIPART_FILE)
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        );
        //then
        perform.andExpect(status().isCreated())
                .andDo(document("이미지_업로드_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParts(
                                partWithName("multipartFile").description("업로드할 이미지 입니다.")
                        ),
                        responseFields(
                                fieldWithPath("imageId").description("이미지 아이디 입니다."),
                                fieldWithPath("remotePath").description("이미지 주소 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("이미지 서버에 업로드 실패 500")
    public void upload_fail() throws Exception {
        //given
        given(imageService.uploadImage(any())).willThrow(new BusinessLogicException(ErrorCode.S3_SERVICE_ERROR));
        //when
        ResultActions perform = mockMvc.perform(
                multipart("/users/avatars")
                        .file(MOCK_MULTIPART_FILE)
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        );
        //then
        perform.andExpect(status().isInternalServerError())
                .andDo(document("이미지_업로드_실패(서버_에러)",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParts(
                                partWithName("multipartFile").description("업로드할 이미지 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("유저 프로필 이미지 삭제 성공 204")
    void delete_suc() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                delete("/users/avatars")
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        verify(imageService, times(1)).deleteImage(any());
        perform.andExpect(status().isNoContent())
                .andDo(document("유저_프로필_이미지_삭제_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet()
                ));
    }

    @Test
    @DisplayName("유저 프로필 이미지 삭제시 해당 유저의 아바타가 없을때 실패 409")
    void delete_fail() throws Exception {
        //given
        doThrow(new BusinessLogicException(ErrorCode.AVATAR_MUST_NOT_NULL)).when(imageService).deleteImage(any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/users/avatars")
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("유저_프로필_이미지_삭제_실패",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet()
                ));
    }
}