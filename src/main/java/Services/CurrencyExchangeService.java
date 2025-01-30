package Services;

import DAO.CurrencyDAO;
import DAO.CurrencyExchangeDAO;
import DTO.ExchangeRateDTO;
import Entity.Currency;
import Entity.ExchangeRate;
import Entity.ExchangeResult;
import exceptions.ExchangeRateDoesntExistsException;
import exceptions.InvalidInputException;
import mapper.ExchangeRateMapper;
import validation.CurrencyValidator;
import validation.ExchangeRateValidator;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.spi.CurrencyNameProvider;

public class CurrencyExchangeService {
    private final CurrencyExchangeDAO currencyExchangeDAO = CurrencyExchangeDAO.getInstance();
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.getInstance();
    private final ExchangeRateValidator exchangeRateValidator = new ExchangeRateValidator();
    private final CurrencyValidator currencyValidator = CurrencyValidator.getInstance();
    private final CurrencyDAO currencyDAO = CurrencyDAO.getInstance();
    private static final CurrencyExchangeService INSTANCE = new CurrencyExchangeService();
    private CurrencyExchangeService() {}
    public static CurrencyExchangeService getInstance() {
        return INSTANCE;
    }
    public List<ExchangeRateDTO> getAll() {
        return currencyExchangeDAO.getAll().stream().map(exchangeRateMapper::toDTO).toList();
    }
    public ExchangeRateDTO getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        if (!exchangeRateValidator.isValidCodes(baseCurrencyCode, targetCurrencyCode)) {
            throw new InvalidInputException("The codes should consist of 3 letters of the latin alphabet");
        }
        Optional<ExchangeRate> optionalExchangeRate = currencyExchangeDAO.get(baseCurrencyCode, targetCurrencyCode);
        if (optionalExchangeRate.isPresent()) {
            return exchangeRateMapper.toDTO(optionalExchangeRate.get());
        }
        throw new ExchangeRateDoesntExistsException("The exchange rate could not be found");
    }
    /* public ExchangeResult getExchangeResult() {
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
    } */
    public ExchangeRateDTO save(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        List<String> errorMessages = new ArrayList<>();
        Optional<Currency> baseCurrency = currencyDAO.get(baseCurrencyCode);
        Optional<Currency> targetCurrency = currencyDAO.get(targetCurrencyCode);
        if (!exchangeRateValidator.isValidCodes(baseCurrencyCode, targetCurrencyCode)) {
            errorMessages.add("Invalid codes given");
        }
        if (!exchangeRateValidator.isValidRate(rate)) {
            errorMessages.add("Invalid rate given");
        }
        if (!errorMessages.isEmpty()) {
            throw new InvalidInputException(String.join(" ", errorMessages));
        }
        if (baseCurrency.isEmpty()) {
            errorMessages.add("Currency with the code " + baseCurrencyCode + "could not be found");
        }
        if (targetCurrency.isEmpty()) {
            errorMessages.add("Currency with the code " + targetCurrency + "could not be found");
        }
    }
}
