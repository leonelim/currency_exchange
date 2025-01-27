package DTO;

import java.math.BigDecimal;

public record ExchangeRateDTO(Integer id, CurrencyDTO baseCurrencyDTO, CurrencyDTO targetCurrencyDTO, BigDecimal rate) {
}
