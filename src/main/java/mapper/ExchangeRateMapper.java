package mapper;

import DTO.ExchangeRateDTO;
import Entity.ExchangeRate;

public class ExchangeRateMapper {
    private final CurrencyMapper currencyMapper = CurrencyMapper.getInstance();
    private static final ExchangeRateMapper INSTANCE = new ExchangeRateMapper();
    private ExchangeRateMapper() {}
    public static ExchangeRateMapper getInstance() {
        return INSTANCE;
    }
    public ExchangeRateDTO toDTO(ExchangeRate exchangeRate) {
        return new ExchangeRateDTO(exchangeRate.getId(), currencyMapper.toDto(exchangeRate.getBaseCurrency()), currencyMapper.toDto(exchangeRate.getTargetCurrency()), exchangeRate.getRate());
    }
    public ExchangeRate toEntity(ExchangeRateDTO exchangeRateDTO) {
        return new ExchangeRate(exchangeRateDTO.id(), currencyMapper.toEntity(exchangeRateDTO.baseCurrencyDTO()), currencyMapper.toEntity(exchangeRateDTO.targetCurrencyDTO()), exchangeRateDTO.rate());
    }
}
