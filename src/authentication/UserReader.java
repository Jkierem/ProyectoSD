package authentication;

import shared.utils.AbstractCSVReader;

import java.io.FileNotFoundException;
import java.util.HashMap;

public class UserReader extends AbstractCSVReader<HashMap<String,String>> {

    private final String defaultPassword;

    public UserReader(String path, String defaultPassword) throws FileNotFoundException {
        super(path, ",");
        this.defaultPassword = defaultPassword;
    }

    @Override
    protected void insert(String[] tuple, HashMap<String, String> container) {
        if( tuple.length > 1 ){
            container.put(tuple[0],tuple[1]);
        }else if( tuple.length > 0 ){
            container.put(tuple[0], this.defaultPassword);
        }
    }

    @Override
    protected HashMap<String, String> instanciate() {
        return new HashMap<>();
    }
}
