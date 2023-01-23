package com.project.mything.item.mapper;

import com.project.mything.item.dto.ItemDto;
import com.project.mything.item.entity.Item;
import com.project.mything.item.entity.ItemUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class ItemMapperTest {

    ItemMapper itemMapper;

    @BeforeEach
    public void init() {
        itemMapper = new ItemMapperImpl();
    }

    @Test
    @DisplayName("RequestSaveItem 객체를 Item객체로 매핑하는 메서드 성공 테스트")
    public void toItem_suc(){
    //given
        ItemDto.RequestSaveItem requestSaveItem = ItemDto.RequestSaveItem.builder()
                .productId(1L)
                .image("imageLink")
                .price(1000)
                .title("테스트 타이틀입니다.")
                .link("link")
                .build();
        //when
        Item item = itemMapper.toItem(requestSaveItem);
        //then
        assertThat(item.getProductId()).isEqualTo(requestSaveItem.getProductId());
        assertThat(item.getImage()).isEqualTo(requestSaveItem.getImage());
        assertThat(item.getPrice()).isEqualTo(requestSaveItem.getPrice());
        assertThat(item.getTitle()).isEqualTo(requestSaveItem.getTitle());
        assertThat(item.getLink()).isEqualTo(requestSaveItem.getLink());
    }

    @Test
    @DisplayName("User 객체에서 ResponseItemId객체로 매핑하는 메서드 성공 테스트")
    public void toResponseItemId_suc(){
        //when
        ItemDto.ResponseItemId responseItemId = itemMapper.toResponseItemId(1L);
        //then
        assertThat(responseItemId.getItemId()).isEqualTo(1L);
    }


    @Test
    @DisplayName("ResponseDetailItem 객체로 mapping하는 메서드 테스트 ")
    public void toResponseDetailItem_suc(){
        //given
        Item dbItem = Item.builder()
                .id(1L)
                .link("링크")
                .title("타이틀")
                .price(1000)
                .image("이미지 링크")
                .build();
        ItemUser dbItemUser = ItemUser.builder()
                .id(1L)
                .item(dbItem)
                .memo("test Memo")
                .build();
        //when
        ItemDto.ResponseDetailItem detailItem = itemMapper.toResponseDetailItem(dbItemUser, dbItem);
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

}