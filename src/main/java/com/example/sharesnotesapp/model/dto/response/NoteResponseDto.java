package com.example.sharesnotesapp.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class NoteResponseDto {
    private UserResponseDto user;
    private Long id;
    private String title;
    private String text;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    private Integer grade;
    private Set<TagResponseDto> tags;
}
