package com.aj.personal.projects.management.controller;

import com.aj.personal.projects.management.dto.ApiResponseDto;
import com.aj.personal.projects.management.dto.SavingsClusterDto;
import com.aj.personal.projects.management.dto.SavingsClusterRequestDto;
import com.aj.personal.projects.management.service.SavingsClusterService;
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
@RequestMapping("api/v1/savings-clusters")
@AllArgsConstructor
public class SavingsClusterController {
    private final SavingsClusterService savingsClusterService;

    @PostMapping("/preview")
    public ResponseEntity<ApiResponseDto<SavingsClusterDto>> previewCluster(
            @Valid @RequestBody SavingsClusterRequestDto request
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.<SavingsClusterDto>builder()
                        .success(true)
                        .message("Savings cluster preview generated successfully")
                        .data(savingsClusterService.previewCluster(request))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<SavingsClusterDto>> createCluster(
            @Valid @RequestBody SavingsClusterRequestDto request
    ) {
        return new ResponseEntity<>(
                ApiResponseDto.<SavingsClusterDto>builder()
                        .success(true)
                        .message("Savings cluster created successfully")
                        .data(savingsClusterService.createCluster(request))
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<SavingsClusterDto>>> getAllClusters() {
        return ResponseEntity.ok(
                ApiResponseDto.<List<SavingsClusterDto>>builder()
                        .success(true)
                        .message("Savings clusters fetched successfully")
                        .data(savingsClusterService.getAllClusters())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<SavingsClusterDto>> getCluster(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponseDto.<SavingsClusterDto>builder()
                        .success(true)
                        .message("Savings cluster fetched successfully")
                        .data(savingsClusterService.getCluster(id))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<SavingsClusterDto>> updateCluster(
            @PathVariable Long id,
            @Valid @RequestBody SavingsClusterRequestDto request
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.<SavingsClusterDto>builder()
                        .success(true)
                        .message("Savings cluster updated successfully")
                        .data(savingsClusterService.updateCluster(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<String>> deleteCluster(@PathVariable Long id) {
        savingsClusterService.deleteCluster(id);

        return ResponseEntity.ok(
                ApiResponseDto.<String>builder()
                        .success(true)
                        .message("Savings cluster deleted successfully")
                        .data("Savings cluster deleted")
                        .build()
        );
    }
}
