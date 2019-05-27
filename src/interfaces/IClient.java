package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClient extends Remote {
    void alertCommit( int tid ) throws RemoteException;
    void alertAbort( int tid , boolean manual ) throws RemoteException;
}
