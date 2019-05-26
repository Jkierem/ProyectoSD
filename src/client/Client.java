package client;

import interfaces.IAuthentication;
import shared.logic.AuthResponse;
import shared.logic.RMIClient;
import shared.utils.Utils;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            IAuthentication authenticator = new RMIClient<IAuthentication>("localhost").getStub("Auth");
            System.out.println("Ingrese usuario: ");
            Scanner in = new Scanner(System.in);
            String user = in.nextLine().trim();
            System.out.println("Ingrese contraseña: ");
            String pass = Utils.sha256(in.nextLine().trim()).get();
            AuthResponse success = authenticator.attemptLogin(user, pass);
            String token = success.getToken();
            int tid = authenticator.attemptChangeBalance("NORMAL",200,token);
            if( authenticator.canCommitBalanceChange( tid , token) ){
                authenticator.commitBalanceChange(tid,"12415");
            }else{
                authenticator.abortBalanceChange(tid,"123");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
