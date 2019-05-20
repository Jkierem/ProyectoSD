package logic;

import java.rmi.Remote;
import java.util.ArrayList;

public interface Store extends Remote {
    int getResourceQuantity(int tid, int id);
    int setReourceQuantity(int tid, int id , int quantity);

    int startTransaction(ArrayList<Integer> resourceIds);
    void abort(int tid);
    void commit(int tid);
}
