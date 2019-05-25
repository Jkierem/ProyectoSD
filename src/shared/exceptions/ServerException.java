package shared.exceptions;

public class ServerException extends InvalidOperationException {
    public ServerException() {
        super("Something went wrong in the server");
    }
}
