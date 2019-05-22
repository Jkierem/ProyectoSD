package interfaces;

import java.io.IOException;
import java.rmi.Remote;

public interface IAuthentication extends Remote {
  boolean attemptLogin( String user , String password ) throws IOException;
}
