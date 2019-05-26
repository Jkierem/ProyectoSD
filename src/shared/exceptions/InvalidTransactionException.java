package shared.exceptions;

public class InvalidTransactionException extends InvalidOperationException {
    public InvalidTransactionException(int tid) {
        super("Transaction did not pass optional validation. ID:" + tid);
    }
}
