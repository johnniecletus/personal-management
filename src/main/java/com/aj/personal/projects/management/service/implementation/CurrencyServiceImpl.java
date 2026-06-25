package com.aj.personal.projects.management.service.implementation;

import com.aj.personal.projects.management.dto.CurrencyDto;
import com.aj.personal.projects.management.dto.CurrencyRequestDto;
import com.aj.personal.projects.management.entity.Currency;
import com.aj.personal.projects.management.exception.BadRequestException;
import com.aj.personal.projects.management.exception.ResourceNotFoundException;
import com.aj.personal.projects.management.repository.CurrencyRepository;
import com.aj.personal.projects.management.repository.IncomeRepository;
import com.aj.personal.projects.management.repository.MonthlyOverviewRepository;
import com.aj.personal.projects.management.repository.SavingsHistoryRepository;
import com.aj.personal.projects.management.service.CurrencyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final IncomeRepository incomeRepository;
    private final SavingsHistoryRepository savingsHistoryRepository;
    private final MonthlyOverviewRepository monthlyOverviewRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CurrencyDto> getAllCurrencies() {
        return currencyRepository.findAllByOrderByCodeAsc()
                .stream()
                .map(this::mapCurrency)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyDto getCurrency(Long id) {
        return mapCurrency(getCurrencyEntity(id));
    }

    @Override
    public CurrencyDto createCurrency(CurrencyRequestDto request) {
        if (currencyRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new BadRequestException("Currency code " + request.getCode() + " already exists");
        }

        Currency currency = new Currency(request.getName().trim(), request.getCode().trim().toUpperCase());
        return mapCurrency(currencyRepository.save(currency));
    }

    @Override
    public CurrencyDto updateCurrency(Long id, CurrencyRequestDto request) {
        Currency currency = getCurrencyEntity(id);
        currencyRepository.findByCodeIgnoreCase(request.getCode())
                .filter(existingCurrency -> !existingCurrency.getId().equals(currency.getId()))
                .ifPresent(existingCurrency -> {
                    throw new BadRequestException("Currency code " + request.getCode() + " already exists");
                });

        currency.setCode(request.getCode().trim().toUpperCase());
        currency.setName(request.getName().trim());
        return mapCurrency(currencyRepository.save(currency));
    }

    @Override
    public void deleteCurrency(Long id) {
        Currency currency = getCurrencyEntity(id);

        if (incomeRepository.existsByCurrencyId(id)
                || savingsHistoryRepository.existsByCurrencyId(id)
                || monthlyOverviewRepository.existsByCurrencyId(id)) {
            throw new BadRequestException("Currency cannot be deleted because it is already in use");
        }

        currencyRepository.delete(currency);
    }

    private Currency getCurrencyEntity(Long id) {
        return currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id " + id));
    }

    private CurrencyDto mapCurrency(Currency currency) {
        return CurrencyDto.builder()
                .id(currency.getId())
                .code(currency.getCode())
                .name(currency.getName())
                .createdAt(currency.getCreatedAt())
                .build();
    }
}
