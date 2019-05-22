package authentication;

import interfaces.IAuthentication;
import shared.logic.RMIServer;
import shared.utils.UserReader;
import shared.utils.UserWriter;

import java.io.IOException;
import java.util.HashMap;

public class Authentication implements IAuthentication {
    public HashMap<String,String> userBase;
    public UserWriter userWriter;
    public static final String NEW_USER_PASSWORD="__NEW_USER_PASSWORD__";

    public Authentication(String fileName) throws IOException{
        this.userBase = new UserReader(fileName, NEW_USER_PASSWORD).readFile();
        this.userWriter = new UserWriter(fileName);
    }

    @Override
    public boolean attemptLogin(String user, String password) throws IOException {
        if ( this.userBase.containsKey(user) ){
            if( this.userBase.get(user).equals( NEW_USER_PASSWORD ) ){
                this.userBase.put( user , password );
                this.userWriter.writeFile( this.userBase );
                return true;
            }else{
                return this.userBase.get( user ).equals( password );
            }
        }else{
            return false;
        }
    }

    public static void main(String[] args) {
        String fname = "/home/juan/Documents/Programacion/IntroSistemasDistribuidos/ProyectoSD/src/users.txt";
        try {
            Authentication auth = new Authentication(fname);
            RMIServer<Authentication, IAuthentication> server = new RMIServer<>(() -> auth);
            server.createAndRegister("Auth");
            System.out.println("Server started...");
        } catch (Exception e) {
            System.out.println("Could not start server");
            e.printStackTrace();
        }
    }
}
