package exceptions;

public class DatabaseCouldNotBeAccessedException extends RuntimeException{
    public DatabaseCouldNotBeAccessedException(String message) {
        super(message);
    }
}
