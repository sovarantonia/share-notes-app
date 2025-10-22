package com.example.sharesnotesapp.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class NoteRequestDto {
    private Long userId;
    @NotBlank(message = "Title should not be empty")
    private String title = "";
    private String text = "";
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    @Min(value = 1, message = "Values must be integers between 1 and 10")
    @Max(value = 10, message = "Values must be integers between 1 and 10")
    private Integer grade = 0;
    private Set<String> tags = new HashSet<>();
}
