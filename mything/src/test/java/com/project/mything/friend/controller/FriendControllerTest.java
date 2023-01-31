package com.project.mything.friend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.exception.ExceptionController;
import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.service.FriendService;
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

import static com.project.mything.config.ApiDocumentUtils.getDocumentRequest;
import static com.project.mything.config.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
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
        FriendDto.ResponseFindUserResult responseFindUserResult = FriendDto.ResponseFindUserResult.builder()
                .userId(1L)
                .name("testName")
                .birthDay(LocalDate.of(1999, 4, 8))
                .infoMessage("hello")
                .itemCount(3)
                .build();
        given(friendService.searchFriend(any())).willReturn(responseFindUserResult);
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
                .andDo(document("핸드폰_번호로_유저_조회시_성공",
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
                                fieldWithPath("itemCount").description("조회한 친구의 아이템 갯수 입니다.")

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
    @DisplayName("핸드폰번호 파라미터가 11자리가 아닐경우 400 Bad_Request")
    public void searchFriend_fail2() throws Exception {
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
}