package interfaces;

import java.rmi.Remote;

public interface IRemoteTransaction extends Remote {
    int startTransaction();
    void commitTransaction( int tid );
    void abortTransaction( int tid );
}
