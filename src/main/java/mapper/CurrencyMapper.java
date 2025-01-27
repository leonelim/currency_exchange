package mapper;

import DTO.CurrencyDTO;
import Entity.Currency;


public class CurrencyMapper {
    private static final CurrencyMapper INSTANCE = new CurrencyMapper();
    private CurrencyMapper() {}
    public static CurrencyMapper getInstance() {
        return INSTANCE;
    }
    public CurrencyDTO toDto(Currency currency) {
        return new CurrencyDTO(currency.getId(), currency.getCode(), currency.getName(), currency.getSign());
    }
    public Currency toEntity(CurrencyDTO currencyDTO) {
        return new Currency(currencyDTO.id(), currencyDTO.code(), currencyDTO.name(), currencyDTO.sign());
    }
}
