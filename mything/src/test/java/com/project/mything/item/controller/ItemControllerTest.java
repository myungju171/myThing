package com.project.mything.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.exception.ExceptionController;
import com.project.mything.item.dto.ItemDto;
import com.project.mything.item.service.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.stream.Stream;

import static com.project.mything.config.ApiDocumentUtils.getDocumentRequest;
import static com.project.mything.config.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest({ItemController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemService itemService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("아이템검색 API 사용 성공시 200과 해당 아이템들을 배열에 담아서 전달한다.")
    public void search_suc() throws Exception {
        //given
        String content = "{\n" +
                "    \"lastBuildDate\": \"Sat, 21 Jan 2023 11:48:13 +0900\",\n" +
                "    \"total\": 80950516,\n" +
                "    \"start\": 1,\n" +
                "    \"display\": 1,\n" +
                "    \"items\": [\n" +
                "        {\n" +
                "            \"title\": \"나이키 레볼루션 6 넥스트 네이처 DC3728-001\",\n" +
                "            \"link\": \"https://search.shopping.naver.com/gate.nhn?id=33197507754\",\n" +
                "            \"image\": \"https://shopping-phinf.pstatic.net/main_3319750/33197507754.20221017111653.jpg\",\n" +
                "            \"lprice\": \"37890\",\n" +
                "            \"hprice\": \"\",\n" +
                "            \"mallName\": \"네이버\",\n" +
                "            \"productId\": \"33197507754\",\n" +
                "            \"productType\": \"1\",\n" +
                "            \"brand\": \"나이키\",\n" +
                "            \"maker\": \"나이키\",\n" +
                "            \"category1\": \"패션잡화\",\n" +
                "            \"category2\": \"남성신발\",\n" +
                "            \"category3\": \"운동화\",\n" +
                "            \"category4\": \"러닝화\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        given(itemService.search(any(), any(), any())).willReturn(new ResponseEntity<String>(
                content, HttpStatus.OK));
        //when
        ResultActions perform = mockMvc.perform(
                get("/items")
                        .param("query", "test")
                        .param("size", "10")
                        .param("sort", "sim")
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(content().string(content))
                .andDo(document("네이버_검색_API_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("query").description("검색어입니다. 필수입니다."),
                                parameterWithName("size").description("가져올 데이터의 갯수입니다. 필수값이 아닙니다. 디폴드10개"),
                                parameterWithName("sort").description("정렬조건입니다. 필수값이 아닙니다. 디폴트 sim(정확도순으로 내림차순 정렬)," +
                                        " date: 날짜순으로 내림차순 정렬,\n" +
                                        " asc: 가격순으로 오름차순 정렬,\n" +
                                        " dsc: 가격순으로 내림차순 정렬")
                        ),
                        responseBody()
                ));
    }
    @Test
    @DisplayName("아이템검색 API 사용시 파라미터를 전달하지 않을시 400 Bad_Request가 리턴된다. ")
    public void search_fail1() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                get("/items")
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("네이버_검색_API_실패_query파라미터가_존재하지_않음_400",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseBody()
                ));
    }

    @Test
    @DisplayName("검색한 아이템을 저장할때 성공시 아이템 아이디와 201을 리턴한다.")
    public void saveItem_suc() throws Exception{
    //given
        ItemDto.RequestSaveItem requestSaveItem = ItemDto.RequestSaveItem.builder()
                .userId(1L)
                .link("testLink")
                .title("testTitle")
                .productId(1L)
                .price(1000)
                .image("testImageLink")
                .build();
        ItemDto.ResponseItemId responseItemId = ItemDto.ResponseItemId.builder()
                .itemId(1L)
                .build();
        String content = objectMapper.writeValueAsString(requestSaveItem);

        given(itemService.saveItem(any())).willReturn(responseItemId);
        //when
        ResultActions perform = mockMvc.perform(
                post("/items/storages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(responseItemId.getItemId()))
                .andDo(document("아이템_저장하기_성공_201",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("요청을 보내는 유저의 아이디입니다."),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("아이템의 이름입니다."),
                                        fieldWithPath("link").type(JsonFieldType.STRING).description("해당 아이템의 사이트 주소입니다."),
                                        fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 링크입니다."),
                                        fieldWithPath("price").type(JsonFieldType.NUMBER).description("해당 아이템의 가격입니다."),
                                        fieldWithPath("productId").type(JsonFieldType.NUMBER).description("해당 아이템의 고유 아이디 값 입니다.")
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("itemId").type(JsonFieldType.NUMBER).description("서버에 저장된 아이템의 아이디값입니다.")
                                )
                        )
                ));

    }

    @Test
    @DisplayName("아이템을 중복으로 저장하면 409 CONFLICT를 리턴한다. ")
    public void searchItem_fail1() throws Exception{
    //given
        ItemDto.RequestSaveItem requestSaveItem = ItemDto.RequestSaveItem.builder()
                .userId(1L)
                .link("testLink")
                .title("testTitle")
                .productId(1L)
                .image("imageLink")
                .price(1000)
                .build();
        String content = objectMapper.writeValueAsString(requestSaveItem);
        given(itemService.saveItem(any())).willThrow(new BusinessLogicException(ErrorCode.ITEM_EXISTS));
    //when
        ResultActions perform = mockMvc.perform(
                post("/items/storages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("아이템_중복_저장하기_실패_409",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("요청을 보내는 유저의 아이디입니다."),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("아이템의 이름입니다."),
                                        fieldWithPath("link").type(JsonFieldType.STRING).description("해당 아이템의 사이트 주소입니다."),
                                        fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 링크입니다."),
                                        fieldWithPath("price").type(JsonFieldType.NUMBER).description("해당 아이템의 가격입니다."),
                                        fieldWithPath("productId").type(JsonFieldType.NUMBER).description("해당 아이템의 고유 아이디 값 입니다.")
                                )
                        )
                ));

    }

    @ParameterizedTest
    @DisplayName("이미지 저장하기 API를 사용할때 요청 dto값 중 하나라도 null값일시 400 BadRequest를 리턴한다. ")
    @MethodSource("notNullFieldValue")
    public void search_fail2(final Long userId, final String link, final String title, final Long productId,
                            final String image, final Integer price) throws Exception {
        //given
        ItemDto.RequestSaveItem requestSaveItem = ItemDto.RequestSaveItem.builder()
                .userId(userId)
                .link(link)
                .title(title)
                .productId(productId)
                .image(image)
                .price(price)
                .build();
        String content = objectMapper.writeValueAsString(requestSaveItem);
        //when
        ResultActions perform = mockMvc.perform(
                post("/items/storages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("아이템_저장하기_실패_모든_요청_DTO_필드값은_null값_허용않함_400",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("요청을 보내는 유저의 아이디입니다."),
                                        fieldWithPath("title").description("아이템의 이름입니다."),
                                        fieldWithPath("link").description("해당 아이템의 사이트 주소입니다."),
                                        fieldWithPath("image").description("이미지 링크입니다."),
                                        fieldWithPath("price").description("해당 아이템의 가격입니다."),
                                        fieldWithPath("productId").description("해당 아이템의 고유 아이디 값 입니다.")
                                )
                        )
                ));
    }
    private static Stream<Arguments> notNullFieldValue(){
        return Stream.of(
                Arguments.of(null, "testLink", "testTitle", 1L, "imageLink", 1000),
                Arguments.of(1L, null, "testTitle", 1L, "imageLink", 1000),
                Arguments.of(1L, "testLink", null, 1L, "imageLink", 1000),
                Arguments.of(1L, "testLink", "testTitle", null, "imageLink", 1000),
                Arguments.of(1L, "testLink", "testTitle", 1L, null, 1000),
                Arguments.of(1L, "testLink", "testTitle", 1L, "imageLink", null)
        );
    }

}