package com.project.mything.item.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.item.dto.ItemDto;
import com.project.mything.item.entity.Item;
import com.project.mything.item.entity.ItemUser;
import com.project.mything.item.entity.enums.ItemStatus;
import com.project.mything.item.mapper.ItemMapper;
import com.project.mything.item.repository.ItemRepository;
import com.project.mything.item.repository.ItemUserRepository;
import com.project.mything.page.ResponseMultiPageDto;
import com.project.mything.user.entity.User;
import com.project.mything.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Integer start = 1;

        //when
        //then
        assertThatThrownBy(() -> itemService.search(query, size, sort, start)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("검색 API 사용시 성공하면 ResponseEntity<String>를 리턴한다.")
    public void search_suc() {
        //given
        String query = "테스트";
        Integer size = 10;
        String sort = "sim";
        Integer start = 1;

        given(naverApiService.searchItem(any(), any(), any(), any())).willReturn(new ResponseEntity<String>("body", HttpStatus.OK));
        //when
        ResponseEntity<String> search = itemService.search(query, size, sort, start);
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
        verify(itemUserRepository, times(1)).findItemUserByUserIdAndProductId(any(), any());
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

    @Test
    @DisplayName("아이템 상세정보 구하기 성공 테스트")
    public void getDetailItem_suc() {
        //given
        Item dbItem = Item.builder()
                .id(1L)
                .link("링크")
                .title("타이틀")
                .price(1000)
                .image("이미지 링크")
                .build();

        User dbUser = User.builder()
                .id(1L)
                .build();

        ItemUser dbItemUser = ItemUser.builder()
                .id(1L)
                .user(dbUser)
                .item(dbItem)
                .memo("test Memo")
                .build();

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

        given(itemUserRepository.findItemUserByUserIdAndItemId(dbUser.getId(), dbItem.getId()))
                .willReturn(Optional.of(dbItemUser));
        given(itemMapper.toResponseDetailItem(dbItemUser, dbItemUser.getItem())).willReturn(responseDetailItem);
        //when
        ItemDto.ResponseDetailItem detailItem = itemService.getDetailItem(dbItem.getId(), dbUser.getId());
        //then
        assertThat(detailItem.getItemId()).isEqualTo(dbItem.getId());
        assertThat(detailItem.getLink()).isEqualTo(dbItem.getLink());
        assertThat(detailItem.getTitle()).isEqualTo(dbItem.getTitle());
        assertThat(detailItem.getPrice()).isEqualTo(dbItem.getPrice());
        assertThat(detailItem.getImage()).isEqualTo(dbItem.getImage());
        assertThat(detailItem.getMemo()).isEqualTo(dbItemUser.getMemo());
        assertThat(detailItem.getInterestedItem()).isEqualTo(dbItemUser.getInterestedItem());
        assertThat(detailItem.getSecretItem()).isEqualTo(dbItemUser.getSecretItem());
        assertThat(detailItem.getItemStatus()).isEqualTo(dbItemUser.getItemStatus());
    }

    @Test
    @DisplayName("아이템 상세정보 구할때 유저아이디와 아이템아이디로 된 ItemUser객체가 존재하지않을때 404 Item_Not_Found 실패 테스트")
    public void getDetailItem_fail() {
        //given
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> itemService.getDetailItem(any(), any())).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("아이템을 리스트로 가져오면 ResponseMultiPageDto를 리턴한다.")
    public void getSimpleItems_suc() {
        //given
        List<ItemDto.ResponseSimpleItem> data = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Boolean value;
            ItemStatus itemStatus;
            if (i % 2 == 0) {
                value = false;
                itemStatus = ItemStatus.BOUGHT;
            } else {
                value = true;
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
        PageRequest pageable = PageRequest.of(0, 5, Sort.by("itemStatus").descending());
        Page<ItemDto.ResponseSimpleItem> responseSimpleItems = new PageImpl<>(data, pageable, 5);
        ResponseMultiPageDto<ItemDto.ResponseSimpleItem> responseMultiPageDto = new ResponseMultiPageDto<ItemDto.ResponseSimpleItem>(data, responseSimpleItems);

        given(userService.findVerifiedUser(any())).willReturn(User.builder().build());
        given(itemUserRepository.searchSimpleItem(any(), any())).willReturn(responseSimpleItems);
        //when
        ResponseMultiPageDto<ItemDto.ResponseSimpleItem> result = itemService.getSimpleItems(1L, 1, 5);
        //then
        assertThat(result.getData()).isEqualTo(responseMultiPageDto.getData());
        assertThat(result.getPageInfo().getPage()).isEqualTo(responseMultiPageDto.getPageInfo().getPage());
        assertThat(result.getPageInfo().getTotalPages()).isEqualTo(responseMultiPageDto.getPageInfo().getTotalPages());
        assertThat(result.getPageInfo().getSize()).isEqualTo(responseMultiPageDto.getPageInfo().getSize());
        assertThat(result.getPageInfo().getTotalElements()).isEqualTo(responseMultiPageDto.getPageInfo().getTotalElements());

    }
    @Test
    @DisplayName("아이템을 리스트로 가져올때 존재하지 않는 유저 아이디를 입력시 User_Not_Found 404가 리턴된다.")
    public void getSimpleItems_fail() {
        //given
        given(userService.findVerifiedUser(any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> itemService.getSimpleItems(1L, 1, 1)).isInstanceOf(BusinessLogicException.class);
    }

}
