package store;

import interfaces.IStore;
import shared.exceptions.InvalidOperationException;
import shared.exceptions.NonexistentProductException;
import shared.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Store implements IStore {

    HashMap<Integer,Product> inventory;
    ProductWriter writer;

    public Store(String path) throws IOException {
        this.inventory = new ProductReader(path).readFile();
        this.writer = new ProductWriter(path);
    }

    @Override
    public HashMap<Integer,Product> batchGetProducts(List<Integer> ids) throws NonexistentProductException {
        this.validateKeys( ids );
        return Utils.filterHashMap( inventory , product -> ids.contains( product.getId() ) );
    }

    @Override
    public HashMap<Integer,Product> batchGetProductCopies(List<Integer> ids) throws NonexistentProductException {
        return Utils.mapHashMap( this.batchGetProducts(ids) , Product::copy );
    }

    @Override
    public void batchSetProducts(HashMap<Integer,Product> products) throws InvalidOperationException {
        this.validateKeys(products.keySet());
        for( Integer key : products.keySet() ){
            this.inventory.put( key , products.get(key) );
        }
        try {
            this.writer.writeFile( this.inventory );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProduct(Product p) throws InvalidOperationException {
        this.validateKeys(List.of(p.getId()));
        this.inventory.put( p.getId() , p );
        try {
            this.writer.writeFile(this.inventory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<Integer, Product> getAllProductCopies() throws NonexistentProductException {
        return this.batchGetProductCopies( List.copyOf(this.inventory.keySet()) );
    }

    private void validateKeys( Iterable<Integer> keys ) throws NonexistentProductException {
        List<Integer> falseKeys = new ArrayList<>();
        for( Integer key : keys ){
            if( !this.inventory.containsKey(key) ){
                falseKeys.add(key);
            }
        }
        if( !falseKeys.isEmpty() ){
            if( falseKeys.size() == 1 ){
                throw new NonexistentProductException( falseKeys.get(0) );
            }else{
                throw new NonexistentProductException( falseKeys );
            }
        }
    }

    public static void main(String[] args) {
        try {
            Store store = new Store(args[0]);
            List<Integer> ids = List.of(4,5);
            HashMap<Integer,Product>  changed = store.batchGetProductCopies(ids);
            for( Integer key : changed.keySet() ){
                changed.get(key).changeQuantityBy(-10);
            }
            store.batchSetProducts(changed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
