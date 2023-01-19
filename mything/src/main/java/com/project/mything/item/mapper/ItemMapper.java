package com.project.mything.item.mapper;

import com.project.mything.item.dto.ItemDto;
import com.project.mything.item.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {

    Item toItem(ItemDto.RequestSaveItem requestSaveItem);

    ItemDto.ResponseItemId toResponseItemId(Long itemId);
}
