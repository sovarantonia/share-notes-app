package com.example.sharesnotesapp.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class GradeSummaryDto {
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    private Double averageGrade;
}
