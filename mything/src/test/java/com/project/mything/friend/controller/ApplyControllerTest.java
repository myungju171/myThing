package com.project.mything.friend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mything.config.SecurityTestConfig;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.exception.ExceptionController;
import com.project.mything.friend.service.ApplyService;
import com.project.mything.security.jwt.service.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.project.mything.config.ApiDocumentUtils.getDocumentRequest;
import static com.project.mything.config.ApiDocumentUtils.getDocumentResponse;
import static com.project.mything.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ApplyController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
@WithMockUser
class ApplyControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ApplyService applyService;
    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("친구 신청 생성 성공 201")
    public void createApply_suc() throws Exception {
        //given
        given(applyService.createApply(any(), any())).willReturn(RESPONSE_APPLY_ID);
        //when
        ResultActions perform = mockMvc.perform(
                post("/friends/applies/{received-id}", ID1)
                        .param("receivedId", ID2.toString())
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.applyId").value(ID1))
                .andDo(document("친구_신청_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(
                                parameterWithName("received-id").description("요청을 받을 아이디 입니다.")
                        ),
                        responseFields(
                                fieldWithPath("applyId").description("생성된 요청 아이디 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("친구 신청 생성시 파라미터값이 양수가 아닐시  실패 400")
    public void createApply_fail2() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                post("/friends/applies/{received-id}", 0)
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("친구_신청_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("received-id").description("요청을 받을 아이디 입니다. 0보다 큰 자연수 입니다. ")
                        ),
                        getRequestHeadersSnippet()
                ));
    }

    @Test
    @DisplayName("친구 신청시 요청을 받는 유저의 아이디가 잘못되었을 경우 404")
    public void createApply_fail1() throws Exception {
        //given
        given(applyService.createApply(any(), any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                post("/friends/applies/{received-id}", NOT_FOUND_ID)
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("친구_신청_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(
                                parameterWithName("received-id").description("요청을 받을 아이디 입니다. 0보다 큰 자연수 입니다. ")
                        )
                ));
    }

    @Test
    @DisplayName("친구 신청 생성시 파라미터 없을시 실패 400")
    public void createApply_fail() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                post("/friends/applies")
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("친구_신청_실패3",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet()
                ));
    }

    @Test
    @DisplayName("친구 요청 거부 성공 204")
    public void rejectsApply_suc() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies/rejects")
                        .param("applyId", ID1.toString())
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        verify(applyService, times(1)).rejectApply(any(), any());
        perform.andExpect(status().isNoContent())
                .andDo(document("친구_요청_거부_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(parameterWithName("applyId").description("친구 신청 applyId 입니다."))
                ));
    }

    @Test
    @DisplayName("친구 요청 거부시 apply의 receiveId가 본인의 아이디와 동일하지 않을시 실패 409")
    public void rejectsApply_fail4() throws Exception {
        //given
        doThrow(new BusinessLogicException(ErrorCode.APPLY_RECEIVER_CONFLICT)).when(applyService).rejectApply(any(), any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies/rejects")
                        .param("applyId", ID1.toString())
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("친구_요청_거부_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(
                                parameterWithName("applyId").description("applyId의 receiveId가 본인의 아이디와 다르면 안됩니다.")
                        )
                ));
    }

    @Test
    @DisplayName("친구 요청 거부시 applyId가 유효하지 않을때 실패 404")
    public void rejectsApply_fail2() throws Exception {
        //given
        doThrow(new BusinessLogicException(ErrorCode.APPLY_NOT_FOUND)).when(applyService).rejectApply(any(), any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies/rejects")
                        .param("applyId", NOT_FOUND_ID.toString())
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("친구_요청_거부_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(
                                parameterWithName("applyId").description("존재하지 않는 applyId 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("친구 요청 거부시 applyId가 양수가 아닐때 실패 400")
    public void rejectsApply_fail1() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies/rejects")
                        .param("applyId", "0")
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("친구_요청_거부_실패3",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(
                                parameterWithName("applyId").description("applyId는 양수이어야 합니다.")
                        )
                ));
    }

    @Test
    @DisplayName("친구 요청 거부시 파라미터 누락시 실패 400")
    public void rejectsApply_fail3() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies/rejects")
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("친구_요청_거부_실패4",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet()
                ));
    }

    @Test
    @DisplayName("친구 요청을 취소 성공 204")
    public void cancelApply_suc() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies/cancels")
                        .param("applyId", ID1.toString())
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        verify(applyService, times(1)).cancelApply(any(), any());
        perform.andExpect(status().isNoContent())
                .andDo(document("친구_요청_취소_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(parameterWithName("applyId").description("친구 신청 applyId 입니다."))
                ));
    }

    @Test
    @DisplayName("친구 요청을 취소시 applyId의 senderId가 본인의 아이디와 다를시 실패 409")
    public void cancelApply_fail() throws Exception {
        //given
        doThrow(new BusinessLogicException(ErrorCode.APPLY_SENDER_CONFLICT)).when(applyService).cancelApply(any(), any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies/cancels")
                        .param("applyId", ID1.toString())
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("친구_요청_취소_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(
                                parameterWithName("applyId").description("applyId의 senderId가 본인의 아이디와 다르면 안됩니다.")
                        )
                ));
    }

    @Test
    @DisplayName("친구 요청을 취소시 applyId가 유효한 아이디가 아닐시 실패 404")
    public void cancelApply_fail2() throws Exception {
        //given
        doThrow(new BusinessLogicException(ErrorCode.APPLY_NOT_FOUND)).when(applyService).cancelApply(any(), any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies/cancels")
                        .param("applyId", ID1.toString())
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("친구_요청_취소_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(
                                parameterWithName("applyId").description("applyId가 존재하지 않는 아이디 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("친구 요청을 취소시 applyId가 양수가 아닐시 실패 400")
    public void cancelApply_fail3() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies/cancels")
                        .param("applyId", "0")
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("친구_요청_취소_실패3",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(
                                parameterWithName("applyId").description("applyId는 양수이어야 합니다.")
                        )
                ));
    }

    @Test
    @DisplayName("친구 요청을 취소시 applyId가 누락시 실패 400")
    public void cancelApply_fail4() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies/cancels")
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("친구_요청_취소_실패3",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet()
                ));
    }

    @Test
    @DisplayName("친구요청 수락시 성공 200")
    public void acceptApply_suc() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .param("applyId", ID1.toString())
        );
        //then
        perform.andExpect(status().isNoContent())
                .andDo(document("친구_요청_수락_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(parameterWithName("applyId").description("친구 신청 applyId 입니다."))
                ));
    }

    @Test
    @DisplayName("친구요청 수락시 apply의 recieveId가 본인의 id와 다를시 409")
    public void acceptApply_fail() throws Exception {
        //given
        doThrow(new BusinessLogicException(ErrorCode.APPLY_RECEIVER_CONFLICT)).when(applyService).acceptApply(any(), any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .param("applyId", ID1.toString())
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("친구_요청_수락_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(
                                parameterWithName("applyId").description("applyId의 receiveId가 본인의 아이디와 다르면 안됩니다.")
                        )
                ));
    }

    @Test
    @DisplayName("친구요청 수락시 applyId가 유효하지 않을시 404")
    public void acceptApply_fail2() throws Exception {
        //given
        doThrow(new BusinessLogicException(ErrorCode.APPLY_NOT_FOUND)).when(applyService).acceptApply(any(), any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .param("applyId", NOT_FOUND_ID.toString())
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("친구_요청_수락_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(
                                parameterWithName("applyId").description("applyId가 존재하지 않는 아이디 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("친구요청 수락시 applyId가 양수가 아닐시 400")
    public void acceptApply_fail3() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .param("applyId", "0")
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("친구_요청_수락_실패3",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(
                                parameterWithName("applyId").description("applyId가 양수이어야 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("친구요청 수락시 applyId가 누락시 400")
    public void acceptApply_fail4() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                delete("/friends/applies")
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("친구_요청_수락_실패4",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet()
                ));
    }

    @Test
    @DisplayName("친구 신청 목록 확인 성공 200")
    public void getSendApply_suc() throws Exception {
        //given
        given(applyService.getApply(any(), any(), any())).willReturn(RESPONSE_SIMPLE_APPLY_LIST);
        //when
        ResultActions perform = mockMvc.perform(
                get("/friends/applies")
                        .param("isReceiveApply", "TRUE")
                        .param("applyStatus", "SUGGEST")
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].applyId").value(ID1))
                .andExpect(jsonPath("$.[0].user.userId").value(ID1))
                .andExpect(jsonPath("$.[0].user.name").value(NAME))
                .andExpect(jsonPath("$.[0].user.avatar.imageId").value(ID1))
                .andExpect(jsonPath("$.[0].user.avatar.remotePath").value(REMOTE_PATH))
                .andExpect(jsonPath("$.[1].applyId").value(ID2))
                .andExpect(jsonPath("$.[1].user.userId").value(ID2))
                .andExpect(jsonPath("$.[1].user.name").value(DIFF_NAME))
                .andExpect(jsonPath("$.[1].user.avatar.imageId").value(ID2))
                .andExpect(jsonPath("$.[1].user.avatar.remotePath").value(DIFF_REMOTE_PATH))
                .andDo(document("친구_요청받은_목록_확인_성공1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(
                                parameterWithName("isReceiveApply").description("FALSE 값을 적으시면 친구 신청 보낸 목록을 확인할 수 있습니다. \n 대문자로 작성 부탁드립니다."),
                                parameterWithName("applyStatus").description("SUGGEST를 작성하시면 차단하지 않은 상태의 요청을 볼 수 있습니다.(SUGGEST, REJECT 가능)  \n 대문자로 작성 부탁드립니다.")
                        ),
                        responseFields(
                                fieldWithPath("[].applyId").description("요청 아이디 입니다."),
                                fieldWithPath("[].user").description("요청을 보낸 유저 정보 입니다."),
                                fieldWithPath("[].user.userId").description("요청을 보낸 유저 아이디 입니다."),
                                fieldWithPath("[].user.name").description("요청을 보낸 유저 이름입니다."),
                                fieldWithPath("[].user.avatar").description("요청을 보낸 유저의 프로필 정보입니다. Nullable"),
                                fieldWithPath("[].user.avatar.imageId").description("요청을 보낸 유저의 프로필 이미지 아이디 입니다. Nullable"),
                                fieldWithPath("[].user.avatar.remotePath").description("요청을 보낸 유저의 프로필 이미지 url 입니다. Nullable")
                        )
                ));
    }

    @Test
    @DisplayName("친구 신청 목록 확인 성공 200")
    public void getSendApply_suc2() throws Exception {
        //given
        given(applyService.getApply(any(), any(), any())).willReturn(RESPONSE_SIMPLE_APPLY_LIST);
        //when
        ResultActions perform = mockMvc.perform(
                get("/friends/applies")
                        .param("isReceiveApply", "FALSE")
                        .param("applyStatus", "SUGGEST")
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].applyId").value(ID1))
                .andExpect(jsonPath("$.[0].user.userId").value(ID1))
                .andExpect(jsonPath("$.[0].user.name").value(NAME))
                .andExpect(jsonPath("$.[0].user.avatar.imageId").value(ID1))
                .andExpect(jsonPath("$.[0].user.avatar.remotePath").value(REMOTE_PATH))
                .andExpect(jsonPath("$.[1].applyId").value(ID2))
                .andExpect(jsonPath("$.[1].user.userId").value(ID2))
                .andExpect(jsonPath("$.[1].user.name").value(DIFF_NAME))
                .andExpect(jsonPath("$.[1].user.avatar.imageId").value(ID2))
                .andExpect(jsonPath("$.[1].user.avatar.remotePath").value(DIFF_REMOTE_PATH))
                .andDo(document("친구_신청한_목록_확인_성공2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(
                                parameterWithName("isReceiveApply").description("FALSE 값을 적으시면 친구 신청 보낸 목록을 확인할 수 있습니다. \n 대문자로 작성 부탁드립니다."),
                                parameterWithName("applyStatus").description("SUGGEST를 작성하시면 차단하지 않은 상태의 요청을 볼 수 있습니다.(SUGGEST, REJECT 가능)  \n 대문자로 작성 부탁드립니다.")
                        ),
                        responseFields(
                                fieldWithPath("[].applyId").description("요청 아이디 입니다."),
                                fieldWithPath("[].user").description("요청을 받은 유저 정보 입니다."),
                                fieldWithPath("[].user.userId").description("요청을 받은 유저 아이디 입니다."),
                                fieldWithPath("[].user.name").description("요청을 받은 유저 이름입니다."),
                                fieldWithPath("[].user.avatar").description("요청을 받은 유저의 프로필 정보입니다. Nullable"),
                                fieldWithPath("[].user.avatar.imageId").description("요청을 받은 유저의 프로필 이미지 아이디 입니다. Nullable"),
                                fieldWithPath("[].user.avatar.remotePath").description("요청을 받은 유저의 프로필 이미지 url 입니다. Nullable")
                        )
                ));
    }

    @Test
    @DisplayName("친구 신청 목록 확인시 파라미터 값 누락 실패 400 ")
    public void getSendApply_fail1() throws Exception {
        //given
        given(applyService.getApply(any(), any(), any())).willReturn(RESPONSE_SIMPLE_APPLY_LIST);
        //when
        ResultActions perform = mockMvc.perform(
                get("/friends/applies")
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("친구_목록_확인_실패",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet()
                ));
    }
}