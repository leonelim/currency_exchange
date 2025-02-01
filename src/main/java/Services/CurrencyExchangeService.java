package Services;

import DAO.CurrencyDAO;
import DAO.CurrencyExchangeDAO;
import DTO.CurrencyDTO;
import DTO.ExchangeRateDTO;
import DTO.ExchangeResultDTO;
import Entity.Currency;
import Entity.ExchangeRate;
import Entity.ExchangeResult;
import exceptions.ExchangeRateDoesntExistsException;
import exceptions.InvalidInputException;
import exceptions.NoExchangeRateFoundException;
import mapper.CurrencyMapper;
import mapper.ExchangeRateMapper;
import validation.CurrencyValidator;
import validation.ExchangeRateValidator;

import javax.swing.text.html.Option;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.spi.CurrencyNameProvider;

public class CurrencyExchangeService {
    private final CurrencyExchangeDAO currencyExchangeDAO = CurrencyExchangeDAO.getInstance();
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.getInstance();
    private final ExchangeRateValidator exchangeRateValidator = new ExchangeRateValidator();
    private final CurrencyValidator currencyValidator = CurrencyValidator.getInstance();
    private final CurrencyMapper currencyMapper = CurrencyMapper.getInstance();
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
    public ExchangeResultDTO currencyExchange(String baseCurrencyCode, String targetCurrencyCode, String amount) {
        Optional<ExchangeRate> optionalExchangeRate = currencyExchangeDAO.get(baseCurrencyCode, targetCurrencyCode);
        BigDecimal moneyAmount = new BigDecimal(amount);
        if (optionalExchangeRate.isPresent()) {
            ExchangeRate exchangeRate = optionalExchangeRate.get();
            BigDecimal convertedAmount = moneyAmount.multiply(exchangeRate.getRate());
            return new ExchangeResultDTO(currencyMapper.toDto(exchangeRate.getBaseCurrency()), currencyMapper.toDto(exchangeRate.getTargetCurrency()), exchangeRate.getRate(), moneyAmount, convertedAmount);
        }
        Optional<ExchangeRate> optionalInverseExchangeRate = currencyExchangeDAO.get(targetCurrencyCode, baseCurrencyCode);
        if (optionalInverseExchangeRate.isPresent()) {
            ExchangeRate inverseExchangeRate = optionalInverseExchangeRate.get();
            BigDecimal inverseRate = BigDecimal.ONE.divide(inverseExchangeRate.getRate(), 6, RoundingMode.HALF_UP);
            BigDecimal convertedAmount = inverseRate.multiply(moneyAmount);
            return new ExchangeResultDTO(currencyMapper.toDto(inverseExchangeRate.getTargetCurrency()), currencyMapper.toDto(inverseExchangeRate.getBaseCurrency()), inverseRate, moneyAmount, convertedAmount);
        }
        Optional<ExchangeRate> optionalExchangeRateUSDBase = currencyExchangeDAO.get("USD", baseCurrencyCode);
        Optional<ExchangeRate> optionalExchangeRateUSDTarget = currencyExchangeDAO.get("USD", targetCurrencyCode);
        if (optionalExchangeRateUSDBase.isPresent() && optionalExchangeRateUSDTarget.isPresent()) {
            ExchangeRate exchangeRateUSDBase = optionalExchangeRateUSDBase.get();
            ExchangeRate exchangeRateUSDTarget = optionalExchangeRateUSDTarget.get();
            BigDecimal rate = exchangeRateUSDBase.getRate().divide(exchangeRateUSDTarget.getRate(), 6, RoundingMode.HALF_UP);
            BigDecimal convertedAmount = moneyAmount.multiply(rate);
            return new ExchangeResultDTO(currencyMapper.toDto(exchangeRateUSDBase.getTargetCurrency()), currencyMapper.toDto(exchangeRateUSDTarget.getTargetCurrency()), rate, moneyAmount, convertedAmount);
        }
        throw new NoExchangeRateFoundException("Failed to convert currency");
    }
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
        if (!errorMessages.isEmpty()) {
            throw new InvalidInputException(String.join(" ", errorMessages));
        }
        ExchangeRate exchangeRate = new ExchangeRate(null, baseCurrency.get(), targetCurrency.get(), new BigDecimal(rate));
        ExchangeRate savedExchangeRate = currencyExchangeDAO.save(exchangeRate);
        return exchangeRateMapper.toDTO(savedExchangeRate);
    }
    public ExchangeRateDTO update(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        Optional<ExchangeRate> optionalExchangeRate = currencyExchangeDAO.get(baseCurrencyCode, targetCurrencyCode);
        if (optionalExchangeRate.isEmpty()) {
            throw new InvalidInputException("The exchange rate doesn't exist");
        }
        if (!exchangeRateValidator.isValidRate(rate)) {
            throw new InvalidInputException("Invalid rate");
        }
        ExchangeRate updatedExchangeRate = currencyExchangeDAO.update(optionalExchangeRate.get(), new BigDecimal(rate));
        return exchangeRateMapper.toDTO(updatedExchangeRate);
    }
}
