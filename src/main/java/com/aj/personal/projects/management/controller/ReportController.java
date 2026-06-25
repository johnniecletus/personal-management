package com.aj.personal.projects.management.controller;

import com.aj.personal.projects.management.dto.ApiResponseDto;
import com.aj.personal.projects.management.dto.MonthlyOverviewDto;
import com.aj.personal.projects.management.dto.SavingsHistoryDto;
import com.aj.personal.projects.management.service.ReportService;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/reports")
@AllArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/monthly-overviews")
    public ResponseEntity<ApiResponseDto<List<MonthlyOverviewDto>>> getMonthlyOverviews(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long currencyId
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.<List<MonthlyOverviewDto>>builder()
                        .success(true)
                        .message("Monthly overviews fetched successfully")
                        .data(reportService.getMonthlyOverviews(from, to, currencyId))
                        .build()
        );
    }

    @GetMapping("/savings-histories")
    public ResponseEntity<ApiResponseDto<List<SavingsHistoryDto>>> getSavingsHistories(
            @RequestParam(required = false) Long incomeId,
            @RequestParam(required = false) Long clusterId
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.<List<SavingsHistoryDto>>builder()
                        .success(true)
                        .message("Savings histories fetched successfully")
                        .data(reportService.getSavingsHistories(incomeId, clusterId))
                        .build()
        );
    }
}
