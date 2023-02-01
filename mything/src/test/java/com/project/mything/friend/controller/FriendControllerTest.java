package com.project.mything.friend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.exception.ExceptionController;
import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.service.FriendService;
import com.project.mything.user.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.project.mything.config.ApiDocumentUtils.getDocumentRequest;
import static com.project.mything.config.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({FriendController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class FriendControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    FriendService friendService;

    @Test
    @DisplayName("핸드폰 번호로 유저 조회시 성공 200")
    public void searchFriend_suc() throws Exception {
        //given
        FriendDto.ResponseSimpleFriend responseSimpleFriend = FriendDto.ResponseSimpleFriend.builder()
                .userId(1L)
                .name("testName")
                .birthDay(LocalDate.of(1999, 4, 8))
                .infoMessage("hello")
                .itemCount(3)
                .avatarId(1L)
                .remotePath("remotePath")
                .build();
        given(friendService.searchFriend(any())).willReturn(responseSimpleFriend);
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/friends")
                        .param("friendPhone", "01012345678")
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.name").value("testName"))
                .andExpect(jsonPath("$.infoMessage").value("hello"))
                .andExpect(jsonPath("$.birthDay").value("1999-04-08"))
                .andExpect(jsonPath("$.itemCount").value(3))
                .andDo(document("핸드폰_번호로_유저_조회시_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("friendPhone").description("조회할 친구 핸드폰 번호입니다.")
                        ),
                        responseFields(
                                fieldWithPath("userId").description("조회한 친구의 아이디 번호입니다."),
                                fieldWithPath("name").description("조회한 친구의 이름 입니다."),
                                fieldWithPath("infoMessage").description("조회한 친구의 상태메세지 입니다."),
                                fieldWithPath("birthDay").description("조회한 친구의 생일입니다."),
                                fieldWithPath("itemCount").description("조회한 친구의 아이템 갯수 입니다."),
                                fieldWithPath("avatarId").description("조회한 친구의 프로필 아이디 입니다."),
                                fieldWithPath("remotePath").description("조회한 친구의 프로필 주소 입니다.")

                        )

                ));
    }

    @Test
    @DisplayName("핸드폰 번호로 유저 조회시 존재하지 않는 폰번호 User_Not_Fount 404")
    public void searchFriend_fail() throws Exception {
        //given

        given(friendService.searchFriend(any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/friends")
                        .param("friendPhone", "01000000000")
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("존재하지_않는_핸드폰_번호로_유저_조회시_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("friendPhone").description("조회할 친구 핸드폰 번호입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("핸드폰 번호로 유저 조회시 존재하지 않는 폰번호 User_Not_Fount 404")
    public void searchFriend_fail2() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/friends")

        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("파라미터_누락_실패_400",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("핸드폰번호 파라미터가 11자리가 아닐경우 400 Bad_Request")
    public void searchFriend_fail3() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/friends")
                        .param("friendPhone", "010")
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("11자리가_아닌_핸드폰_번호로_유저_조회시_실패_400",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("friendPhone").description("조회할 친구 핸드폰 번호입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("친구리스트를 성공적으로 불러올때 친구 5명 200 리턴")
    public void getFriends_suc() throws Exception {
    //given
        List<FriendDto.ResponseSimpleFriend> responseSimpleFriends = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
           responseSimpleFriends.add(FriendDto.ResponseSimpleFriend.builder()
                    .userId((long) i)
                    .name("test"+i)
                    .infoMessage("hello")
                    .birthDay(LocalDate.of(1999, 4, 8))
                    .avatarId((long)i)
                    .remotePath("remotePath").build());
        }
        UserDto.ResponseSimpleUser responseSimpleUser = UserDto.ResponseSimpleUser.builder()
                .userId(1L)
                .name("testName")
                .image("remotePath")
                .build();

        FriendDto.ResponseMultiFriend<FriendDto.ResponseSimpleFriend> result =
                new FriendDto.ResponseMultiFriend<>(responseSimpleFriends, responseSimpleUser);

        given(friendService.getFriends(any())).willReturn(result);
    //when
        ResultActions perform = mockMvc.perform(
                get("/friends/charts")
                        .param("userId", "1")
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.userInfo.userId").value(1))
                .andExpect(jsonPath("$.userInfo.name").value("testName"))
                .andExpect(jsonPath("$.userInfo.image").value("remotePath"))
                .andDo(document("친구리스트_불러오기_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("userId").description("해당 유저아이디의 친구목록을 불러옵니다.")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data[].userId").description("친구의 아이디 번호입니다."),
                                        fieldWithPath("data[].name").description("친구의 이름 입니다."),
                                        fieldWithPath("data[].infoMessage").description("친구의 상태메세지 입니다."),
                                        fieldWithPath("data[].birthDay").description("친구의 생일 입니다."),
                                        fieldWithPath("data[].itemCount").description("친구의 아이템 갯수 입니다."),
                                        fieldWithPath("data[].avatarId").description("친구의 아바타 아이디 입니다."),
                                        fieldWithPath("data[].remotePath").description("친구의 이미지 주소 입니다."),
                                        fieldWithPath("userInfo.userId").description("친구목록을 불러온 유저의 아이디 입니다."),
                                        fieldWithPath("userInfo.name").description("친구목록을 불러온 유저의 이름입니다."),
                                        fieldWithPath("userInfo.image").description("친구목록을 불러온 유저의 이미지 주소 입니다.")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("친구리스트를 성공적으로 불러올때 200 리턴")
    public void getFriends_suc2() throws Exception {
        //given
        List<FriendDto.ResponseSimpleFriend> responseSimpleFriends = new ArrayList<>();

        UserDto.ResponseSimpleUser responseSimpleUser = UserDto.ResponseSimpleUser.builder()
                .userId(1L)
                .name("testName")
                .image("remotePath")
                .build();

        FriendDto.ResponseMultiFriend<FriendDto.ResponseSimpleFriend> result =
                new FriendDto.ResponseMultiFriend<>(responseSimpleFriends, responseSimpleUser);

        given(friendService.getFriends(any())).willReturn(result);
        //when
        ResultActions perform = mockMvc.perform(
                get("/friends/charts")
                        .param("userId", "1")
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.userInfo.userId").value(1))
                .andExpect(jsonPath("$.userInfo.name").value("testName"))
                .andExpect(jsonPath("$.userInfo.image").value("remotePath"))
                .andDo(document("친구리스트_불러오기_존재하는_친구_없음_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("userId").description("해당 유저아이디의 친구목록을 불러옵니다.")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data[]").description("존재하는 친구가 없을시 빈배열을 리턴합니다."),
                                        fieldWithPath("userInfo.userId").description("친구목록을 불러온 유저의 아이디 입니다."),
                                        fieldWithPath("userInfo.name").description("친구목록을 불러온 유저의 이름입니다."),
                                        fieldWithPath("userInfo.image").description("친구목록을 불러온 유저의 이미지 주소 입니다.")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("친구리스트를 불러올때 파라미터 없음 400 리턴")
    public void getFriends_fail1() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                get("/friends/charts")

        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("친구리스트_불러오기_파라미터_Null_400",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("친구리스트를 불러올때 존재하지 않는 유저아이디 404 리턴")
    public void getFriends_fail2() throws Exception {
        //given

        given(friendService.getFriends(any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                get("/friends/charts")
                        .param("userId", "1")
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("친구리스트_불러오기_존재하지_않는_유저아이디_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("userId").description("해당 유저아이디의 친구목록을 불러옵니다.")
                        )
                ));

    }
}