package authentication;

import interfaces.IAuthentication;
import shared.exceptions.InvalidOperationException;
import shared.logic.RMIServer;
import shared.exceptions.UnathorizedException;
import shared.exceptions.UserNotFoundException;
import shared.logic.Transaction;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Authentication implements IAuthentication {
    private static final String NEW_USER_PASSWORD="__NEW_USER_PASSWORD__";
    private static final String ADMIN_TOKEN="1";

    private HashMap<String, UserData> userBase;
    private UserWriter userWriter;
    private List<Transaction> transactions;

    public Authentication(String fileName) throws IOException{
        this.userBase = new UserReader(fileName, NEW_USER_PASSWORD).readFile();
        this.userWriter = new UserWriter(fileName);
    }

    @Override
    public boolean attemptLogin(String user, String password) throws IOException {
        if ( this.userBase.containsKey(user) ){
            UserData userData = this.userBase.get(user);
            if( userData.getPassword().equals( NEW_USER_PASSWORD ) ){
                userData.setPassword(password);
                this.userBase.put( user , userData );
                this.userWriter.writeFile( this.userBase );
                return true;
            }else{
                return this.userBase.get( user ).getPassword().equals( password );
            }
        }else{
            return false;
        }
    }

    private void validateUser( String user ) throws UserNotFoundException {
        if( !this.userBase.containsKey(user) ){
            throw new UserNotFoundException(user);
        }
    }

    private void validateToken( String token ) throws UnathorizedException {
        if( !token.equals(ADMIN_TOKEN) ){
            throw new UnathorizedException(token);
        }
    }

    @Override
    public int getBalance(String user) throws InvalidOperationException {
        this.validateUser(user);
        return this.userBase.get(user).getBalace();
    }

    @Override
    public int attemptChangeBalance(String user, int amount, String token) throws InvalidOperationException {
        this.validateToken(token);
        this.validateUser(user);
        //CREATE TRANSACTION
        return 0;
    }

    @Override
    public void commitBalanceChange(String user, int tid, String token) throws InvalidOperationException {
        this.validateToken(token);
        this.validateUser(user);

        //ABORT OTHER TRANSACTIONS
    }

    @Override
    public void abortBalanceChange(String user, int tid, String token) throws InvalidOperationException {
        this.validateToken(token);
        this.validateUser(user);
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
