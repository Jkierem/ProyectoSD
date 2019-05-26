package interfaces;

import java.rmi.Remote;

public interface IClient extends Remote {
    void alertCommit( int tid );
    void alertAbort( int tid , boolean manual );
}
