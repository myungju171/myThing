package com.project.mything.friend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mything.config.SecurityTestConfig;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.exception.ExceptionController;
import com.project.mything.friend.service.FriendService;
import com.project.mything.security.jwt.service.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static com.project.mything.config.ApiDocumentUtils.getDocumentRequest;
import static com.project.mything.config.ApiDocumentUtils.getDocumentResponse;
import static com.project.mything.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({FriendController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
@WithMockUser
class FriendControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    FriendService friendService;
    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("핸드폰 번호로 친구 검색 성공 200")
    public void searchFriend_suc() throws Exception {
        //given
        given(friendService.searchFriend(any())).willReturn(RESPONSE_SIMPLE_FRIEND);
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/friends/searches")
                        .param("friendPhone", PHONE)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.user.userId").value(ID1))
                .andExpect(jsonPath("$.user.name").value(NAME))
                .andExpect(jsonPath("$.user.phone").value(PHONE))
                .andExpect(jsonPath("$.user.birthday").value(BIRTHDAY.toString()))
                .andExpect(jsonPath("$.user.infoMessage").value(INFO_MESSAGE))
                .andExpect(jsonPath("$.user.avatar.imageId").value(ID1))
                .andExpect(jsonPath("$.user.avatar.remotePath").value(REMOTE_PATH))
                .andExpect(jsonPath("$.itemCount").value(ITEM_COUNT))
                .andDo(document("핸드폰번호로_유저_검색_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("friendPhone").description("검색할 친구의 핸드폰번호 입니다.")
                        ),
                        responseFields(
                                fieldWithPath("user.userId").description("검색한 유저의 아이디 입니다."),
                                fieldWithPath("user.name").description("검색한 유저의 이름 입니다."),
                                fieldWithPath("user.phone").description("검색한 유저의 핸드폰 번호 입니다."),
                                fieldWithPath("user.birthday").description("검색한 유저의 생일 입니다."),
                                fieldWithPath("user.infoMessage").description("검색한 유저의 상태메세지 입니다."),
                                fieldWithPath("user.avatar.imageId").description("검색한 유저의 프로필 이미지 아이디 입니다. Nullable"),
                                fieldWithPath("user.avatar.remotePath").description("검색한 유저의 프로필 이미지 주소 입니다. Nullable"),
                                fieldWithPath("itemCount").description("검색한 유저의 아이템 갯수 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("핸드폰 번호로 친구 검색시 핸드폰 번호가 존재하지 않을 경우 실패 404")
    public void searchFriend_fail() throws Exception {
        //given
        given(friendService.searchFriend(any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/friends/searches")
                        .param("friendPhone", PHONE)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("핸드폰번호로_유저_검색_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("friendPhone").description("검색할 친구의 핸드폰번호 입니다. \n'-'붙이지 않고 번호만 작성해 주세요. \n010 포함 11자리 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("핸드폰 번호가 11자리가 아닐 경우 실패 404")
    public void searchFriend_fail2() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/friends/searches")
                        .param("friendPhone", "0101234567")
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("핸드폰번호로_유저_검색_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("friendPhone").description("핸드폰 번호가 10자리입니다. 실패")
                        )
                ));
    }

    @Test
    @DisplayName("핸드폰 번호가 '-'을 붙일 경우 경우 실패 404")
    public void searchFriend_fail3() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/friends/searches")
                        .param("friendPhone", "010-1234-5678")
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("핸드폰번호로_유저_검색_실패3",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("friendPhone").description("핸드폰 번호가 10자리입니다. 실패")
                        )
                ));
    }

    @Test
    @DisplayName("자신의 친구의 목록을 불러올때 성공 200")
    public void getFriendsList_suc() throws Exception {
        //given
        given(friendService.getFriendsList(any(), any(), any())).willReturn(RESPONSE_SIMPLE_FRIEND_RESPONSE_MULTI_PAGE);
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/friends")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .param("friendStatus", "ACTIVE")
                        .param("isBirthday", "FALSE")
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].user.userId").value(ID1))
                .andExpect(jsonPath("$.data[0].user.name").value(NAME))
                .andExpect(jsonPath("$.data[0].user.phone").value(PHONE))
                .andExpect(jsonPath("$.data[0].user.birthday").value(BIRTHDAY.toString()))
                .andExpect(jsonPath("$.data[0].user.infoMessage").value(INFO_MESSAGE))
                .andExpect(jsonPath("$.data[0].user.avatar.imageId").value(ID1))
                .andExpect(jsonPath("$.data[0].user.avatar.remotePath").value(REMOTE_PATH))
                .andExpect(jsonPath("$.data[0].itemCount").value(ITEM_COUNT))
                .andExpect(jsonPath("$.data[1].user.userId").value(ID2))
                .andExpect(jsonPath("$.data[1].user.name").value(DIFF_NAME))
                .andExpect(jsonPath("$.data[1].user.phone").value(DIFF_PHONE))
                .andExpect(jsonPath("$.data[1].user.birthday").value(DIFF_BIRTHDAY.toString()))
                .andExpect(jsonPath("$.data[1].user.infoMessage").value(DIFF_INFO_MESSAGE))
                .andExpect(jsonPath("$.data[1].user.avatar.imageId").value(ID2))
                .andExpect(jsonPath("$.data[1].user.avatar.remotePath").value(DIFF_REMOTE_PATH))
                .andExpect(jsonPath("$.data[1].itemCount").value(ITEM_COUNT))
                .andDo(document("자신의_친구목록_조회_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(
                                parameterWithName("friendStatus").description("특정 상태의 친구만 조회합니다. ACTIVE 와 BLOCK 두개의 옵션이 있습니다. 대문자로 작성 요망."),
                                parameterWithName("isBirthday").description("오늘 생일인 유저만 조회합니다. true 와 false 두개의 옵션이 있습니다. 대문자로 작성 요망")
                        ),
                        responseFields(
                                fieldWithPath("data[].user").description("친구 상세 정보 입니다."),
                                fieldWithPath("data[].user.userId").description("유저 아이디 입니다."),
                                fieldWithPath("data[].user.name").description("유저의 이름 입니다."),
                                fieldWithPath("data[].user.phone").description("유저의 핸드폰번호 입니다."),
                                fieldWithPath("data[].user.birthday").description("유저의 생일 입니다."),
                                fieldWithPath("data[].user.infoMessage").description("유저의 상태메세지 입니다."),
                                fieldWithPath("data[].user.avatar").description("유저의 이미지 정보 입니다."),
                                fieldWithPath("data[].user.avatar.imageId").description("유저의 프로필 이미지 아이디 입니다. Nullable"),
                                fieldWithPath("data[].user.avatar.remotePath").description("유저의 프로필 이미지 url 입니다. Nullable"),
                                fieldWithPath("data[].itemCount").description("유저의 위시리스트 아이템 갯수 입니다."),
                                fieldWithPath("pageInfo").description("페이지 정보 입니다."),
                                fieldWithPath("pageInfo.page").description("현재 페이지 번호 입니다."),
                                fieldWithPath("pageInfo.size").description("현 페이지의 위시리스트 아이템 최대 갯수 입니다."),
                                fieldWithPath("pageInfo.totalElements").description("위시리스트 아이템 총 갯수 입니다."),
                                fieldWithPath("pageInfo.totalPages").description("페이지 전체 갯수 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("자신의 생일인 친구의 목록을 불러올때 성공 200")
    public void getFriendsList_fail() throws Exception {
        //given
        given(friendService.getFriendsList(any(), any(), any())).willReturn(BIRTH_RESPONSE_SIMPLE_FRIEND_RESPONSE_MULTI_PAGE);
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/friends")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .param("friendStatus", "ACTIVE")
                        .param("isBirthday", "TRUE")
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].user.userId").value(ID1))
                .andExpect(jsonPath("$.data[0].user.name").value(NAME))
                .andExpect(jsonPath("$.data[0].user.phone").value(PHONE))
                .andExpect(jsonPath("$.data[0].user.birthday").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.data[0].user.infoMessage").value(INFO_MESSAGE))
                .andExpect(jsonPath("$.data[0].user.avatar.imageId").value(ID1))
                .andExpect(jsonPath("$.data[0].user.avatar.remotePath").value(REMOTE_PATH))
                .andExpect(jsonPath("$.data[0].itemCount").value(ITEM_COUNT))
                .andExpect(jsonPath("$.data[1].user.userId").value(ID2))
                .andExpect(jsonPath("$.data[1].user.name").value(DIFF_NAME))
                .andExpect(jsonPath("$.data[1].user.phone").value(DIFF_PHONE))
                .andExpect(jsonPath("$.data[1].user.birthday").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.data[1].user.infoMessage").value(DIFF_INFO_MESSAGE))
                .andExpect(jsonPath("$.data[1].user.avatar.imageId").value(ID2))
                .andExpect(jsonPath("$.data[1].user.avatar.remotePath").value(DIFF_REMOTE_PATH))
                .andExpect(jsonPath("$.data[1].itemCount").value(ITEM_COUNT))
                .andDo(document("자신의_생일인_친구목록_조회_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(
                                parameterWithName("friendStatus").description("특정 상태의 친구만 조회합니다. ACTIVE 와 BLOCK 두개의 옵션이 있습니다. 대문자로 작성 요망."),
                                parameterWithName("isBirthday").description("오늘 생일인 유저만 조회합니다. true 와 false 두개의 옵션이 있습니다. 대문자로 작성 요망")
                        ),
                        responseFields(
                                fieldWithPath("data[].user").description("친구 상세 정보 입니다."),
                                fieldWithPath("data[].user.userId").description("유저 아이디 입니다."),
                                fieldWithPath("data[].user.name").description("유저의 이름 입니다."),
                                fieldWithPath("data[].user.phone").description("유저의 핸드폰번호 입니다."),
                                fieldWithPath("data[].user.birthday").description("유저의 생일 입니다."),
                                fieldWithPath("data[].user.infoMessage").description("유저의 상태메세지 입니다."),
                                fieldWithPath("data[].user.avatar").description("유저의 이미지 정보 입니다."),
                                fieldWithPath("data[].user.avatar.imageId").description("유저의 프로필 이미지 아이디 입니다. Nullable"),
                                fieldWithPath("data[].user.avatar.remotePath").description("유저의 프로필 이미지 url 입니다. Nullable"),
                                fieldWithPath("data[].itemCount").description("유저의 위시리스트 아이템 갯수 입니다."),
                                fieldWithPath("pageInfo").description("페이지 정보 입니다."),
                                fieldWithPath("pageInfo.page").description("현재 페이지 번호 입니다."),
                                fieldWithPath("pageInfo.size").description("현 페이지의 위시리스트 아이템 최대 갯수 입니다."),
                                fieldWithPath("pageInfo.totalElements").description("위시리스트 아이템 총 갯수 입니다."),
                                fieldWithPath("pageInfo.totalPages").description("페이지 전체 갯수 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("친구 삭제 성공 204")
    public void deleteFriend_suc() throws Exception {
    //given
    //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/friends/{delete-user-id}", ID2)
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isNoContent())
                .andDo(document("친구_삭제_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(parameterWithName("delete-user-id").description("삭제할 유저의 아이디 번호 입니다."))
                        ));
    }
}