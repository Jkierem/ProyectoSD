package authentication;

import shared.utils.AbstractCSVWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserWriter extends AbstractCSVWriter<HashMap<String,UserData>> {

    public UserWriter(String path) throws IOException {
        super(path,",");
    }

    @Override
    protected Iterable<String[]> convertData(HashMap<String, UserData> c) {
        List<String[]> res = new ArrayList<>();
        for( String key : c.keySet()){
            String[] tuple = new String[3];
            tuple[0] = key;
            tuple[1] = String.valueOf(c.get(key).getBalace());
            tuple[2] = c.get(key).getPassword();
            res.add(tuple);
        }
        return res;
    }
}
