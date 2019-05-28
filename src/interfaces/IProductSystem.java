package interfaces;

import store.Product;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IProductSystem extends Remote {
    void registerClient( String host , String binding ) throws RemoteException;
    int startProductTransaction( String host , String binding ) throws RemoteException;
    void abortProductTransaction( int tid ) throws RemoteException;
    void attemptPurchase(int tid , String user ) throws RemoteException;
    Product getProduct( int tid , int rid ) throws RemoteException;
    List<Product> getAllProducts( ) throws RemoteException;
    void attemptUpdateProductQuantity( int tid , int rid , int quantity ) throws RemoteException;
    void attemptAdminRestock( int tid , String token ) throws RemoteException;
}
