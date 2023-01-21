package com.project.mything.item.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.item.dto.ItemDto;
import com.project.mything.item.entity.Item;
import com.project.mything.item.entity.ItemUser;
import com.project.mything.item.mapper.ItemMapper;
import com.project.mything.item.repository.ItemRepository;
import com.project.mything.item.repository.ItemUserRepository;
import com.project.mything.user.entity.User;
import com.project.mything.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith({MockitoExtension.class})
class ItemServiceTest {
    @InjectMocks
    ItemService itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemMapper itemMapper;
    @Mock
    ItemUserRepository itemUserRepository;
    @Mock
    UserService userService;
    @Mock
    NAVERApiService naverApiService;


    @Test
    @DisplayName("검색 API 사용시 query가 널값이라면 200 INCORRECT_QUERY_REQUEST 400을 리턴한다.")
    public void search_fail() {
        //given
        String query = "";
        Integer size = 10;
        String sort = "sim";

        //when
        //then
        assertThatThrownBy(() -> itemService.search(query, size, sort)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("검색 API 사용시 성공하면 ResponseEntity<String>를 리턴한다.")
    public void search_suc() {
        //given
        String query = "테스트";
        Integer size = 10;
        String sort = "sim";

        given(naverApiService.searchItem(any(), any(), any())).willReturn(new ResponseEntity<String>("body", HttpStatus.OK));
        //when
        ResponseEntity<String> search = itemService.search(query, size, sort);
        //then
        assertThat(search.getBody()).isEqualTo("body");
        assertThat(search.getStatusCodeValue()).isEqualTo(200);
        assertThat(search.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("아이템 저장시 성공하면 ItemDto.ResponseItemId를 리턴한다.")
    public void saveItem_suc() {
        //given
        ItemDto.RequestSaveItem requestSaveItem = ItemDto.RequestSaveItem.builder()
                .userId(1L)
                .link("링크")
                .title("타이틀")
                .price(1000)
                .image("이미지 링크")
                .productId(1111L)
                .build();

        User dbUser = User.builder()
                .id(1L)
                .name("홍길동")
                .build();

        Item dbItem = Item.builder()
                .id(1L)
                .link("링크")
                .title("타이틀")
                .price(1000)
                .image("이미지 링크")
                .productId(1111L)
                .build();

        ItemUser dbItemUser = ItemUser
                .builder()
                .user(dbUser)
                .item(dbItem)
                .id(1L)
                .build();
        given(userService.findVerifiedUser(any())).willReturn(dbUser);
        given(itemMapper.toItem(any())).willReturn(dbItem);
        given(itemRepository.save(any())).willReturn(dbItem);
        given(itemUserRepository.save(any())).willReturn(dbItemUser);
        given(itemMapper.toResponseItemId(any())).willReturn(ItemDto.ResponseItemId.builder().itemId(1L).build());
        //when
        ItemDto.ResponseItemId responseItemId = itemService.saveItem(requestSaveItem);
        //then
        assertThat(responseItemId.getItemId()).isEqualTo(1L);
        verify(itemUserRepository, times(1)).findItemUserByUserIdAndProductId(any(),any());
    }

    @Test
    @DisplayName("아이템 저장시 이미 존재하는 아이템을 유저가 추가하려고하면 Item_Exist 409 CONFLICT 리턴된다.")
    public void saveItem_fail() {
        //given
        ItemDto.RequestSaveItem requestSaveItem = ItemDto.RequestSaveItem.builder()
                .userId(1L)
                .link("링크")
                .title("타이틀")
                .price(1000)
                .image("이미지 링크")
                .productId(1111L)
                .build();
        given(itemUserRepository.findItemUserByUserIdAndProductId(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_EXISTS));
        //when
        //then
        assertThatThrownBy(() -> itemService.saveItem(requestSaveItem)).isInstanceOf(BusinessLogicException.class);
    }

}
