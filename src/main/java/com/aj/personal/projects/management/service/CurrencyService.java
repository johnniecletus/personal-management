package com.aj.personal.projects.management.service;

import com.aj.personal.projects.management.dto.CurrencyDto;
import com.aj.personal.projects.management.dto.CurrencyRequestDto;
import java.util.List;

public interface CurrencyService {
    List<CurrencyDto> getAllCurrencies();

    CurrencyDto getCurrency(Long id);

    CurrencyDto createCurrency(CurrencyRequestDto request);

    CurrencyDto updateCurrency(Long id, CurrencyRequestDto request);

    void deleteCurrency(Long id);
}
