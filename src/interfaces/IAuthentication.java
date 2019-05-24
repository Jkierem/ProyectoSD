package interfaces;

import shared.exceptions.InvalidOperationException;

import java.io.IOException;
import java.rmi.Remote;

public interface IAuthentication extends Remote {
  boolean attemptLogin( String user , String password ) throws IOException;
  int getBalance( String user ) throws InvalidOperationException;
  int attemptChangeBalance( String user , int amount , String token ) throws InvalidOperationException;
  void commitBalanceChange( String user , int tid , String token ) throws InvalidOperationException;
  void abortBalanceChange( String user , int tid , String token ) throws InvalidOperationException;
}
