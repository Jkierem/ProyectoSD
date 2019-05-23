package store;

import interfaces.IStore;
import models.Product;
import shared.utils.Utils;

import java.io.IOException;
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
    public int getProductQuantity(int id) {
        if( this.inventory.containsKey(id) ){
            return this.inventory.get(id).getQuantity();
        }
        return 0;
    }

    @Override
    public HashMap<Integer,Product> batchGetProducts(List<Integer> ids) {
        return Utils.filterHashMap( inventory , product -> ids.contains( product.getId() ) );
    }

    @Override
    public HashMap<Integer,Product> batchGetProductCopies(List<Integer> ids) {
        return Utils.mapHashMap( this.batchGetProducts(ids) , Product::copy );
    }

    @Override
    public void batchSetProducts(HashMap<Integer,Product> products) {
        for( Integer key : products.keySet() ){
            this.inventory.put( key , products.get(key) );
        }
        try {
            this.writer.writeFile( this.inventory );
        } catch (IOException e) {
            e.printStackTrace();
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
