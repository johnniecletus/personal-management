package com.aj.personal.projects.management.service;

import com.aj.personal.projects.management.dto.IncomeDto;
import com.aj.personal.projects.management.dto.IncomePreviewDto;
import com.aj.personal.projects.management.dto.IncomePreviewRequestDto;
import com.aj.personal.projects.management.dto.IncomeRequestDto;
import java.util.List;

public interface IncomeService {
    IncomePreviewDto previewIncome(IncomePreviewRequestDto request);

    IncomeDto createIncome(IncomeRequestDto request);

    IncomeDto updateIncome(Long id, IncomeRequestDto request);

    IncomeDto getIncome(Long id);

    List<IncomeDto> getAllIncomes();

    void deleteIncome(Long id);
}
