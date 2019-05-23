package authentication;

import interfaces.IAuthentication;
import shared.logic.RMIServer;

import java.io.IOException;
import java.util.HashMap;

public class Authentication implements IAuthentication {
    private HashMap<String,String> userBase;
    private UserWriter userWriter;
    private static final String NEW_USER_PASSWORD="__NEW_USER_PASSWORD__";

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
        if( args.length < 1 ){
            System.out.println("Please supply an argument for the users file");
        }
        String fname = args[0];
        try {
            Authentication auth = new Authentication(fname);
            RMIServer<Authentication, IAuthentication> server = new RMIServer<>(() -> auth);
            System.out.println("Rebinding Auth...");
            server.createAndRebind("Auth");
            for( String key : auth.userBase.keySet() ){
                System.out.println( key + ":" + auth.userBase.get(key));
            }
            System.out.println("Server started...");
        } catch (Exception e) {
            System.out.println("Could not start server");
            e.printStackTrace();
        }
    }
}
