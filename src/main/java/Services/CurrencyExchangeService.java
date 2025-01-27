package Services;

import DAO.CurrencyExchangeDAO;
import DTO.ExchangeRateDTO;
import Entity.ExchangeRate;
import exceptions.ExchangeRateDoesntExistsException;
import exceptions.InvalidInputException;
import mapper.ExchangeRateMapper;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class CurrencyExchangeService {
    private final CurrencyExchangeDAO currencyExchangeDAO = CurrencyExchangeDAO.getInstance();
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.getInstance();
    private static final CurrencyExchangeService INSTANCE = new CurrencyExchangeService();
    private CurrencyExchangeService() {}
    public static CurrencyExchangeService getInstance() {
        return INSTANCE;
    }
    public List<ExchangeRateDTO> getAll() {
        return currencyExchangeDAO.getAll().stream().map(exchangeRateMapper::toDTO).toList();
    }
    public ExchangeRateDTO get(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<ExchangeRate> optionalExchangeRate = currencyExchangeDAO.get(baseCurrencyCode, targetCurrencyCode);
        if (optionalExchangeRate.isPresent()) {
            return exchangeRateMapper.toDTO(optionalExchangeRate.get());
        }
        Optional<ExchangeRate> optionalReverseExchangeRate = currencyExchangeDAO.get(targetCurrencyCode, baseCurrencyCode);
        ExchangeRate reverseExchangeRate = optionalReverseExchangeRate.orElseThrow(() -> new ExchangeRateDoesntExistsException("The exchange rate with the given codes doesn't exist"));
        reverseExchangeRate.setRate(BigDecimal.ONE.divide(reverseExchangeRate.getRate(), RoundingMode.HALF_UP));
        return exchangeRateMapper.toDTO(reverseExchangeRate);
    }
}
