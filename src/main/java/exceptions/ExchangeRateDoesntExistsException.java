package exceptions;

public class ExchangeRateDoesntExistsException extends RuntimeException {
    public ExchangeRateDoesntExistsException(String message) {
        super(message);
    }
}
