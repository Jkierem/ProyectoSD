package interfaces;

import shared.exceptions.InvalidOperationException;
import shared.exceptions.NoSuchTransactionException;
import store.Product;

import java.rmi.Remote;
import java.util.List;

public interface IProductSystem extends Remote {
    void registerClient( String host );
    int startTransaction( String host );
    Product getProduct(int tid , int rid ) throws InvalidOperationException;
    List<Product> getAllProducts(int tid ) throws InvalidOperationException;
    void attemptUpdateProductQuantity( int tid , int rid , int quantity ) throws InvalidOperationException;
    void abortTransaction( int tid ) throws InvalidOperationException;
    void commitTransaction( int tid ) throws InvalidOperationException;
}
