package com.project.mything.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.exception.ExceptionController;
import com.project.mything.item.dto.ItemDto;
import com.project.mything.item.entity.enums.ItemStatus;
import com.project.mything.item.service.ItemService;
import com.project.mything.page.ResponseMultiPageDto;
import com.project.mything.user.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.project.mything.config.ApiDocumentUtils.getDocumentRequest;
import static com.project.mything.config.ApiDocumentUtils.getDocumentResponse;
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

        given(itemService.search(any(), any(), any(), any())).willReturn(new ResponseEntity<String>(
                content, HttpStatus.OK));
        //when
        ResultActions perform = mockMvc.perform(
                get("/items")
                        .param("query", "test")
                        .param("size", "10")
                        .param("sort", "sim")
                        .param("start", "1")
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
    public void saveItem_suc() throws Exception {
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
    public void searchItem_fail1() throws Exception {
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

    private static Stream<Arguments> notNullFieldValue() {
        return Stream.of(
                Arguments.of(null, "testLink", "testTitle", 1L, "imageLink", 1000),
                Arguments.of(1L, null, "testTitle", 1L, "imageLink", 1000),
                Arguments.of(1L, "testLink", null, 1L, "imageLink", 1000),
                Arguments.of(1L, "testLink", "testTitle", null, "imageLink", 1000),
                Arguments.of(1L, "testLink", "testTitle", 1L, null, 1000),
                Arguments.of(1L, "testLink", "testTitle", 1L, "imageLink", null)
        );
    }

    @Test
    @DisplayName("UserId 와 ItemId를 보냈을때 해당 아이디를 가지고있는 ItemUser가 존재할시 200 ResponseDetailItem객체를 리턴함")
    public void getDetailPage_suc() throws Exception {
        //given
        ItemDto.ResponseDetailItem responseDetailItem = ItemDto.ResponseDetailItem.builder()
                .itemId(1L)
                .title("타이틀")
                .link("링크")
                .price(1000)
                .image("이미지 링크")
                .memo("test Memo")
                .interestedItem(false)
                .secretItem(false)
                .itemStatus(ItemStatus.POST)
                .build();
        given(itemService.getDetailItem(any(), any())).willReturn(responseDetailItem);
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/items/{item-id}/users/{user-id}", 1L, 1L)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("itemId").value(responseDetailItem.getItemId()))
                .andExpect(jsonPath("title").value(responseDetailItem.getTitle()))
                .andExpect(jsonPath("link").value(responseDetailItem.getLink()))
                .andExpect(jsonPath("price").value(responseDetailItem.getPrice()))
                .andExpect(jsonPath("image").value(responseDetailItem.getImage()))
                .andExpect(jsonPath("memo").value(responseDetailItem.getMemo()))
                .andExpect(jsonPath("interestedItem").value(responseDetailItem.getInterestedItem()))
                .andExpect(jsonPath("secretItem").value(responseDetailItem.getSecretItem()))
                .andExpect(jsonPath("itemStatus").value(responseDetailItem.getItemStatus().toString()))
                .andDo(document("아이템_상세조회_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                List.of(
                                        parameterWithName("item-id").description("아이템 아이디 입니다."),
                                        parameterWithName("user-id").description("유저 아이디 입니다.")
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                        fieldWithPath("title").description("아이템 이름입니다."),
                                        fieldWithPath("link").description("아이템 네이버 주소입니다."),
                                        fieldWithPath("price").description("아이템 가격입니다."),
                                        fieldWithPath("image").description("아이템 이미지 주소입니다."),
                                        fieldWithPath("memo").description("아이템에 대한 짧은 메모입니다."),
                                        fieldWithPath("interestedItem").description("관심있는 아이템 유/무 입니다."),
                                        fieldWithPath("secretItem").description("비밀 아이템 유/무 입니다."),
                                        fieldWithPath("itemStatus").description("아이템 상태입니다.")
                                )
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
                RestDocumentationRequestBuilders.get("/items/{item-id}/users/{user-id}", 1L, 1L)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("아이템_상세조회시_존재하지_않는_아이템_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                List.of(
                                        parameterWithName("item-id").description("아이템 아이디 입니다."),
                                        parameterWithName("user-id").description("유저 아이디 입니다.")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("리스트로 본인의 아이템을 조회할때 성공시 200과 ResponseSimpleItem를 리스트로 전달받는다.")
    public void getSimpleItems_suc() throws Exception {
        //given
        List<ItemDto.ResponseSimpleItem> data = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Boolean value;
            ItemStatus itemStatus;
            if (i % 2 == 0) {
                value = Boolean.FALSE;
                itemStatus = ItemStatus.BOUGHT;
            } else {
                value = Boolean.TRUE;
                itemStatus = ItemStatus.POST;
            }
            data.add(ItemDto.ResponseSimpleItem.builder()
                    .itemId((long) i)
                    .itemStatus(itemStatus)
                    .secretItem(value)
                    .interestedItem(value)
                    .title("test")
                    .createdAt(LocalDateTime.now())
                    .lastModifiedAt(LocalDateTime.now())
                    .image("imageLInk")
                    .price(1000)
                    .build());
        }
        UserDto.ResponseSimpleUser responseSimpleUser = UserDto.ResponseSimpleUser.builder()
                .userId(1L)
                .name("홍길동")
                .image("remote image")
                .build();
        PageRequest pageable = PageRequest.of(0, 5, Sort.by("itemStatus").descending());
        Page<ItemDto.ResponseSimpleItem> responseSimpleItems = new PageImpl<>(data, pageable, 5);
        ResponseMultiPageDto<ItemDto.ResponseSimpleItem> responseMultiPageDto =
                new ResponseMultiPageDto<ItemDto.ResponseSimpleItem>(data, responseSimpleItems, responseSimpleUser);

        given(itemService.getSimpleItems(any(), any(), any(), any())).willReturn(responseMultiPageDto);
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/items/users/{user-id}", 1)
                        .param("isFriend", "false")
                        .param("start", "1")
                        .param("size", "5")
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.user.userId").value(1))
                .andExpect(jsonPath("$.user.name").value("홍길동"))
                .andExpect(jsonPath("$.user.image").value("remote image"))
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(5))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(5))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1))
                .andDo(document("본인의_아이템_리스트_조회_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("user-id").description("아이템을 가져올 유저의 아이디값입니다.")
                        ),
                        requestParameters(
                                List.of(
                                        parameterWithName("isFriend").description("필수값이 아닙니다. 디폴트값 false, 친구의 아이템 리스트 조회시 true를 작성해주세요."),
                                        parameterWithName("start").description("현재 보여질 페이지 번호입니다."),
                                        parameterWithName("size").description("한페이지에 보여질 게시글의 갯수입니다.")
                                )
                        ),
                        responseFields(
                                List.of(
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
                                        fieldWithPath("user.userId").type(JsonFieldType.NUMBER).description("아이템을 가지고 있는 유저의 아이디입니다."),
                                        fieldWithPath("user.name").type(JsonFieldType.STRING).description("아이템을 가지고 있는 유저의 이름입니다."),
                                        fieldWithPath("user.image").type(JsonFieldType.STRING).description("아이템을 가지고 있는 유저의 이미지 주소입니다."),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 보여질 페이지 입니다."),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("한페이지에 들어갈 게시글의 갯수 입니다."),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 게시글의 갯수 입니다."),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 숫자입니다.")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("리스트로 친구의 아이템을 조회할때 성공시 200과 ResponseSimpleItem를 리스트로 전달받는다.")
    public void getSimpleItems_suc2() throws Exception {
        //given
        List<ItemDto.ResponseSimpleItem> data = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            Boolean value;
            ItemStatus itemStatus;
            if (i % 2 == 0) {
                value = Boolean.FALSE;
                itemStatus = ItemStatus.BOUGHT;
            } else {
                value = Boolean.TRUE;
                itemStatus = ItemStatus.POST;
            }
            data.add(ItemDto.ResponseSimpleItem.builder()
                    .itemId((long) i)
                    .itemStatus(itemStatus)
                    .secretItem(false)
                    .interestedItem(value)
                    .title("test")
                    .createdAt(LocalDateTime.now())
                    .lastModifiedAt(LocalDateTime.now())
                    .image("imageLInk")
                    .price(1000)
                    .build());
        }
        UserDto.ResponseSimpleUser responseSimpleUser = UserDto.ResponseSimpleUser.builder()
                .userId(1L)
                .name("홍길동")
                .image("remote image")
                .build();
        PageRequest pageable = PageRequest.of(0, 3, Sort.by("itemStatus").descending());
        Page<ItemDto.ResponseSimpleItem> responseSimpleItems = new PageImpl<>(data, pageable, 3);
        ResponseMultiPageDto<ItemDto.ResponseSimpleItem> responseMultiPageDto =
                new ResponseMultiPageDto<ItemDto.ResponseSimpleItem>(data, responseSimpleItems, responseSimpleUser);

        given(itemService.getSimpleItems(any(), any(), any(), any())).willReturn(responseMultiPageDto);
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/items/users/{user-id}", 1)
                        .param("isFriend", "true")
                        .param("start", "1")
                        .param("size", "3")
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.user.userId").value(1))
                .andExpect(jsonPath("$.user.name").value("홍길동"))
                .andExpect(jsonPath("$.user.image").value("remote image"))
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(3))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(3))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1))
                .andDo(document("친구의_아이템_리스트_조회_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("user-id").description("아이템을 가져올 유저의 아이디값입니다.")
                        ),
                        requestParameters(
                                List.of(
                                        parameterWithName("isFriend").description("필수값이 아닙니다. 디폴트값 false, 친구의 아이템 리스트 조회시 true를 작성해주세요."),
                                        parameterWithName("start").description("현재 보여질 페이지 번호입니다."),
                                        parameterWithName("size").description("한페이지에 보여질 게시글의 갯수입니다.")
                                )
                        ),
                        responseFields(
                                List.of(
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
                                        fieldWithPath("user.userId").type(JsonFieldType.NUMBER).description("아이템을 가지고 있는 유저의 아이디입니다."),
                                        fieldWithPath("user.name").type(JsonFieldType.STRING).description("아이템을 가지고 있는 유저의 이름입니다."),
                                        fieldWithPath("user.image").type(JsonFieldType.STRING).description("아이템을 가지고 있는 유저의 이미지 주소입니다."),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 보여질 페이지 입니다."),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("한페이지에 들어갈 게시글의 갯수 입니다."),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 게시글의 갯수 입니다."),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 숫자입니다.")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("리스트로 아이템 조회시 존재하지 않는 유저 아이디로 요청을 보낼때")
    public void getSimpleItems_fail() throws Exception {
        //given

        given(itemService.getSimpleItems(any(), any(), any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/items/users/{user-id}", -1)
                        .param("isFriend", "false")
                        .param("start", "1")
                        .param("size", "5")
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("아이템_리스트_조회_존재하지_않는_유저아이디로_요청_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("user-id").description("아이템을 가져올 유저의 아이디값입니다.")
                        ),
                        requestParameters(
                                List.of(
                                        parameterWithName("isFriend").description("친구의 리스트라면 true, 본인의 리스트를 확인한다면 false를 전달해주세요"),
                                        parameterWithName("start").description("현재 보여질 페이지 번호입니다."),
                                        parameterWithName("size").description("한페이지에 보여질 게시글의 갯수입니다.")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("파라미터를 작성하지 않고 요청을 보낼때")
    public void getSimpleItems_fail2() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/items/users/{user-id}", -1)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("아이템_리스트_조회_파라미터를_작성하지_않고_요청_400",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("user-id").description("아이템을 가져올 유저의 아이디값입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 BOUGHT로 변경할때 성공")
    public void changeItemStatus_suc() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(1L)
                .itemStatus(ItemStatus.BOUGHT)
                .build();
        ItemDto.ResponseItemId responseItemId = ItemDto.ResponseItemId.builder()
                .itemId(1L)
                .build();
        String content = objectMapper.writeValueAsString(requestChangeItemStatus);
        given(itemService.changeItemStatus(any(), any())).willReturn(responseItemId);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1L))
                .andDo(document("아이템_상태_BOUGHT로_변경_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템 소유자 유저 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                        fieldWithPath("itemStatus").description("변경하고싶은 아이템 상태명 입니다. (대문자)")
                                )
                        ),
                        responseFields(
                                fieldWithPath("itemId").description("변경된 아이템의 아이디 입니다.")
                        )));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 RECEIVED로 변경할때 성공")
    public void changeItemStatus_suc2() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(1L)
                .itemStatus(ItemStatus.RECEIVED)
                .build();
        ItemDto.ResponseItemId responseItemId = ItemDto.ResponseItemId.builder()
                .itemId(1L)
                .build();
        String content = objectMapper.writeValueAsString(requestChangeItemStatus);
        given(itemService.changeItemStatus(any(), any())).willReturn(responseItemId);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1L))
                .andDo(document("아이템_상태_RECEIVED로_변경_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템 소유자 유저 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                        fieldWithPath("itemStatus").description("변경하고싶은 아이템 상태명 입니다. (대문자)")
                                )
                        ),
                        responseFields(
                                fieldWithPath("itemId").description("변경된 아이템의 아이디 입니다.")
                        )));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 RESERVE로 변경할때 성공")
    public void changeItemStatus_suc3() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(1L)
                .itemStatus(ItemStatus.RESERVE)
                .build();
        ItemDto.ResponseItemId responseItemId = ItemDto.ResponseItemId.builder()
                .itemId(1L)
                .build();
        String content = objectMapper.writeValueAsString(requestChangeItemStatus);
        given(itemService.changeItemStatus(any(), any())).willReturn(responseItemId);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("reservedId", 2L)
                        .content(content)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1L))
                .andDo(document("아이템_상태_RESERVE로_변경_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("reservedId").description("예약하는 유저의 아이디 번호입니다. 예약시에만 필요함")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템 소유자 유저 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                        fieldWithPath("itemStatus").description("변경하고싶은 아이템 상태명 입니다. (대문자)")
                                )
                        ),
                        responseFields(
                                fieldWithPath("itemId").description("변경된 아이템의 아이디 입니다.")
                        )));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 POST로 변경할때 성공")
    public void changeItemStatus_suc4() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(1L)
                .itemStatus(ItemStatus.RESERVE)
                .build();
        ItemDto.ResponseItemId responseItemId = ItemDto.ResponseItemId.builder()
                .itemId(1L)
                .build();
        String content = objectMapper.writeValueAsString(requestChangeItemStatus);
        given(itemService.changeItemStatus(any(), any())).willReturn(responseItemId);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1L))
                .andDo(document("아이템_상태_POST로_변경_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템 소유자 유저 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                        fieldWithPath("itemStatus").description("변경하고싶은 아이템 상태명 입니다. (대문자)")
                                )
                        ),
                        responseFields(
                                fieldWithPath("itemId").description("변경된 아이템의 아이디 입니다.")
                        )));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 존재하지 않는 유저의 아이템을 변경하려 할때 ITEM_NOT_FOUND 404 리턴")
    public void changeItemStatus_fail1() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(10000L)
                .itemId(1L)
                .itemStatus(ItemStatus.RESERVE)
                .build();
        String content = objectMapper.writeValueAsString(requestChangeItemStatus);
        given(itemService.changeItemStatus(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("존재하지_않는_유저가_아이템_상태_변경_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템 소유자 유저 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                        fieldWithPath("itemStatus").description("변경하고싶은 아이템 상태명 입니다. (대문자)")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 존재하지 않는 아이템의 상태를 변경하려 할때 ITEM_NOT_FOUND 404 리턴")
    public void changeItemStatus_fail2() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(10000L)
                .itemStatus(ItemStatus.RESERVE)
                .build();
        String content = objectMapper.writeValueAsString(requestChangeItemStatus);
        given(itemService.changeItemStatus(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("존재하지_않는_아이템의_상태_변경_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템 소유자 유저 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                        fieldWithPath("itemStatus").description("변경하고싶은 아이템 상태명 입니다. (대문자)")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("아이템의 상태를 변경할때 요청값이 잘못되었을때 Bad_Request 400 리턴")
    public void changeItemStatus_fail3() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(-1L)
                .itemId(-1L)
                .itemStatus(ItemStatus.POST)
                .build();
        String content = objectMapper.writeValueAsString(requestChangeItemStatus);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document("요청값이_잘못되었을때_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("값이 양수이어야합니다. 값이 널이어서는 안됩니다."),
                                        fieldWithPath("itemId").description("값이 양수이어야합니다. 값이 널이어서는 안됩니다."),
                                        fieldWithPath("itemStatus").description("BOUGHT, RESERVE, RECEIVE 외에는 허용하지 않습니다.")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 이미 아이템 상태가 RESERVE 일때 ITEM_ALREADY_RESERVED 409 리턴")
    public void changeItemStatus_fail4() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(2L)
                .itemStatus(ItemStatus.RESERVE)
                .build();
        String content = objectMapper.writeValueAsString(requestChangeItemStatus);
        given(itemService.changeItemStatus(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_ALREADY_RESERVED));
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("reservedId", 2L)
                        .content(content)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("아이템의_상태가_이미_RESERVE상태_변경_실패_409",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("reservedId").description("예약하는 유저의 아이디 번호입니다. 예약시에만 필요함")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템 소유자 유저 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                        fieldWithPath("itemStatus").description("변경하고싶은 아이템 상태명 입니다. (대문자)")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 존재하지 않는 유저가 예약하려 할때 User_Not_Found 404 리턴")
    public void changeItemStatus_fail5() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(2L)
                .itemStatus(ItemStatus.RESERVE)
                .build();
        String content = objectMapper.writeValueAsString(requestChangeItemStatus);
        given(itemService.changeItemStatus(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("reservedId", 50000L)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("존재하지_않는_예약자가_아이템상태_RESERVE로_변경_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("reservedId").description("예약하는 유저의 아이디 번호입니다. 예약시에만 필요함")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템 소유자 유저 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                        fieldWithPath("itemStatus").description("변경하고싶은 아이템 상태명 입니다. (대문자)")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("아이템 상태를 변경시 아이템 소유자의 유저아이디와 예약자의 유저아이디가 동일할 경우 RESERVE_USER_CONFLICT 409 리턴")
    public void changeItemStatus_fail6() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(2L)
                .itemStatus(ItemStatus.RESERVE)
                .build();
        String content = objectMapper.writeValueAsString(requestChangeItemStatus);
        given(itemService.changeItemStatus(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.RESERVE_USER_CONFLICT));
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("reservedId", 1L)
                        .content(content)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("아이템_소유_유저아이디와_예약_유저아이디가_동일_실패_409",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("reservedId").description("예약하는 유저의 아이디 번호입니다. 예약시에만 필요함")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템 소유자 유저 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                        fieldWithPath("itemStatus").description("변경하고싶은 아이템 상태명 입니다. (대문자)")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("아이템 상태를 POST로 변경하려할시 POST_NOT_ALLOW 409 리턴")
    public void changeItemStatus_fail7() throws Exception {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(2L)
                .itemStatus(ItemStatus.POST)
                .build();
        String content = objectMapper.writeValueAsString(requestChangeItemStatus);
        given(itemService.changeItemStatus(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.POST_NOT_ALLOW));
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("아이템상태를_POST로_변경_시도_실패_409",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템 소유자 유저 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템 아이디 입니다."),
                                        fieldWithPath("itemStatus").description("변경하고싶은 아이템 상태명 입니다. (대문자)")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("예약된 아이템 예약취소시 정확한 정보를 전달할 경우 204 No Content 리턴")
    public void cancelReserve_suc() throws Exception {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .userId(1L)
                .itemId(1L)
                .reservedId(2L)
                .build();
        String content = objectMapper.writeValueAsString(requestCancelReserveItem);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then

        perform.andExpect(status().isNoContent())
                .andDo(document("예약된_아이템_예약취소_성공_204",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관한 유저의 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템의 아이디 번호입니다."),
                                        fieldWithPath("reservedId").description("예약했었던 유저의 아이디 번호입니다.")
                                )
                        )));

        verify(itemService, times(1)).cancelReservedItem(any());
    }

    @Test
    @DisplayName("예약된 아이템 예약취소시 요청 값이 누락되었을 경우 400 Bad_Request 리턴")
    public void cancelReserve_fail() throws Exception {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .userId(1L)
                .itemId(1L)
                .build();
        String content = objectMapper.writeValueAsString(requestCancelReserveItem);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then

        perform.andExpect(status().isBadRequest())
                .andDo(document("예약된_아이템_예약취소시_요청값을_누락할_경우_400",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관한 유저의 아이디 입니다. 필수값입니다."),
                                        fieldWithPath("itemId").description("아이템의 아이디 번호입니다. 필수값입니다."),
                                        fieldWithPath("reservedId").description("예약했었던 유저의 아이디 번호입니다. 필수값입니다.")
                                )
                        )));
    }

    @Test
    @DisplayName("예약된 아이템 예약취소시 요청 값이 음수일 경우 400 Bad_Request 리턴")
    public void cancelReserve_fail2() throws Exception {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .userId(-1L)
                .itemId(-1L)
                .reservedId(-1L)
                .build();
        String content = objectMapper.writeValueAsString(requestCancelReserveItem);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then

        perform.andExpect(status().isBadRequest())
                .andDo(document("예약된_아이템_예약취소시_요청값이_음수일_경우_400",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관한 유저의 아이디 입니다. 양수이어야합니다.."),
                                        fieldWithPath("itemId").description("아이템의 아이디 번호입니다. 양수이어야합니다."),
                                        fieldWithPath("reservedId").description("예약했었던 유저의 아이디 번호입니다. 양수이어야합니다.")
                                )
                        )));
    }

    @Test
    @DisplayName("예약된 아이템 예약취소시 존재하지 않는 reservedId를 전달할 경우 USER_NOT_FOUND 404 리턴")
    public void cancelReserve_fail3() throws Exception {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .userId(1L)
                .itemId(1L)
                .reservedId(5000L)
                .build();
        String content = objectMapper.writeValueAsString(requestCancelReserveItem);
        doThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND)).when(itemService).cancelReservedItem(any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("예약된_아이템_예약취소시_존재하지_않는_reservedId를_전달할_경우_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관한 유저의 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템의 아이디 번호입니다."),
                                        fieldWithPath("reservedId").description("예약했었던 유저의 아이디 번호입니다.")
                                )
                        )));
    }

    @Test
    @DisplayName("예약된 아이템 예약취소시 존재하지 않는 itemId를 전달할 경우 ITEM_NOT_FOUND 404 리턴")
    public void cancelReserve_fail4() throws Exception {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .userId(1L)
                .itemId(5000L)
                .reservedId(2L)
                .build();
        String content = objectMapper.writeValueAsString(requestCancelReserveItem);
        doThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND)).when(itemService).cancelReservedItem(any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("예약된_아이템_예약취소시_존재하지_않는_itemId를_전달할_경우_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관한 유저의 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템의 아이디 번호입니다."),
                                        fieldWithPath("reservedId").description("예약했었던 유저의 아이디 번호입니다.")
                                )
                        )));
    }

    @Test
    @DisplayName("예약된 아이템 예약취소시 존재하지 않는 userId를 전달할 경우 USER_NOT_FOUND 404 리턴")
    public void cancelReserve_fail5() throws Exception {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .userId(5000L)
                .itemId(1L)
                .reservedId(2L)
                .build();
        String content = objectMapper.writeValueAsString(requestCancelReserveItem);
        doThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND)).when(itemService).cancelReservedItem(any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("예약된_아이템_예약취소시_존재하지_않는_userId를_전달할_경우_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관한 유저의 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템의 아이디 번호입니다."),
                                        fieldWithPath("reservedId").description("예약했었던 유저의 아이디 번호입니다.")
                                )
                        )));
    }

    @Test
    @DisplayName("예약된 아이템 예약취소시 아이템의 상태가 RESERVE가 아닐 경우 ITEM_STATUS_NOT_RESERVE 409 리턴")
    public void cancelReserve_fail6() throws Exception {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .userId(1L)
                .itemId(1L)
                .reservedId(2L)
                .build();
        String content = objectMapper.writeValueAsString(requestCancelReserveItem);
        doThrow(new BusinessLogicException(ErrorCode.ITEM_STATUS_NOT_RESERVE))
                .when(itemService).cancelReservedItem(any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("예약된_아이템_예약취소시_아이템의_상태가_RESERVE가_아님_실패_409",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관한 유저의 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템의 아이디 번호입니다."),
                                        fieldWithPath("reservedId").description("예약했었던 유저의 아이디 번호입니다.")
                                )
                        )));
    }

    @Test
    @DisplayName("예약된 아이템 예약취소시 기존 reservedId 와 요청 reservedId가 다를 경우 USER_NOT_MATCH 403 리턴")
    public void cancelReserve_fail7() throws Exception {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .userId(1L)
                .itemId(1L)
                .reservedId(2L)
                .build();
        String content = objectMapper.writeValueAsString(requestCancelReserveItem);
        doThrow(new BusinessLogicException(ErrorCode.USER_NOT_MATCH))
                .when(itemService).cancelReservedItem(any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isForbidden())
                .andDo(document("예약된_아이템_예약취소시_기존_reservedId와_요청_reservedId가_다름_실패_403",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관한 유저의 아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템의 아이디 번호입니다."),
                                        fieldWithPath("reservedId").description("예약했었던 유저의 아이디 번호입니다.")
                                )
                        )));
    }

    @Test
    @DisplayName("사용자가 보관중인 POST상태인 아이템을 삭제시 204 No_Content 리턴")
    public void deleteItem_suc() throws Exception {
        //given
        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .userId(1L)
                .itemId(1L)
                .build();
        String content = objectMapper.writeValueAsString(requestSimpleItem);
        doNothing().when(itemService).deleteItemUser(requestSimpleItem);
        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/storages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNoContent())
                .andDo(document("POST_상태인_아이템_삭제시_성공_204",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관함에 갖고있는 유저아이디 입니다."),
                                        fieldWithPath("itemId").description("해당 아이템의 아이디 입니다.")
                                )
                        )));
    }

    @Test
    @DisplayName("사용자가 보관중인 아이템 삭제시 잘못된 유저 아이디를 보낼경우 Item_Not_Found 404 리턴 리턴")
    public void deleteItem_fail() throws Exception {
        //given
        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .userId(5000L)
                .itemId(1L)
                .build();
        String content = objectMapper.writeValueAsString(requestSimpleItem);
        doThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND))
                .when(itemService).deleteItemUser(any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/storages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("아이템_삭제시_잘못된_유저_아이디_전달_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관함에 갖고있는 유저아이디 입니다. 필수값입니다."),
                                        fieldWithPath("itemId").description("해당 아이템의 아이디 입니다. 필수값입니다.")
                                )
                        )));
    }

    @Test
    @DisplayName("사용자가 보관중인 아이템 삭제시 잘못된 아이템 아이디를 보낼경우 Item_Not_Found 404 리턴 리턴")
    public void deleteItem_fail2() throws Exception {
        //given
        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .userId(1L)
                .itemId(5000L)
                .build();
        String content = objectMapper.writeValueAsString(requestSimpleItem);
        doThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND))
                .when(itemService).deleteItemUser(any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/storages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("아이템_삭제시_잘못된_아이템_아이디_전달_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관함에 갖고있는 유저아이디 입니다. 필수값입니다."),
                                        fieldWithPath("itemId").description("해당 아이템의 아이디 입니다. 필수값입니다.")
                                )
                        )));
    }

    @Test
    @DisplayName("사용자가 보관중인 POST가 아닌 아이템을 삭제시 409 Item_Status_Not_Post 리턴")
    public void deleteItem_fail3() throws Exception {
        //given
        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .userId(1L)
                .itemId(1L)
                .build();
        String content = objectMapper.writeValueAsString(requestSimpleItem);
        doThrow(new BusinessLogicException(ErrorCode.ITEM_STATUS_NOT_POST))
                .when(itemService).deleteItemUser(any());
        //when
        ResultActions perform = mockMvc.perform(
                delete("/items/storages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isConflict())
                .andDo(document("POST_상태가_아닌_아이템_삭제시_실패_409",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관함에 갖고있는 유저아이디 입니다. 필수값입니다."),
                                        fieldWithPath("itemId").description("해당 아이템의 아이디 입니다. 필수값입니다.")
                                )
                        )));
    }

    @Test
    @DisplayName("관심상품 아이템의 상태를 변경할떄 정확한 유저, 아이템 아이디를 전달할 경우 itemId와 200 리턴")
    public void changeItemInterest_suc() throws Exception {
        //given
        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .itemId(1L)
                .userId(1L)
                .build();
        ItemDto.ResponseItemId responseItemId = ItemDto.ResponseItemId.builder()
                .itemId(1L)
                .build();
        String content = objectMapper.writeValueAsString(requestSimpleItem);
        given(itemService.changeItemInterest(any())).willReturn(responseItemId);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/interests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1))
                .andDo(document("관심아이템_상태_변경_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관중이 유저아이디 입니다. 필수값입니다."),
                                        fieldWithPath("itemId").description("아이템의 아이디 입니다. 필수값입니다.")
                                )
                        ),
                        responseFields(
                                fieldWithPath("itemId").description("관심아이템 상태가 변경된 아이템 아이디 입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("관심상품 아이템의 상태를 변경할떄 정확하지 않은 유저, 아이템 아이디를 전달할 경우 ITEM_NOT_FOUND 404 리턴")
    public void changeItemInterest_fail() throws Exception {
        //given
        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .itemId(1L)
                .userId(1L)
                .build();

        String content = objectMapper.writeValueAsString(requestSimpleItem);
        given(itemService.changeItemInterest(any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/interests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("관심아이템_상태_변경_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관중이 유저아이디 입니다. 정확한 값 필요"),
                                        fieldWithPath("itemId").description("아이템의 아이디 입니다. 정확한 값 필요")
                                )
                        )
                ));
    }


    @Test
    @DisplayName("비밀 아이템의 상태를 변경할떄 정확한 유저, 아이템 아이디를 전달할 경우 itemId와 200 리턴")
    public void changeItemSecret_suc() throws Exception {
        //given
        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .itemId(1L)
                .userId(1L)
                .build();
        ItemDto.ResponseItemId responseItemId = ItemDto.ResponseItemId.builder()
                .itemId(1L)
                .build();
        String content = objectMapper.writeValueAsString(requestSimpleItem);
        given(itemService.changeItemSecret(any())).willReturn(responseItemId);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/secrets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1))
                .andDo(document("비밀아이템_상태_변경_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관중이 유저아이디 입니다."),
                                        fieldWithPath("itemId").description("아이템의 아이디 입니다.")
                                )
                        ),
                        responseFields(
                                fieldWithPath("itemId").description("관심아이템 상태가 변경된 아이템 아이디 입니다.")
                        )
                ));

    }

    @Test
    @DisplayName("비밀상품 아이템의 상태를 변경할떄 정확하지 않은 유저, 아이템 아이디를 전달할 경우 ITEM_NOT_FOUND 404 리턴")
    public void changeItemSecret_fail() throws Exception {
        //given
        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .itemId(1L)
                .userId(1L)
                .build();

        String content = objectMapper.writeValueAsString(requestSimpleItem);
        given(itemService.changeItemSecret(any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                patch("/items/secrets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("비밀아이템_상태_변경_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userId").description("아이템을 보관중이 유저아이디 입니다. 정확한 값 필요"),
                                        fieldWithPath("itemId").description("아이템의 아이디 입니다. 정확한 값 필요")
                                )
                        )
                ));
    }
}