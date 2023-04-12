package com.project.mything.item.mapper;

import com.project.mything.item.dto.ItemDto;
import com.project.mything.item.entity.Item;
import com.project.mything.item.entity.ItemUser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {

    Item toItem(ItemDto.RequestSaveItem requestSaveItem);

    ItemDto.ResponseItemId toResponseItemId(Long itemId);

    @Mapping(source = "dbItem.id", target = "itemId")
    @Mapping(source = "itemUser.createdAt", target = "createdAt")
    @Mapping(source = "itemUser.lastModifiedAt", target = "lastModifiedAt")
    ItemDto.ResponseDetailItem toResponseDetailItem(ItemUser itemUser, Item dbItem);

    private ItemDto.SearchItem toSearchItem(JSONObject jsonObject) {
        return ItemDto.SearchItem.builder()
                .title(jsonObject.getString("title"))
                .price(jsonObject.getInt("lprice"))
                .productId(jsonObject.getLong("productId"))
                .link(jsonObject.getString("link"))
                .image(jsonObject.getString("image"))
                .build();
    }

    default List<ItemDto.SearchItem> toSearchItemList(JSONArray jsonArray) {
        List<ItemDto.SearchItem> SearchItemList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++)
            SearchItemList.add(toSearchItem(jsonArray.getJSONObject(i)));
        return SearchItemList;
    }

    @Mapping(source = "searchItemList", target = "items")
    ItemDto.ResponseSearchItem toResponseSearchItem(List<ItemDto.SearchItem> searchItemList, Integer size, Integer start);

}
