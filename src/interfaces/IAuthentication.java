package interfaces;

import shared.logic.AuthResponse;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IAuthentication extends Remote {
  AuthResponse attemptLogin(String user , String password ) throws RemoteException;
  List<String> getUserList( String token ) throws RemoteException;
  int getBalance( String user ) throws RemoteException;
  int attemptChangeBalance( String user , int amount , String token ) throws RemoteException;
  int attemptChangeBalanceWithResponse(  String host , String binding , String user , int amount , String token ) throws RemoteException;
  void commitBalanceChange( int tid , String token ) throws RemoteException;
  void abortBalanceChange( int tid , String token ) throws RemoteException;
  boolean canCommitBalanceChange( int tid , String token ) throws RemoteException;
}
