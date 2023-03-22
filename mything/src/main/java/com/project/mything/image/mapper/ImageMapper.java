package com.project.mything.image.mapper;

import com.project.mything.image.dto.ImageDto;
import com.project.mything.image.entity.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface ImageMapper {

    @Mapping(source = "image.id", target = "imageId")
    ImageDto.SimpleImageDto toResponseUpload(Image image);

    @Mapping(source = "originalName", target = "originalFilename")
    @Mapping(source = "size", target = "fileSize")
    Image toImage(String localPath, String originalName, Integer size, String remotePath);

}
