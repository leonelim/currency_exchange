package exceptions;

public class CurrencyDoesntExistException extends RuntimeException {
    public CurrencyDoesntExistException(String message) {
        super(message);
    }
}
