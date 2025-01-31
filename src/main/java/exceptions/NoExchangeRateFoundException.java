package exceptions;

public class NoExchangeRateFoundException extends RuntimeException {
    public NoExchangeRateFoundException(String message) {
        super(message);
    }
}
