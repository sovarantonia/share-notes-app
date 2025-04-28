package com.example.sharesnotesapp.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Date;
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
