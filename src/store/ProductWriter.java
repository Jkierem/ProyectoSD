package store;

import shared.utils.AbstractCSVWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductWriter extends AbstractCSVWriter<HashMap<Integer, Product>> {

    public ProductWriter(String path) throws IOException {
        super(path, ",");
    }

    @Override
    protected Iterable<String[]> convertData(HashMap<Integer, Product> c) {
        List<String[]> data = new ArrayList<>();
        for ( Integer key : c.keySet() ){
            data.add( c.get(key).toTuple() );
        }
        return data;
    }
}
