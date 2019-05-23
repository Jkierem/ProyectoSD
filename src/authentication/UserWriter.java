package authentication;

import shared.utils.AbstractCSVWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserWriter extends AbstractCSVWriter<HashMap<String,String>> {

    public UserWriter(String path) throws IOException {
        super(path,",");
    }

    @Override
    protected Iterable<String[]> convertData(HashMap<String, String> c) {
        List<String[]> res = new ArrayList<>();
        for( String key : c.keySet()){
            String[] tuple = new String[2];
            tuple[0] = key;
            tuple[1] = c.get(key);
            res.add(tuple);
        }
        return res;
    }
}
