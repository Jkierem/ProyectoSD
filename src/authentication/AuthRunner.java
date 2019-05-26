package authentication;

import interfaces.IAuthentication;
import shared.logic.RMIServer;
import shared.utils.ConditionalLogger;

public class AuthRunner {
    public static void main(String[] args) {
        if( args.length < 1 ){
            System.out.println("Please supply an argument for the users file");
        }
        String fname = args[0];
        try {
            ConditionalLogger logger = new ConditionalLogger(true,"Auth: ");
            Authentication auth = new Authentication(fname,logger);
            auth.printUserBase();
            RMIServer<Authentication, IAuthentication> server = new RMIServer<>(() -> auth);
            System.out.println("Rebinding Auth...");
            server.createAndRebind("Auth");
            System.out.println("Server started...");
        } catch (Exception e) {
            System.out.println("Could not start server");
            e.printStackTrace();
        }
    }
}
