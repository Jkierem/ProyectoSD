package interfaces;

import shared.exceptions.InvalidOperationException;
import store.Product;

import java.rmi.Remote;
import java.util.List;

public interface IProductSystem extends Remote {
    void registerClient( String host );
    int startProductTransaction( String host );
    void abortProductTransaction( int tid ) throws InvalidOperationException;
    void finishProductTransaction( int tid ) throws InvalidOperationException;
    Product getProduct( int tid , int rid ) throws InvalidOperationException;
    List<Product> getAllProducts( int tid ) throws InvalidOperationException;
    void attemptUpdateProductQuantity( int tid , int rid , int quantity ) throws InvalidOperationException;
}
