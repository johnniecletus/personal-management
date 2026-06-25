package com.aj.personal.projects.management.controller;

import com.aj.personal.projects.management.dto.ApiResponseDto;
import com.aj.personal.projects.management.dto.IncomeDto;
import com.aj.personal.projects.management.dto.IncomePreviewDto;
import com.aj.personal.projects.management.dto.IncomePreviewRequestDto;
import com.aj.personal.projects.management.dto.IncomeRequestDto;
import com.aj.personal.projects.management.service.IncomeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/incomes")
@AllArgsConstructor
public class IncomeController {
    private final IncomeService incomeService;

    @PostMapping("/preview")
    public ResponseEntity<ApiResponseDto<IncomePreviewDto>> previewIncome(
            @Valid @RequestBody IncomePreviewRequestDto request
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.<IncomePreviewDto>builder()
                        .success(true)
                        .message("Income allocation preview generated successfully")
                        .data(incomeService.previewIncome(request))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<IncomeDto>> createIncome(
            @Valid @RequestBody IncomeRequestDto request
    ) {
        return new ResponseEntity<>(
                ApiResponseDto.<IncomeDto>builder()
                        .success(true)
                        .message("Income created successfully")
                        .data(incomeService.createIncome(request))
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<IncomeDto>>> getAllIncomes() {
        return ResponseEntity.ok(
                ApiResponseDto.<List<IncomeDto>>builder()
                        .success(true)
                        .message("Incomes fetched successfully")
                        .data(incomeService.getAllIncomes())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<IncomeDto>> getIncome(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponseDto.<IncomeDto>builder()
                        .success(true)
                        .message("Income fetched successfully")
                        .data(incomeService.getIncome(id))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<IncomeDto>> updateIncome(
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequestDto request
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.<IncomeDto>builder()
                        .success(true)
                        .message("Income updated successfully")
                        .data(incomeService.updateIncome(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<String>> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);

        return ResponseEntity.ok(
                ApiResponseDto.<String>builder()
                        .success(true)
                        .message("Income deleted successfully")
                        .data("Income deleted")
                        .build()
        );
    }
}
