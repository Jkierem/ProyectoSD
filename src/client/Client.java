package client;

import interfaces.IAuthentication;
import shared.logic.RMIClient;
import shared.utils.Utils;

import java.util.Optional;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {

        Optional<IAuthentication> auth = new RMIClient<IAuthentication>("localhost").getStub("Auth");
        if( !auth.isEmpty() ){
            IAuthentication authenticator = auth.get();
            System.out.println("Ingrese usuario: ");
            Scanner in = new Scanner(System.in);
            String user = in.nextLine().trim();
            System.out.println("Ingrese contraseña: ");
            String pass = Utils.sha256(in.nextLine().trim()).get();
            try {
                boolean success = authenticator.attemptLogin(user, pass);
                System.out.println(success);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            System.out.println("Something went wrong. Could not retrieve stub");
        }

    }
}
