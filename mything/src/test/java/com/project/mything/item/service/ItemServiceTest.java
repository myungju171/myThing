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
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import com.project.mything.user.mapper.UserMapper;
import com.project.mything.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

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
    @Mock
    UserMapper userMapper;


    @Test
    @DisplayName("?????? API ????????? query??? ??????????????? 200 INCORRECT_QUERY_REQUEST 400??? ????????????.")
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
    @DisplayName("?????? API ????????? ???????????? ResponseEntity<String>??? ????????????.")
    public void search_suc() {
        //given
        String query = "?????????";
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
    @DisplayName("????????? ????????? ???????????? ItemDto.ResponseItemId??? ????????????.")
    public void saveItem_suc() {
        //given
        ItemDto.RequestSaveItem requestSaveItem = ItemDto.RequestSaveItem.builder()
                .userId(1L)
                .link("??????")
                .title("?????????")
                .price(1000)
                .image("????????? ??????")
                .productId(1111L)
                .build();

        User dbUser = User.builder()
                .id(1L)
                .name("?????????")
                .build();

        Item dbItem = Item.builder()
                .id(1L)
                .link("??????")
                .title("?????????")
                .price(1000)
                .image("????????? ??????")
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
    @DisplayName("????????? ????????? ?????? ???????????? ???????????? ????????? ????????????????????? Item_Exist 409 CONFLICT ????????????.")
    public void saveItem_fail() {
        //given
        ItemDto.RequestSaveItem requestSaveItem = ItemDto.RequestSaveItem.builder()
                .userId(1L)
                .link("??????")
                .title("?????????")
                .price(1000)
                .image("????????? ??????")
                .productId(1111L)
                .build();
        given(itemUserRepository.findItemUserByUserIdAndProductId(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_EXISTS));
        //when
        //then
        assertThatThrownBy(() -> itemService.saveItem(requestSaveItem)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("????????? ???????????? ????????? ?????? ?????????")
    public void getDetailItem_suc() {
        //given
        Item dbItem = Item.builder()
                .id(1L)
                .link("??????")
                .title("?????????")
                .price(1000)
                .image("????????? ??????")
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
                .title("?????????")
                .link("??????")
                .price(1000)
                .image("????????? ??????")
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
    @DisplayName("????????? ???????????? ????????? ?????????????????? ????????????????????? ??? ItemUser????????? ????????????????????? 404 Item_Not_Found ?????? ?????????")
    public void getDetailItem_fail() {
        //given
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> itemService.getDetailItem(any(), any())).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("???????????? ???????????? ???????????? ResponseMultiPageDto??? ????????????.")
    public void getSimpleItems_suc() {
        //given
        List<ItemDto.ResponseSimpleItem> data = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
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
                .name("?????????")
                .image("remote image")
                .build();
        PageRequest pageable = PageRequest.of(0, 5, Sort.by("itemStatus").descending());
        Page<ItemDto.ResponseSimpleItem> responseSimpleItems = new PageImpl<>(data, pageable, 5);
        ResponseMultiPageDto<ItemDto.ResponseSimpleItem> responseMultiPageDto =
                new ResponseMultiPageDto<ItemDto.ResponseSimpleItem>(data, responseSimpleItems, responseSimpleUser);

        given(userService.findVerifiedUser(any())).willReturn(User.builder().build());
        given(itemUserRepository.searchSimpleItem(any(), any(), any())).willReturn(responseSimpleItems);
        given(userMapper.toResponseSimpleUser(any())).willReturn(responseSimpleUser);
        //when
        ResponseMultiPageDto<ItemDto.ResponseSimpleItem> result = itemService.getSimpleItems(1L, false,1, 5);
        //then
        assertThat(result.getData()).isEqualTo(responseMultiPageDto.getData());
        assertThat(result.getPageInfo().getPage()).isEqualTo(responseMultiPageDto.getPageInfo().getPage());
        assertThat(result.getPageInfo().getTotalPages()).isEqualTo(responseMultiPageDto.getPageInfo().getTotalPages());
        assertThat(result.getPageInfo().getSize()).isEqualTo(responseMultiPageDto.getPageInfo().getSize());
        assertThat(result.getPageInfo().getTotalElements()).isEqualTo(responseMultiPageDto.getPageInfo().getTotalElements());

    }

    @Test
    @DisplayName("???????????? ???????????? ???????????? ???????????? ?????? ?????? ???????????? ????????? User_Not_Found 404??? ????????????.")
    public void getSimpleItems_fail() {
        //given
        given(userService.findVerifiedUser(any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> itemService.getSimpleItems(1L, false,1, 1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("???????????? ????????? Bought??? ?????? ??????")
    public void changeItemStatus_suc() {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(1L)
                .itemStatus(ItemStatus.BOUGHT)
                .build();
        User dbUser = User.builder()
                .id(1L)
                .build();
        Item dbItem = Item.builder()
                .id(1L)
                .build();
        ItemUser dbItemUser = ItemUser.builder()
                .id(1L)
                .user(dbUser)
                .item(dbItem)
                .itemStatus(ItemStatus.POST)
                .build();
        ItemDto.ResponseItemId responseItemId = ItemDto.ResponseItemId.builder()
                .itemId(1L)
                .build();
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any())).willReturn(Optional.of(dbItemUser));
        given(itemMapper.toResponseItemId(any())).willReturn(responseItemId);
        //when
        ItemDto.ResponseItemId result = itemService.changeItemStatus(requestChangeItemStatus, null);
        //then
        assertThat(result.getItemId()).isEqualTo(responseItemId.getItemId());
        assertThat(dbItemUser.getItemStatus()).isEqualTo(ItemStatus.BOUGHT);
    }

    @Test
    @DisplayName("???????????? ????????? RECEIVED??? ?????? ??????")
    public void changeItemStatus_suc3() {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(1L)
                .itemStatus(ItemStatus.RECEIVED)
                .build();
        User dbUser = User.builder()
                .id(1L)
                .build();
        Item dbItem = Item.builder()
                .id(1L)
                .build();
        ItemUser dbItemUser = ItemUser.builder()
                .id(1L)
                .user(dbUser)
                .item(dbItem)
                .itemStatus(ItemStatus.POST)
                .build();
        ItemDto.ResponseItemId responseItemId = ItemDto.ResponseItemId.builder()
                .itemId(1L)
                .build();
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any())).willReturn(Optional.of(dbItemUser));
        given(itemMapper.toResponseItemId(any())).willReturn(responseItemId);
        //when
        ItemDto.ResponseItemId result = itemService.changeItemStatus(requestChangeItemStatus, null);
        //then
        assertThat(result.getItemId()).isEqualTo(responseItemId.getItemId());
        assertThat(dbItemUser.getItemStatus()).isEqualTo(ItemStatus.RECEIVED);
    }

    @Test
    @DisplayName("???????????? ????????? RESERVE??? ?????? ??????")
    public void changeItemStatus_suc4() {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(1L)
                .itemStatus(ItemStatus.RESERVE)
                .build();
        User dbUser = User.builder()
                .id(1L)
                .build();
        User reservedUser = User.builder()
                .id(2L)
                .build();
        Item dbItem = Item.builder()
                .id(1L)
                .build();
        ItemUser dbItemUser = ItemUser.builder()
                .id(1L)
                .user(dbUser)
                .item(dbItem)
                .itemStatus(ItemStatus.POST)
                .build();
        ItemDto.ResponseItemId responseItemId = ItemDto.ResponseItemId.builder()
                .itemId(1L)
                .build();
        given(userService.findVerifiedUser(any())).willReturn(reservedUser);
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any())).willReturn(Optional.of(dbItemUser));
        given(itemMapper.toResponseItemId(any())).willReturn(responseItemId);
        //when
        ItemDto.ResponseItemId result = itemService.changeItemStatus(requestChangeItemStatus, reservedUser.getId());
        //then
        assertThat(result.getItemId()).isEqualTo(responseItemId.getItemId());
        assertThat(dbItemUser.getItemStatus()).isEqualTo(ItemStatus.RESERVE);
        assertThat(dbItemUser.getReservedUserId()).isEqualTo(reservedUser.getId());
    }

    @Test
    @DisplayName("???????????? ????????? RESERVE??? ????????? ?????? RESERVE????????? ?????? ?????? 409 ")
    public void changeItemStatus_fail1() {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(1L)
                .itemStatus(ItemStatus.RESERVE)
                .build();
        User dbUser = User.builder()
                .id(1L)
                .build();
        User reservedUser = User.builder()
                .id(2L)
                .build();
        Item dbItem = Item.builder()
                .id(1L)
                .build();
        ItemUser dbItemUser = ItemUser.builder()
                .id(1L)
                .user(dbUser)
                .item(dbItem)
                .itemStatus(ItemStatus.RESERVE)
                .reservedUserId(2L)
                .build();
        given(userService.findVerifiedUser(any())).willReturn(reservedUser);
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any())).willReturn(Optional.of(dbItemUser));

        //when

        //then
        assertThatThrownBy(() -> itemService.changeItemStatus(requestChangeItemStatus, reservedUser.getId()))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("???????????? ????????? RESERVE??? ????????? ???????????? ?????? reservedId??? ?????? ??? ?????? ?????? 409 ")
    public void changeItemStatus_fail2() {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(1L)
                .itemStatus(ItemStatus.RESERVE)
                .build();
        User reservedUser = User.builder()
                .id(50000L)
                .build();
        given(userService.findVerifiedUser(any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        //when
        //then
        assertThatThrownBy(() -> itemService.changeItemStatus(requestChangeItemStatus, reservedUser.getId()))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("???????????? ????????? ????????? ?????????????????? ?????????????????? ?????? ??? ?????? ?????? 404 ")
    public void changeItemStatus_fail3() {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(5000L)
                .itemId(1L)
                .itemStatus(ItemStatus.RESERVE)
                .build();

        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));

        //when
        //then
        assertThatThrownBy(() -> itemService.changeItemStatus(requestChangeItemStatus, null))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("???????????? ????????? ????????? ???????????? ????????? ?????????????????? ????????????????????? ????????? ?????? ?????? 409")
    public void changeItemStatus_fail4() {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(5000L)
                .itemId(1L)
                .itemStatus(ItemStatus.RESERVE)
                .build();

        //when
        //then
        assertThatThrownBy(() ->
                itemService.changeItemStatus(requestChangeItemStatus, requestChangeItemStatus.getUserId()))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("???????????? ????????? POST??? ????????? POST_NOT_ALLOW 409 ??????")
    public void changeItemStatus_fail5() {
        //given
        ItemDto.RequestChangeItemStatus requestChangeItemStatus = ItemDto.RequestChangeItemStatus.builder()
                .userId(1L)
                .itemId(1L)
                .itemStatus(ItemStatus.POST)
                .build();
        User reservedId = User.builder()
                .id(1L)
                .build();

        //when
        //then
        assertThatThrownBy(() -> itemService.changeItemStatus(requestChangeItemStatus, reservedId.getId()))
                .isInstanceOf(BusinessLogicException.class);
    }
    @Test
    @DisplayName("????????? ????????? ???????????? ??????????????? ??????")
    public void cancelReservedItem_suc() {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .userId(1L)
                .itemId(1L)
                .reservedId(2L)
                .build();
        ItemUser dbItemUser = ItemUser.builder()
                .id(1L)
                .reservedUserId(2L)
                .itemStatus(ItemStatus.RESERVE)
                .build();
        User reservedUser = User.builder()
                .id(2L)
                .build();

        given(userService.findVerifiedUser(any())).willReturn(reservedUser);
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any())).willReturn(Optional.of(dbItemUser));
        //when
        itemService.cancelReservedItem(requestCancelReserveItem);
        //then
        assertThat(dbItemUser.getItemStatus()).isEqualTo(ItemStatus.POST);
        assertThat(dbItemUser.getReservedUserId()).isNull();
    }

    @Test
    @DisplayName("????????? ????????? ????????? ???????????? ?????? ?????? ?????? ???????????? ?????? USER_NOT_MATCH 409 ??????")
    public void cancelReservedItem_fail1() {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .userId(1L)
                .itemId(1L)
                .reservedId(2L)
                .build();
        ItemUser dbItemUser = ItemUser.builder()
                .id(1L)
                .reservedUserId(10L)
                .itemStatus(ItemStatus.RESERVE)
                .build();
        User differentUser = User.builder()
                .id(2L)
                .build();

        given(userService.findVerifiedUser(any())).willReturn(differentUser);
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any())).willReturn(Optional.of(dbItemUser));
        //when
        //then
        assertThatThrownBy(() -> itemService.cancelReservedItem(requestCancelReserveItem))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("????????? ????????? ????????? ???????????? ????????? RESERVE??? ????????? ITEM_STATUS_NOT_RESERVE 409??????")
    public void cancelReservedItem_fail3() {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .userId(1L)
                .itemId(1L)
                .reservedId(2L)
                .build();
        ItemUser dbItemUser = ItemUser.builder()
                .id(1L)
                .itemStatus(ItemStatus.POST)
                .build();
        User reservedUser = User.builder()
                .id(2L)
                .build();

        given(userService.findVerifiedUser(any())).willReturn(reservedUser);
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any())).willReturn(Optional.of(dbItemUser));
        //when
        //then
        assertThatThrownBy(() -> itemService.cancelReservedItem(requestCancelReserveItem))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("????????? ????????? ????????? ???????????? ?????? reserve?????????????????? ?????? ??? ?????? USER_NOT_FOUND 404 ??????")
    public void cancelReservedItem_fail4() {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .userId(1L)
                .itemId(1L)
                .reservedId(5000L)
                .build();

        given(userService.findVerifiedUser(any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> itemService.cancelReservedItem(requestCancelReserveItem))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("????????? ????????? ????????? ???????????? ?????? ????????????????????? ?????? ??? ?????? ITEM_NOT_FOUND 404 ??????")
    public void cancelReservedItem_fail5() {
        //given
        ItemDto.RequestCancelReserveItem requestCancelReserveItem = ItemDto.RequestCancelReserveItem.builder()
                .userId(1L)
                .itemId(5000L)
                .reservedId(2L)
                .build();
        User reservedUser = User.builder()
                .id(2L)
                .build();

        given(userService.findVerifiedUser(any())).willReturn(reservedUser);
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> itemService.cancelReservedItem(requestCancelReserveItem))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("ItemUser?????? ????????? ItemStatus??? POST??? ????????????.")
    public void deleteItemUser_suc() {
        //given
        User user = User.builder().id(1L).build();
        Item item = Item.builder().id(1L).build();

        ItemUser itemUser = ItemUser.builder().item(item).user(user).itemStatus(ItemStatus.POST).build();
        itemUser.addItemUser();

        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .itemId(1L)
                .userId(1L)
                .build();
        assertThat(user.getItemUserList().size()).isEqualTo(1);
        assertThat(user.getItemUserList().size()).isEqualTo(1);

        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any())).willReturn(Optional.of(itemUser));

        //when
        itemService.deleteItemUser(requestSimpleItem);
        //then
        assertThat(user.getItemUserList().size()).isEqualTo(0);
        assertThat(user.getItemUserList().size()).isEqualTo(0);
        verify(itemUserRepository, times(1)).delete(any());
    }

    @DisplayName("ItemUser?????? ????????? ItemStatus??? POST??? ????????? 409 ITEM_STATUS_NOT_POST ??????")
    @ParameterizedTest
    @MethodSource("invalidItemStatusParameter")
    public void deleteItemUser_fail(final ItemStatus itemStatus) {
        //given
        ItemUser itemUser = ItemUser.builder().itemStatus(itemStatus).build();

        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .itemId(1L)
                .userId(1L)
                .build();
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any())).willReturn(Optional.of(itemUser));

        //when
        //then
        assertThatThrownBy(() -> itemService.deleteItemUser(requestSimpleItem))
                .isInstanceOf(BusinessLogicException.class);
    }

    private static Stream<Arguments> invalidItemStatusParameter() {

        return Stream.of(
                Arguments.of(ItemStatus.BOUGHT),
                Arguments.of(ItemStatus.RECEIVED),
                Arguments.of(ItemStatus.RESERVE)
        );
    }

    @DisplayName("ItemUser?????? ????????? ???????????? ?????? ??????????????? ?????? ????????? ???????????? ????????? ?????? 404 ITEM_NOT_FOUND 404 ??????.")
    @ParameterizedTest
    @MethodSource("invalidUserIdAndItemIdParameter")
    public void deleteItemUser_fail2(final Long userId, final Long itemId) {
        //given
        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .itemId(userId)
                .userId(itemId)
                .build();

        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));

        //when
        //then
        assertThatThrownBy(() -> itemService.deleteItemUser(requestSimpleItem))
                .isInstanceOf(BusinessLogicException.class);
    }

    private static Stream<Arguments> invalidUserIdAndItemIdParameter() {

        return Stream.of(
                Arguments.of(5000L, 1L),
                Arguments.of(1L, 5000L)
        );
    }

    @Test
    @DisplayName("???????????? ???????????? ????????? ???????????? ????????? ?????????????????? ????????? ????????????????????? ????????? ?????? itemId??? 200 ??????  ")
    public void changeItemInterest_suc() {
        //given
        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .userId(1L)
                .itemId(1L)
                .build();
        ItemUser interestedTrueItemUser = ItemUser.builder()
                .interestedItem(Boolean.TRUE)
                .build();
        ItemDto.ResponseItemId responseItemId = ItemDto.ResponseItemId.builder()
                .itemId(1L)
                .build();
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any()))
                .willReturn(Optional.of(interestedTrueItemUser));
        given(itemMapper.toResponseItemId(any())).willReturn(responseItemId);
        //when
        responseItemId = itemService.changeItemInterest(requestSimpleItem);
        //then
        assertThat(responseItemId.getItemId()).isEqualTo(requestSimpleItem.getItemId());
        assertThat(interestedTrueItemUser.getInterestedItem()).isFalse();
    }


    @ParameterizedTest
    @MethodSource("invalidUserIdAndItemIdParameter")
    @DisplayName("???????????? ???????????? ????????? ???????????? ????????? ?????????????????? ????????????????????? ????????? ?????? ITEM_NOT_FOUND ??? 404 ??????  ")
    public void changeItemInterest_fail(final Long userId, final Long itemId) {
        //given
        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .userId(userId)
                .itemId(itemId)
                .build();
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
        //when

        //then
        assertThatThrownBy(() -> itemService.changeItemInterest(requestSimpleItem))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("????????? ???????????? ????????? ???????????? ????????? ?????????????????? ????????? ????????????????????? ????????? ?????? itemId??? 200 ??????  ")
    public void changeItemSecret_suc() {
        //given
        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .userId(1L)
                .itemId(1L)
                .build();
        ItemUser secretItemTrueItemUser = ItemUser.builder()
                .secretItem(Boolean.TRUE)
                .build();
        ItemDto.ResponseItemId responseItemId = ItemDto.ResponseItemId.builder()
                .itemId(1L)
                .build();
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any()))
                .willReturn(Optional.of(secretItemTrueItemUser));
        given(itemMapper.toResponseItemId(any())).willReturn(responseItemId);
        //when
        responseItemId = itemService.changeItemSecret(requestSimpleItem);
        //then
        assertThat(responseItemId.getItemId()).isEqualTo(requestSimpleItem.getItemId());
        assertThat(secretItemTrueItemUser.getInterestedItem()).isFalse();
    }


    @ParameterizedTest
    @MethodSource("invalidUserIdAndItemIdParameter")
    @DisplayName("???????????? ???????????? ????????? ???????????? ????????? ?????????????????? ????????????????????? ????????? ?????? ITEM_NOT_FOUND ??? 404 ??????  ")
    public void changeItemSecret_fail(final Long userId, final Long itemId) {
        //given
        ItemDto.RequestSimpleItem requestSimpleItem = ItemDto.RequestSimpleItem.builder()
                .userId(userId)
                .itemId(itemId)
                .build();
        given(itemUserRepository.findItemUserByUserIdAndItemId(any(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ITEM_NOT_FOUND));
        //when

        //then
        assertThatThrownBy(() -> itemService.changeItemSecret(requestSimpleItem))
                .isInstanceOf(BusinessLogicException.class);
    }
}