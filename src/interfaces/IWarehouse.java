package interfaces;

import java.rmi.Remote;
import java.util.ArrayList;

public interface IWarehouse extends IRemoteTransaction {
    void register( String locatorHost );
    void lockResource( int tid, int resourceId );
    void releaseResource( int tid, int resourceId );
    void batchRelease( int tid , ArrayList<Integer> resourceIds);
}
