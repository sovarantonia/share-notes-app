package com.example.sharesnotesapp.model.dto.mapper;

import com.example.sharesnotesapp.model.Tag;
import com.example.sharesnotesapp.model.dto.response.TagResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponseDto toDto(Tag tag);
}
