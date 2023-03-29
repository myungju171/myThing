package com.project.mything.item.mapper;

import com.project.mything.item.dto.ItemDto;
import com.project.mything.item.entity.Item;
import com.project.mything.item.entity.ItemUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {

    Item toItem(ItemDto.RequestSaveItem requestSaveItem);

    ItemDto.ResponseItemId toResponseItemId(Long itemId);

    @Mapping(source = "dbItem.id", target = "itemId")
    @Mapping(source = "itemUser.createdAt", target = "createdAt")
    @Mapping(source = "itemUser.lastModifiedAt", target = "lastModifiedAt")
    ItemDto.ResponseDetailItem toResponseDetailItem(ItemUser itemUser, Item dbItem);

}
