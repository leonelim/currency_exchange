package Services;

import DAO.CurrencyExchangeDAO;
import DTO.ExchangeRateDTO;
import Entity.ExchangeRate;
import exceptions.ExchangeRateDoesntExistsException;
import exceptions.InvalidInputException;
import mapper.ExchangeRateMapper;
import validation.ExchangeRateValidator;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class CurrencyExchangeService {
    private final CurrencyExchangeDAO currencyExchangeDAO = CurrencyExchangeDAO.getInstance();
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.getInstance();
    private final ExchangeRateValidator exchangeRateValidator = new ExchangeRateValidator();
    private static final CurrencyExchangeService INSTANCE = new CurrencyExchangeService();
    private CurrencyExchangeService() {}
    public static CurrencyExchangeService getInstance() {
        return INSTANCE;
    }
    public List<ExchangeRateDTO> getAll() {
        return currencyExchangeDAO.getAll().stream().map(exchangeRateMapper::toDTO).toList();
    }
    public ExchangeRateDTO getAndSaveCalculatedExchangeRates(String baseCurrencyCode, String targetCurrencyCode) {
        if (!exchangeRateValidator.isValidCodes(baseCurrencyCode, targetCurrencyCode)) {
            throw new InvalidInputException("The codes should consist of 3 letters of the latin alphabet");
        }

        Optional<ExchangeRate> optionalExchangeRate = currencyExchangeDAO.get(baseCurrencyCode, targetCurrencyCode);
        if (optionalExchangeRate.isPresent()) {
            return exchangeRateMapper.toDTO(optionalExchangeRate.get());
        }
        Optional<ExchangeRate> optionalReverseExchangeRate = currencyExchangeDAO.get(targetCurrencyCode, baseCurrencyCode);
        if (optionalReverseExchangeRate.isPresent()) {
            ExchangeRate reverseExchangeRate = optionalReverseExchangeRate.get();
            reverseExchangeRate.setRate(BigDecimal.ONE.divide(reverseExchangeRate.getRate(), RoundingMode.HALF_UP));
            reverseExchangeRate.setId(null);
            reverseExchangeRate = currencyExchangeDAO.save(reverseExchangeRate);
            return exchangeRateMapper.toDTO(reverseExchangeRate);
        }
        Optional<ExchangeRate> optionalExchangeRateUSDBase = currencyExchangeDAO.get("USD", baseCurrencyCode);
        Optional<ExchangeRate> optionalExchangeRateUSDTarget = currencyExchangeDAO.get("USD", targetCurrencyCode);
        if (optionalExchangeRateUSDBase.isPresent() && optionalExchangeRateUSDTarget.isPresent()) {
            ExchangeRate exchangeRateUSDBase = optionalExchangeRateUSDBase.get();
            ExchangeRate exchangeRateUSDTarget = optionalExchangeRateUSDTarget.get();
            ExchangeRate calculatedExchangeRate = new ExchangeRate(null, exchangeRateUSDBase.getTargetCurrency(), exchangeRateUSDTarget.getTargetCurrency(), null);
            calculatedExchangeRate.setRate(exchangeRateUSDBase.getRate().divide(exchangeRateUSDTarget.getRate(), RoundingMode.HALF_UP));
            return exchangeRateMapper.toDTO(calculatedExchangeRate);
        }
        throw new ExchangeRateDoesntExistsException("The exchange rate could not be found");
    }
}
