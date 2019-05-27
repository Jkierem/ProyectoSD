package authentication;

import interfaces.IAuthentication;
import shared.logic.RMIServer;
import shared.utils.OptionalList;

import java.util.List;

public class AuthRunner {
    @SuppressWarnings("Duplicates")
    public static void main(String[] args) {
        OptionalList<String> argv = new OptionalList<>(List.of(args));
        boolean verbose = argv.containsAny(List.of("-v","--verbose"));
        String fileName = argv.findNextOrElse("--path","No path supplied");
        System.out.println("Users file: " + fileName);
        System.out.println("Verbose: " + verbose);
        try {
            Authentication auth = new Authentication(fileName,verbose);
            if( verbose ){
                auth.printUserBase();
            }
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
