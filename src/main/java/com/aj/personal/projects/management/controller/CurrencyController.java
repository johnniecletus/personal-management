package com.aj.personal.projects.management.controller;

import com.aj.personal.projects.management.dto.ApiResponseDto;
import com.aj.personal.projects.management.dto.CurrencyDto;
import com.aj.personal.projects.management.dto.CurrencyRequestDto;
import com.aj.personal.projects.management.service.CurrencyService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/currencies")
@AllArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<CurrencyDto>>> getAllCurrencies() {
        return ResponseEntity.ok(
                ApiResponseDto.<List<CurrencyDto>>builder()
                        .success(true)
                        .message("Currencies fetched successfully")
                        .data(currencyService.getAllCurrencies())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CurrencyDto>> getCurrency(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponseDto.<CurrencyDto>builder()
                        .success(true)
                        .message("Currency fetched successfully")
                        .data(currencyService.getCurrency(id))
                        .build()
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<CurrencyDto>> createCurrency(
            @Valid @RequestBody CurrencyRequestDto request
    ) {
        return new ResponseEntity<>(
                ApiResponseDto.<CurrencyDto>builder()
                        .success(true)
                        .message("Currency created successfully")
                        .data(currencyService.createCurrency(request))
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<CurrencyDto>> updateCurrency(
            @PathVariable Long id,
            @Valid @RequestBody CurrencyRequestDto request
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.<CurrencyDto>builder()
                        .success(true)
                        .message("Currency updated successfully")
                        .data(currencyService.updateCurrency(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<String>> deleteCurrency(@PathVariable Long id) {
        currencyService.deleteCurrency(id);

        return ResponseEntity.ok(
                ApiResponseDto.<String>builder()
                        .success(true)
                        .message("Currency deleted successfully")
                        .data("Currency deleted")
                        .build()
        );
    }
}
