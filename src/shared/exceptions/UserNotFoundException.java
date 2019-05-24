package shared.exceptions;

import shared.exceptions.InvalidOperationException;

public class UserNotFoundException extends InvalidOperationException {
    public UserNotFoundException(String message) {
        super("User not found: "+message);
    }
}
