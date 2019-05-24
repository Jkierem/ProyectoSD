package shared.exceptions;

import shared.exceptions.InvalidOperationException;

public class UnathorizedException extends InvalidOperationException {
    public UnathorizedException(String token) {
        super("Token is not valid: "+token);
    }
}
