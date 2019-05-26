package authentication;

import shared.utils.AbstractCSVReader;
import shared.utils.OptionalList;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

public class UserReader extends AbstractCSVReader<HashMap<String, UserData>> {

    private final String defaultPassword;

    public UserReader(String path, String defaultPassword) throws FileNotFoundException {
        super(path, ",");
        this.defaultPassword = defaultPassword;
    }

    @Override
    protected void insert(String[] tuple, HashMap<String, UserData> container) {
        OptionalList<String> data = new OptionalList<>(List.of(tuple));
        String id = data.get(0);
        int balance = data.getOrElseMap(1,0, Integer::parseInt);
        String pass = data.getOrElse(2, this.defaultPassword);
        boolean isAdmin = data.getOrElseMap(3,false,Boolean::parseBoolean);
        UserData userData = new UserData(pass,balance,isAdmin);
        container.put(id,userData);
    }

    @Override
    protected HashMap<String, UserData> instanciate() {
        return new HashMap<>();
    }
}
