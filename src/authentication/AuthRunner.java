package authentication;

import interfaces.IAuthentication;
import shared.logic.RMIServer;

public class AuthRunner {
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
            for( String key : auth.getUserBase().keySet() ){
                UserData data = auth.getUserBase().get(key);
                System.out.println( "User: " + key + " Password: " + data.getPassword() + " Balance:" + data.getBalace());
            }

            //auth.attemptChangeBalance();
            System.out.println("Server started...");
        } catch (Exception e) {
            System.out.println("Could not start server");
            e.printStackTrace();
        }
    }
}
