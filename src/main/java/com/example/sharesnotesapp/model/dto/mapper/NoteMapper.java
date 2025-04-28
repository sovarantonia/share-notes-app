package com.example.sharesnotesapp.model.dto.mapper;

import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.dto.response.NoteResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TagMapper.class)
public interface NoteMapper {
    @Mapping(target = "tags", source = "tags")
    NoteResponseDto toDto(Note note);
}
