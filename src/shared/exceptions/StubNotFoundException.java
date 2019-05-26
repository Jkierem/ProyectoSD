package shared.exceptions;

import shared.exceptions.InvalidOperationException;

public class StubNotFoundException extends InvalidOperationException {
    public StubNotFoundException(String stub, String host) {
        super("Could not acquire "+ stub +" stub in "+host);
    }
}
