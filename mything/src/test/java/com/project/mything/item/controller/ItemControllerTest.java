package com.project.mything.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mything.config.SecurityTestConfig;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.exception.ExceptionController;
import com.project.mything.item.dto.ItemDto;
import com.project.mything.item.entity.enums.ItemStatus;
import com.project.mything.item.service.ItemService;
import com.project.mything.page.ResponseMultiPageDto;
import com.project.mything.security.jwt.service.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static com.project.mything.config.ApiDocumentUtils.getDocumentRequest;
import static com.project.mything.config.ApiDocumentUtils.getDocumentResponse;
import static com.project.mything.util.TestConstants.*;
import static java.sql.Types.NULL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest({ItemController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
@WithMockUser
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemService itemService;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    JwtTokenProvider jwtTokenProvider;

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

        given(itemService.search(any(), any(), any(), any())).willReturn(new ResponseEntity<String>(
                content, HttpStatus.OK));
        //when
        ResultActions perform = mockMvc.perform(
                get("/items/search")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .param("query", "test")
                        .param("size", "10")
                        .param("sort", "sim")
                        .param("start", "1")
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(content().string(content))
                .andDo(document("네이버_검색_API_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestParameters(
                                parameterWithName("query").description("검색어입니다. 필수입니다."),
                                parameterWithName("size").description("가져올 데이터의 갯수입니다. 필수값이 아닙니다. 디폴드10개"),
                                parameterWithName("sort").description("정렬조건입니다. 필수값이 아닙니다. 디폴트 sim(정확도순으로 내림차순 정렬)," +
                                        " date: 날짜순으로 내림차순 정렬,\n" +
                                        " asc: 가격순으로 오름차순 정렬,\n" +
                                        " dsc: 가격순으로 내림차순 정렬"),
                                parameterWithName("start").description("가져올 데이터의 페이지 번호 입니다. 필수값이 아닙니다. 디폴트 1번")
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
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("네이버_검색_API_실패",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        responseBody()
                ));
    }

    @Test
    @DisplayName("검색한 아이템을 저장할때 성공시 아이템 아이디와 201을 리턴한다.")
    public void saveItem_suc() throws Exception {
        //given
        String content = objectMapper.writeValueAsString(REQUEST_SAVE_ITEM);
        given(itemService.saveItem(any(), any())).willReturn(RESPONSE_ITEM_ID);
        //when
        ResultActions perform = mockMvc.perform(
                post("/items")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(RESPONSE_ITEM_ID.getItemId()))
                .andDo(document("아이템_저장_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("아이템의 이름입니다."),
                                fieldWithPath("link").type(JsonFieldType.STRING).description("해당 아이템의 사이트 주소입니다."),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 링크입니다."),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("해당 아이템의 가격입니다."),
                                fieldWithPath("productId").type(JsonFieldType.NUMBER).description("해당 아이템의 고유 아이디 값 입니다.")
                        ),
                        responseFields(fieldWithPath("itemId").type(JsonFieldType.NUMBER).description("서버에 저장된 아이템의 아이디값입니다."))
                ));

    }

    @Test
    @DisplayName("아이템을 중복으로 저장하면 409 CONFLICT를 리턴한다. ")
    public void searchItem_fail1() throws Exception {
        //given
        String content = objectMapper.writeValueAsString(REQUEST_SAVE_ITEM);
        given(itemService.saveItem(any(), any())).willThrow(new BusinessLogicException(ErrorCode.ITEM_EXISTS));
        //when
        ResultActions perform = mockMvc.perform(
                post("/items")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("아이템_저장_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("아이템의 이름입니다."),
                                fieldWithPath("link").type(JsonFieldType.STRING).description("해당 아이템의 사이트 주소입니다."),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 링크입니다."),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("해당 아이템의 가격입니다."),
                                fieldWithPath("productId").type(JsonFieldType.NUMBER).description("해당 아이템의 고유 아이디 값 입니다.")
                        )
                ));

    }

    @Test
    @DisplayName("아이템 저장하기 API를 사용할때 요청 dto값 중 하나라도 null값일시 400 BadRequest를 리턴한다. ")
    public void search_fail2() throws Exception {
        //given
        ItemDto.RequestSaveItem invalidRequestSaveItem = ItemDto.RequestSaveItem.builder()
                .title(TITLE)
                .productId(ID1)
                .image(IMAGE)
                .price(PRICE)
                .build();
        String content = objectMapper.writeValueAsString(invalidRequestSaveItem);
        //when
        ResultActions perform = mockMvc.perform(
                post("/items")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("아이템_저장_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("null 값을 허용하지 않습니다."),
                                fieldWithPath("link").type(JsonFieldType.NULL).description("null 값을 허용하지 않습니다."),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("null 값을 허용하지 않습니다."),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("null 값을 허용하지 않습니다."),
                                fieldWithPath("productId").type(JsonFieldType.NUMBER).description("null 값을 허용하지 않습니다.")
                        )
                ));
    }

    @Test
    @DisplayName("UserId 와 ItemId를 보냈을때 해당 아이디를 가지고있는 ItemUser가 존재할시 200 ResponseDetailItem객체를 리턴함")
    public void getDetailPage_suc() throws Exception {
        //given
        given(itemService.getDetailItem(any(), any())).willReturn(RESPONSE_DETAIL_ITEM);
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/items/{item-id}", ID1)
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("itemId").value(RESPONSE_DETAIL_ITEM.getItemId()))
                .andExpect(jsonPath("title").value(RESPONSE_DETAIL_ITEM.getTitle()))
                .andExpect(jsonPath("link").value(RESPONSE_DETAIL_ITEM.getLink()))
                .andExpect(jsonPath("price").value(RESPONSE_DETAIL_ITEM.getPrice()))
                .andExpect(jsonPath("image").value(RESPONSE_DETAIL_ITEM.getImage()))
                .andExpect(jsonPath("memo").value(RESPONSE_DETAIL_ITEM.getMemo()))
                .andExpect(jsonPath("interestedItem").value(RESPONSE_DETAIL_ITEM.getInterestedItem()))
                .andExpect(jsonPath("secretItem").value(RESPONSE_DETAIL_ITEM.getSecretItem()))
                .andExpect(jsonPath("itemStatus").value(RESPONSE_DETAIL_ITEM.getItemStatus().toString()))
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("lastModifiedAt").exists())
                .andDo(document("아이템_상세조회_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(
                                parameterWithName("item-id").description("아이템 아이디 입니다.")
                        ),
                        responseFields(
                                fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                fieldWithPath("title").description("아이템 이름입니다."),
                                fieldWithPath("link").description("아이템 네이버 주소입니다."),
                                fieldWithPath("price").description("아이템 가격입니다."),
                                fieldWithPath("image").description("아이템 이미지 주소입니다."),
                                fieldWithPath("memo").description("아이템에 대한 짧은 메모입니다."),
                                fieldWithPath("interestedItem").description("관심있는 아이템 유/무 입니다."),
                                fieldWithPath("secretItem").description("비밀 아이템 유/무 입니다."),
                                fieldWithPath("itemStatus").description("아이템 상태입니다."),
                                fieldWithPath("createdAt").description("아이템이 생성된 시점입니다."),
                                fieldWithPath("lastModifiedAt").description("아이템이 마지막으로 수정된 시점입니다.")
                        )
                ));

    }

    @Test
    @DisplayName("아이템 상세 조회시 존재하지 않는 아이템을 조회할때 404 ITEM_NOT_FOUND 리턴")
    public void getDetailPage_fail1() throws Exception {
        //given
        given(itemService.getDetailItem(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/items/{item-id}", 1L)
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("아이템_상세조회_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(
                                parameterWithName("item-id").description("아이템 아이디 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("리스트로 본인의 아이템을 조회할때 성공시 200과 ResponseSimpleItem를 리스트로 전달받는다.")
    public void getSimpleItems_suc() throws Exception {
        //given
        List<ItemDto.ResponseSimpleItem> data = new ArrayList<>();
        setData(data);
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("itemStatus").descending().and(Sort.by("id").descending()));
        Page<ItemDto.ResponseSimpleItem> responseSimpleItems = new PageImpl<>(data, pageRequest, 5);
        ResponseMultiPageDto<ItemDto.ResponseSimpleItem> responseMultiPageDto =
                new ResponseMultiPageDto<ItemDto.ResponseSimpleItem>(data, responseSimpleItems);

        given(itemService.getSimpleItems(any(), any(), any(), any(), any(), any())).willReturn(responseMultiPageDto);

        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/items/users/{user-id}", 1)
                        .header(JWT_HEADER, JWT_TOKEN)
                        .param("isWish", "TRUE")
                        .param("isFriend", "TRUE")
                        .param("sortBy", "사용하지 않습니다.")
                        .param("start", "1")
                        .param("size", "5")
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(5))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(5))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1))
                .andDo(document("아이템_리스트_조회_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(parameterWithName("user-id").description("확인할 해당 유저의 아이디 입니다.")),
                        requestParameters(
                                parameterWithName("isWish").description("POST와 RESERVED 확인시 TRUE, BOUGHT와 RECEIVED 확인시 FALSE입니다. Default = TRUE. Nullable"),
                                parameterWithName("isFriend").description("본인의 위시리스트 확인시 FALSE, 남의 위시리스트 확인시 TRUE입니다. Default = TRUE. Nullable"),
                                parameterWithName("sortBy").description("현재 사용하지 않는 파라미터 입니다. 기본정렬 좋아요 + 최신순 Nullable"),
                                parameterWithName("start").description("현재 보여질 페이지 번호입니다."),
                                parameterWithName("size").description("한페이지에 보여질 게시글의 갯수입니다.")
                        ),
                        responseFields(
                                fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("아이템을 리스트 형태로 보여줍니다."),
                                fieldWithPath("data[].itemId").type(JsonFieldType.NUMBER).description("아이템의 아이디 번호 입니다."),
                                fieldWithPath("data[].title").type(JsonFieldType.STRING).description("아이템의 타이틀입니다."),
                                fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("아이템의 가격입니다."),
                                fieldWithPath("data[].image").type(JsonFieldType.STRING).description("아이템의 이미지 주소입니다."),
                                fieldWithPath("data[].interestedItem").type(JsonFieldType.BOOLEAN).description("관심상품 유무입니다.true 관심, false 관심없음"),
                                fieldWithPath("data[].secretItem").type(JsonFieldType.BOOLEAN).description("공개/비공개 상품 유무입니다. ture 비공개, false 공개"),
                                fieldWithPath("data[].itemStatus").type(JsonFieldType.STRING).description("아이템의 상태입니다."),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("아이템을 등록한 날자입니다."),
                                fieldWithPath("data[].lastModifiedAt").type(JsonFieldType.STRING).description("아이템을 최종 수정한 날짜 입니다."),
                                fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 보여질 페이지 입니다."),
                                fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("한페이지에 들어갈 게시글의 갯수 입니다."),
                                fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 게시글의 갯수 입니다."),
                                fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 숫자입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("리스트로 아이템 조회시 존재하지 않는 유저 아이디로 요청을 보낼때")
    public void getSimpleItems_fail() throws Exception {
        //given
        given(itemService.getSimpleItems(any(), any(), any(), any(), any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/items/users/{user-id}", NOT_FOUND_ID)
                        .header(JWT_HEADER, JWT_TOKEN)
                        .param("isWish", "TRUE")
                        .param("isFriend", "TRUE")
                        .param("sortBy", "사용하지 않습니다.")
                        .param("start", "1")
                        .param("size", "5")
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("아이템_리스트_조회_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(
                                parameterWithName("user-id").description("아이템을 가져올 유저의 아이디값입니다.")
                        ),
                        requestParameters(
                                parameterWithName("isWish").description("POST와 RESERVED 확인시 TRUE, BOUGHT와 RECEIVED 확인시 FALSE입니다. Default = TRUE. Nullable"),
                                parameterWithName("isFriend").description("본인의 위시리스트 확인시 FALSE, 남의 위시리스트 확인시 TRUE입니다. Default = TRUE. Nullable"),
                                parameterWithName("sortBy").description("현재 사용하지 않는 파라미터 입니다. 기본정렬 좋아요 + 최신순 Nullable"),
                                parameterWithName("start").description("현재 보여질 페이지 번호입니다."),
                                parameterWithName("size").description("한페이지에 보여질 게시글의 갯수입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("파라미터를 작성하지 않고 요청을 보낼때 실패 400")
    public void getSimpleItems_fail2() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/items/users/{user-id}", ID1)
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("아이템_리스트_조회_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(
                                parameterWithName("user-id").description("아이템을 가져올 유저의 아이디값입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 BOUGHT로 변경할때 성공")
    public void changeItemStatus_suc() throws Exception {
        //given
        String content = objectMapper.writeValueAsString(REQUEST_CHANGE_ITEM_STATUS);
        given(itemService.changeItemStatus(any(), any())).willReturn(RESPONSE_ITEM_ID);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(ID1))
                .andDo(document("아이템_상태_BOUGHT로_변경_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestFields(
                                fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                fieldWithPath("itemStatus").description("변경하고 싶은 아이템 상태 BOUGHT, 대문자로 작성."),
                                fieldWithPath("reservedId").type(NULL).description("아이템 상태를 RESERVED로 변경시에만 작성 합니다.Nullable")
                        ),
                        responseFields(
                                fieldWithPath("itemId").description("변경된 아이템의 아이디 입니다.")
                        )));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 RECEIVE로 변경할때 성공")
    public void changeItemStatus_suc3() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus receivedRequest = ItemDto.RequestChangeItemStatus.builder()
                .itemId(ID1)
                .itemStatus(ItemStatus.RECEIVED)
                .build();
        String content = objectMapper.writeValueAsString(receivedRequest);
        given(itemService.changeItemStatus(any(), any())).willReturn(RESPONSE_ITEM_ID);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(ID1))
                .andDo(document("아이템_상태_RECEIVED로_변경_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestFields(
                                fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                fieldWithPath("itemStatus").description("변경하고 싶은 아이템 상태 RECEIVED, 대문자로 작성."),
                                fieldWithPath("reservedId").type(NULL).description("아이템 상태를 RESERVED로 변경시에만 작성 합니다.Nullable")
                        ),
                        responseFields(
                                fieldWithPath("itemId").description("변경된 아이템의 아이디 입니다.")
                        )));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 RESERVE로 변경할때 성공")
    public void changeItemStatus_suc2() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus reserveRequest = ItemDto.RequestChangeItemStatus.builder()
                .itemId(ID1)
                .itemStatus(ItemStatus.RESERVE)
                .reservedId(ID2)
                .build();
        String content = objectMapper.writeValueAsString(reserveRequest);
        given(itemService.changeItemStatus(any(), any())).willReturn(RESPONSE_ITEM_ID);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(ID1))
                .andDo(document("아이템_상태_RESERVE로_변경_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestFields(
                                fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                fieldWithPath("itemStatus").description("변경하고 싶은 아이템 상태 RESERVE, 대문자로 작성."),
                                fieldWithPath("reservedId").description("아이템상태를 RESERVE로 변경시 필요합니다. ")
                        ),
                        responseFields(
                                fieldWithPath("itemId").description("변경된 아이템의 아이디 입니다.")
                        )));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 RESERVE로 변경할때 본인이 본인의 아이템을 예약시 409실패")
    public void changeItemStatus_fail3() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus reserveRequest = ItemDto.RequestChangeItemStatus.builder()
                .itemId(ID1)
                .itemStatus(ItemStatus.RESERVE)
                .reservedId(ID2)
                .build();
        String content = objectMapper.writeValueAsString(reserveRequest);
        given(itemService.changeItemStatus(any(), any())).willThrow(new BusinessLogicException(ErrorCode.RESERVE_USER_CONFLICT));
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("아이템_상태_RESERVE로_변경_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestFields(
                                fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                fieldWithPath("itemStatus").description("변경하고 싶은 아이템 상태 RESERVE, 대문자로 작성."),
                                fieldWithPath("reservedId").description("본인이 본인의 아이템을 예약시 예외가 발생합니다. ")
                        )
                ));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 RESERVE로 변경할때 존재하지 않는 reservedId 404 실패")
    public void changeItemStatus_fail4() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus reserveRequest = ItemDto.RequestChangeItemStatus.builder()
                .itemId(ID1)
                .itemStatus(ItemStatus.RESERVE)
                .reservedId(NOT_FOUND_ID)
                .build();
        String content = objectMapper.writeValueAsString(reserveRequest);
        given(itemService.changeItemStatus(any(), any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("아이템_상태_RESERVE로_변경_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestFields(
                                fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                fieldWithPath("itemStatus").description("변경하고 싶은 아이템 상태 RESERVE, 대문자로 작성."),
                                fieldWithPath("reservedId").description("존재하지 않는 reservedId일시 예외 발생 ")
                        )
                ));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 BOUGHT로 변경할때 존재하지 않는 아이템 Id일때 404 실패")
    public void changeItemStatus_fail() throws Exception {
        //given
        String content = objectMapper.writeValueAsString(REQUEST_CHANGE_ITEM_STATUS);
        given(itemService.changeItemStatus(any(), any())).willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("아이템_상태_변경_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        requestFields(
                                fieldWithPath("itemId").description("아이템 아이디 입니다. 존재하지 않는 아이템 id일시 예외 발생"),
                                fieldWithPath("itemStatus").description("변경하고 싶은 아이템 상태 BOUGHT, 대문자로 작성."),
                                fieldWithPath("reservedId").description("")
                        )
                ));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 BOUGHT로 변경할때 dto 값 누락시 전송하지 않을 경우 실패")
    public void changeItemStatus_fail2() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("아이템_상태_변경_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet()
                ));
    }


    @Test
    @DisplayName("예약된 아이템 예약취소시 정확한 정보를 전달할 경우 204 No Content 리턴")
    public void cancelReserve_suc() throws Exception {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .itemId(ID1)
                .itemOwnerUserId(ID2)
                .build();
        String content = objectMapper.writeValueAsString(requestCancelReserveItem);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/statuses")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then

        perform.andExpect(status().isNoContent())
                .andDo(document("아이템_예약취소_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(headerWithName(JWT_HEADER).description("아이템을 예약했었던 유저 입니다.")),
                        requestFields(
                                fieldWithPath("itemId").description("아이템의 아이디 번호입니다."),
                                fieldWithPath("itemOwnerUserId").description("아이템 소유자 유저의 아이디 번호입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("예약된 아이템 예약취소시 요청 값이 누락되었을 경우 400 Bad_Request 리턴")
    public void cancelReserve_fail() throws Exception {
        //given

        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/statuses")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then

        perform.andExpect(status().isBadRequest())
                .andDo(document("아이템_예약취소_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet()
                ));
    }

    @Test
    @DisplayName("예약된 아이템 예약취소시 itemOwnerUserId 또는 itemId가 존재하지 않을시 404 리턴")
    public void cancelReserve_fail2() throws Exception {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .itemId(NOT_FOUND_ID)
                .itemOwnerUserId(NOT_FOUND_ID)
                .build();
        doThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND)).when(itemService).cancelReservedItem(any(), any());
        String content = objectMapper.writeValueAsString(requestCancelReserveItem);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/statuses")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then

        perform.andExpect(status().isNotFound())
                .andDo(document("아이템_예약취소_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(headerWithName(JWT_HEADER).description("아이템을 예약했었던 유저 입니다.")),
                        requestFields(
                                fieldWithPath("itemId").description("아이템 아이디가 존재하지 않을시 예외발생"),
                                fieldWithPath("itemOwnerUserId").description("아이템 소유자 유저의 아이디가 존재하지 않을시 예외발생")
                        )
                ));
    }

    @Test
    @DisplayName("예약된 아이템 예약취소시 아이템 상태가 RESERVE가 아닐경우 리턴")
    public void cancelReserve_fail3() throws Exception {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .itemId(ID1)
                .itemOwnerUserId(ID2)
                .build();
        doThrow(new BusinessLogicException(ErrorCode.ITEM_STATUS_CONFLICT)).when(itemService).cancelReservedItem(any(), any());
        String content = objectMapper.writeValueAsString(requestCancelReserveItem);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/statuses")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then

        perform.andExpect(status().isConflict())
                .andDo(document("아이템_예약취소_실패3",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(headerWithName(JWT_HEADER).description("아이템을 예약했었던 유저 입니다.")),
                        requestFields(
                                fieldWithPath("itemId").description("아이템의 아이디 번호입니다."),
                                fieldWithPath("itemOwnerUserId").description("아이템 소유자 유저의 아이디 번호입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("예약된 아이템 예약취소시 아이템 예약자가 본인이 아닐경우 아닐경우 리턴")
    public void cancelReserve_fail4() throws Exception {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .itemId(ID1)
                .itemOwnerUserId(ID2)
                .build();
        doThrow(new BusinessLogicException(ErrorCode.USER_NOT_MATCH)).when(itemService).cancelReservedItem(any(), any());
        String content = objectMapper.writeValueAsString(requestCancelReserveItem);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/statuses")
                        .header(JWT_HEADER, JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then

        perform.andExpect(status().isForbidden())
                .andDo(document("아이템_예약취소_실패4",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(headerWithName(JWT_HEADER).description("아이템을 예약한 유저가 본인이 아닐경우 예외 발생 ")),
                        requestFields(
                                fieldWithPath("itemId").description("아이템의 아이디 번호입니다."),
                                fieldWithPath("itemOwnerUserId").description("아이템 소유자 유저의 아이디 번호입니다.")
                        )
                ));
    }


    @Test
    @DisplayName("아이템을 삭제시 204 No_Content 리턴")
    public void deleteItem_suc() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/items/{item-id}", ID1)
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isNoContent())
                .andDo(document("아이템_삭제_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(parameterWithName("item-id").description("삭제할 아이템의 아이디 입니다."))
                ));
    }

    @Test
    @DisplayName("아이템을 삭제시 아이템 상태가 POST가 아닐시 409 리턴")
    public void deleteItem_fail() throws Exception {
        //given
        doThrow(new BusinessLogicException(ErrorCode.ITEM_STATUS_CONFLICT)).when(itemService).deleteItemUser(any(), any());
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/items/{item-id}", ID1)
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("아이템_삭제_실패1",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(parameterWithName("item-id").description("삭제할 아이템의 상태가 POST가 아닐시 예외발생"))
                ));
    }

    @Test
    @DisplayName("아이템을 삭제시 아이템이 존재하지 않는 아이디일시 404 리턴")
    public void deleteItem_fail2() throws Exception {
        //given
        doThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND)).when(itemService).deleteItemUser(any(), any());
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/items/{item-id}", ID1)
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("아이템_삭제_실패2",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(parameterWithName("item-id").description("존재하지 않는 아이템 id일시 예외 발생"))
                ));
    }


    @Test
    @DisplayName("관심상품 아이템의 상태를 변경할떄 정확한 유저, 아이템 아이디를 전달할 경우 itemId와 200 리턴")
    public void changeItemInterest_suc() throws Exception {
        //given
        given(itemService.changeItemInterest(any(), any())).willReturn(RESPONSE_ITEM_ID);
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/items/interests/{item-id}", ID1)
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(ID1))
                .andDo(document("관심아이템_상태_변경_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(parameterWithName("item-id").description("관심상품으로 설정할 아이템 아이디 입니다.")),
                        responseFields(fieldWithPath("itemId").description("관심상품으로 변경된 아이템 아이디 입니다."))
                ));
    }

    @Test
    @DisplayName("관심상품 아이템의 상태를 변경할떄 정확하지 않은 유저, 아이템 아이디를 전달할 경우 ITEM_NOT_FOUND 404 리턴")
    public void changeItemInterest_fail() throws Exception {
        //given
        given(itemService.changeItemInterest(any(), any())).willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/items/interests/{item-id}", ID1)
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("관심아이템_상태_변경_실패",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(parameterWithName("item-id").description("존재하지 않는 아이템 아이디일시 예외 발생 "))
                ));
    }

    @Test
    @DisplayName("비밀 상품 아이템의 상태를 변경할떄 정확한 유저, 아이템 아이디를 전달할 경우 itemId와 200 리턴")
    public void changeItemSecret_suc() throws Exception {
        //given
        given(itemService.changeItemSecret(any(), any())).willReturn(RESPONSE_ITEM_ID);
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/items/secrets/{item-id}", ID1)
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(ID1))
                .andDo(document("비밀아이템_상태_변경_성공",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(parameterWithName("item-id").description("비공개 상품으로 설정할 아이템 아이디 입니다.")),
                        responseFields(fieldWithPath("itemId").description("비공개 상품으로 변경된 아이템 아이디 입니다."))
                ));
    }

    @Test
    @DisplayName("관심상품 아이템의 상태를 변경할떄 정확하지 않은 유저, 아이템 아이디를 전달할 경우 ITEM_NOT_FOUND 404 리턴")
    public void changeItemSecret_fail() throws Exception {
        //given
        given(itemService.changeItemSecret(any(), any())).willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/items/secrets/{item-id}", ID1)
                        .header(JWT_HEADER, JWT_TOKEN)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("비밀아이템_상태_변경_실패",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        getRequestHeadersSnippet(),
                        pathParameters(parameterWithName("item-id").description("존재하지 않는 아이템 아이디일시 예외 발생 "))
                ));
    }
}