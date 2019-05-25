package shared.exceptions;

import java.rmi.RemoteException;

public class InvalidOperationException extends RemoteException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
