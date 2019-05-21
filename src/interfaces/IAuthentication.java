package interfaces;

import java.rmi.Remote;
import java.util.Optional;

public interface IAuthentication extends Remote {
  Optional<String> attemptLogin( String user , String password );
  void attemptLogout( String token );
  Boolean validateToken( String token );
}
