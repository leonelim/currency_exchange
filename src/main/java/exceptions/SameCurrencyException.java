package exceptions;

public class SameCurrencyException extends RuntimeException {
    public SameCurrencyException(String message) {
        super(message);
    }
}
