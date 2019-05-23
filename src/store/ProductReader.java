package store;

import models.Product;
import shared.utils.AbstractCSVReader;

import java.io.FileNotFoundException;
import java.util.HashMap;

public class ProductReader extends AbstractCSVReader<HashMap<Integer, Product>> {

    public ProductReader(String path) throws FileNotFoundException {
        super(path, ",");
    }

    @Override
    protected void insert(String[] tuple, HashMap<Integer, Product> hashMap) {
        Product p = new ProductBuilder()
                .setId( Integer.parseInt(tuple[0]) )
                .setName( tuple[1] )
                .setQuantity( Integer.parseInt(tuple[2]) )
                .setValue( Integer.parseInt(tuple[3]) )
                .build();
        hashMap.put( p.getId() , p );
    }

    @Override
    protected HashMap<Integer, Product> instanciate() {
        return new HashMap<>();
    }
}
