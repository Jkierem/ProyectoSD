package interfaces;

import models.Product;

import java.util.HashMap;
import java.util.List;

public interface IStore {
    int getProductQuantity( int id );
    HashMap<Integer,Product> batchGetProducts(List<Integer> ids );
    HashMap<Integer,Product> batchGetProductCopies(List<Integer> ids );
    void batchSetProducts(HashMap<Integer,Product> products);
}
