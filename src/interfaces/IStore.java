package interfaces;

import shared.exceptions.NonexistentProductException;
import store.Product;
import shared.exceptions.InvalidOperationException;

import java.util.HashMap;
import java.util.List;

public interface IStore {
    HashMap<Integer,Product> batchGetProducts(List<Integer> ids ) throws NonexistentProductException;
    HashMap<Integer,Product> batchGetProductCopies(List<Integer> ids ) throws NonexistentProductException;
    HashMap<Integer,Product> getAllProductCopies() throws NonexistentProductException;
    void batchSetProducts(HashMap<Integer,Product> products) throws InvalidOperationException;
}
