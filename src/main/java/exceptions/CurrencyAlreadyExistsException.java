package exceptions;

public class CurrencyAlreadyExistsException extends RuntimeException {
    public CurrencyAlreadyExistsException(String message) {
        super(message);
    }
}
