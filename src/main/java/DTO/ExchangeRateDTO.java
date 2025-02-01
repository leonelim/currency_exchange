package DTO;

import java.math.BigDecimal;

public record ExchangeRateDTO(Integer id, CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, BigDecimal rate) {
}
