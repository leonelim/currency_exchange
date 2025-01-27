package Services;

import DAO.CurrencyDAO;
import DTO.CurrencyDTO;
import Entity.Currency;
import exceptions.CurrencyAlreadyExistsException;
import exceptions.CurrencyDoesntExistException;
import exceptions.InvalidInputException;
import mapper.CurrencyMapper;
import validation.CurrencyValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private static CurrencyDAO currencyDAO = CurrencyDAO.getInstance();
    private static CurrencyMapper currencyMapper = CurrencyMapper.getInstance();
    private static CurrencyValidator currencyValidator = CurrencyValidator.getInstance();
    private static final CurrencyService INSTANCE = new CurrencyService();
    private CurrencyService() {}
    public static CurrencyService getInstance() {
        return INSTANCE;
    }
    public List<CurrencyDTO> getAll() {
        return currencyDAO.getAll().stream().map(currencyMapper::toDto).toList();
    }
    public CurrencyDTO get(String code) {
        if (!currencyValidator.isValidCode(code)) {
            throw new InvalidInputException("The provided code is invalid, a code should consist of 3 letters of the latin alphabet");
        }
        Optional<Currency> optionalCurrency = currencyDAO.get(code);
        Currency currency = optionalCurrency.orElseThrow(() -> new CurrencyDoesntExistException("The currency with the code" + code + "could not be found"));
        return currencyMapper.toDto(currency);
    }
    public CurrencyDTO save(CurrencyDTO currencyDTO) {
        List<String> errorMessages = new ArrayList<>();
        if (!currencyValidator.isValidCode(currencyDTO.code())) {
            errorMessages.add("The code should consist of 3 letters of the latin alphabet");
        }
        if (!currencyValidator.isValidName(currencyDTO.name())) {
            errorMessages.add("The name should be between 3 and 30 letters of the latin alphabet long");
        }
        if (!currencyValidator.isValidSign(currencyDTO.sign())) {
            errorMessages.add("The sign should not be longer than 3 symbols");
        }
        if (!errorMessages.isEmpty()) {
            throw new InvalidInputException(String.join(" ", errorMessages));
        }
        Optional<Currency> optionalCurrency = currencyDAO.get(currencyDTO.code());
        if (optionalCurrency.isPresent()) {
            throw new CurrencyAlreadyExistsException("A currency with the code" + currencyDTO.code() + "already exists");
        }
        Currency currency = currencyDAO.save(currencyMapper.toEntity(currencyDTO));
        return currencyMapper.toDto(currency);
    }
}
