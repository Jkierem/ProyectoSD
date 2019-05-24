package shared.exceptions;

public class InsufficientQuantityException extends InvalidOperationException {
    public InsufficientQuantityException() {
        super("Not enough quantity in stock");
    }
}
