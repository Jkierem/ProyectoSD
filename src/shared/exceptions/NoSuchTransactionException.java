package shared.exceptions;

public class NoSuchTransactionException extends InvalidOperationException {
    public NoSuchTransactionException( int id ) {
        super("There does not exist transaction with id "+ String.valueOf(id));
    }
}
