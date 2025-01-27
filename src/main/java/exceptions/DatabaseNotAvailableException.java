package exceptions;

public class DatabaseNotAvailableException extends RuntimeException{
    public DatabaseNotAvailableException(String message) {
        super(message);
    }
}
