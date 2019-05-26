package shared.exceptions;

import shared.exceptions.InvalidOperationException;

public class UnauthorizedException extends InvalidOperationException {
    public UnauthorizedException(String token) {
        super("Token is not valid: "+token);
    }
}
