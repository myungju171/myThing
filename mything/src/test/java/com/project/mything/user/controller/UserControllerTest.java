package com.project.mything.user.controller;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.exception.ExceptionController;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.FileInputStream;

import static com.project.mything.config.ApiDocumentUtils.getDocumentRequest;
import static com.project.mything.config.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;


    @Test
    @DisplayName("프로필 이미지와 상태메세지 포함 수정 성공 201")
    public void editProfile_suc() throws Exception {
        //given
        UserDto.ResponseImageURl responseImageURl = UserDto.ResponseImageURl.builder()
                .userId(1L)
                .avatarId(1L)
                .remotePath("remotePath")
                .build();
        final String fileName = "testFile.png";
        final String contentType = "image/png";
        final String filePath = "src/main/resources/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "multipartFile",
                fileName,
                contentType,
                fileInputStream
        );
        given(userService.uploadImageAndEditUserProfile(any(), any(), any(), any(), any())).willReturn(responseImageURl);
        //when
        ResultActions perform = mockMvc.perform(
                multipart("/users/profiles")
                        .file(mockMultipartFile)
                        .param("userId", "1")
                        .param("name", "testName")
                        .param("infoMessage", "hello")
                        .param("birthDay", "1999-04-08")
                        .contentType(MediaType.MULTIPART_FORM_DATA)

        );

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.remotePath").value(responseImageURl.getRemotePath()))
                .andDo(document("프로필_이미지와_상태메세지_포함_수정_성공_201",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParts(
                                partWithName("multipartFile").description("업로드할 유저 프로필 이미지 입니다. 필수가 아닙니다.")
                        ),
                        requestParameters(
                                parameterWithName("userId").description("업로드할 파일입니다. 파일형태입니다. 필수입니다."),
                                parameterWithName("name").description("변경할 유저의 이름입니다. 필수입니다."),
                                parameterWithName("infoMessage").description("변경할 유저의 상태메세지 입니다. 필수가 아닙니다."),
                                parameterWithName("birthDay").description("변경할 유저의 생일입니다. 필수입니다.")
                        ),
                        responseFields(
                                fieldWithPath("userId").description("유저 아이디 입니다."),
                                fieldWithPath("avatarId").description("아바타 아이디 입니다."),
                                fieldWithPath("remotePath").description("이미지 주소 입니다.")
                        )

                ));
    }

    @Test
    @DisplayName("프로필 이미지와 상태메세지 포함하지않고 수정 성공 201")
    public void editProfile_suc2() throws Exception {
        //given

        UserDto.ResponseImageURl responseImageURl = UserDto.ResponseImageURl.builder()
                .userId(1L)
                .avatarId(1L)
                .remotePath("remotePath")
                .build();

        given(userService.uploadImageAndEditUserProfile(any(), any(), any(), any(), any())).willReturn(responseImageURl);
        //when
        ResultActions perform = mockMvc.perform(
                multipart("/users/profiles")
                        .param("userId", "1")
                        .param("name", "testName")
                        .param("birthDay", "1999-04-08")
                        .contentType(MediaType.MULTIPART_FORM_DATA)

        );

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.remotePath").value(responseImageURl.getRemotePath()))
                .andDo(document("프로필_이미지와_상태메세지_포함하지_않고_수정_성공_201",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("userId").description("업로드할 파일입니다. 파일형태입니다. 필수입니다."),
                                parameterWithName("name").description("변경할 유저의 이름입니다. 필수입니다."),
                                parameterWithName("birthDay").description("변경할 유저의 생일입니다. 필수입니다.")
                        ),
                        responseFields(
                                fieldWithPath("userId").description("유저 아이디 입니다."),
                                fieldWithPath("avatarId").description("아바타 아이디 입니다."),
                                fieldWithPath("remotePath").description("이미지 주소 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("프로필 수정시 존재하지 않는 유저 실패 404")
    public void editProfile_fail1() throws Exception {
        //given

        final String fileName = "testFile.png";
        final String contentType = "image/png";
        final String filePath = "src/main/resources/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "multipartFile",
                fileName,
                contentType,
                fileInputStream
        );
        given(userService.uploadImageAndEditUserProfile(any(), any(), any(), any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                multipart("/users/profiles")
                        .param("userId", "5000")
                        .param("name", "testName")
                        .param("infoMessage", "hello")
                        .param("birthDay", "1999-04-08")
                        .contentType(MediaType.MULTIPART_FORM_DATA)

        );

        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("존재하지_않는_유저_프로필_수정_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("userId").description("업로드할 파일입니다. 파일형태입니다. 필수입니다."),
                                parameterWithName("name").description("변경할 유저의 이름입니다. 필수입니다."),
                                parameterWithName("infoMessage").description("변경할 유저의 상태메세지 입니다. 필수가 아닙니다."),
                                parameterWithName("birthDay").description("변경할 유저의 생일입니다. 필수입니다.")
                        )

                ));
    }

    @Test
    @DisplayName("프로필 수정시 이미지 업로드 실패 500")
    public void editProfile_fail2() throws Exception {
        //given

        final String fileName = "testFile.png";
        final String contentType = "image/png";
        final String filePath = "src/main/resources/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "multipartFile",
                fileName,
                contentType,
                fileInputStream
        );
        given(userService.uploadImageAndEditUserProfile(any(), any(), any(), any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.S3_SERVICE_ERROR));
        //when
        ResultActions perform = mockMvc.perform(
                multipart("/users/profiles")
                        .param("userId", "1")
                        .param("name", "testName")
                        .param("infoMessage", "hello")
                        .param("birthDay", "1999-04-08")
                        .contentType(MediaType.MULTIPART_FORM_DATA)

        );

        //then
        perform.andExpect(status().isInternalServerError())
                .andDo(document("프로필_수정시_이미지_업로드_실패_500",
                        getDocumentRequest(),
                        getDocumentResponse(),

                        requestParameters(
                                parameterWithName("userId").description("업로드할 파일입니다. 파일형태입니다. 필수입니다."),
                                parameterWithName("name").description("변경할 유저의 이름입니다. 필수입니다."),
                                parameterWithName("infoMessage").description("변경할 유저의 상태메세지 입니다. 필수가 아닙니다."),
                                parameterWithName("birthDay").description("변경할 유저의 생일입니다. 필수입니다.")
                        )

                ));
    }
}