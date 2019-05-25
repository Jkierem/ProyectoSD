package authentication;

import interfaces.IAuthentication;
import shared.exceptions.InvalidOperationException;
import shared.exceptions.ServerException;
import shared.logic.Operation;
import shared.exceptions.UnathorizedException;
import shared.exceptions.UserNotFoundException;
import shared.logic.Transaction;
import shared.logic.TransactionalSystem;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;


public class Authentication extends TransactionalSystem<String,Integer> implements IAuthentication {
    private static final String NEW_USER_PASSWORD="__NEW_USER_PASSWORD__";
    private static final String ADMIN_TOKEN="1";

    private HashMap<String, UserData> userBase;
    private UserWriter userWriter;

    public Authentication(String fileName) throws IOException{
        this.userBase = new UserReader(fileName, NEW_USER_PASSWORD).readFile();
        this.userWriter = new UserWriter(fileName);
    }

    public HashMap<String, UserData> getUserBase() {
        return userBase;
    }

    @Override
    public boolean attemptLogin(String user, String password) throws RemoteException {
        if ( this.userBase.containsKey(user) ){
            UserData userData = this.userBase.get(user);
            if( userData.getPassword().equals( NEW_USER_PASSWORD ) ){
                userData.setPassword(password);
                this.userBase.put( user , userData );
                try {
                    this.userWriter.writeFile( this.userBase );
                }catch ( Exception e) {
                    throw new ServerException();
                }
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
    public int getBalance(String user) throws RemoteException {
        this.validateUser(user);
        return this.userBase.get(user).getBalace();
    }

    @Override
    public int attemptChangeBalance(String user, int amount, String token) throws RemoteException {
        this.validateToken(token);
        this.validateUser(user);
        int tid = this.createTransaction("ADMIN");
        this.addWriteOperation( tid , user, amount);
        return tid;
    }

    @Override
    public void commitBalanceChange( int tid, String token) throws RemoteException {
        this.validateToken(token);
        this.commitTransaction( tid );
    }

    @Override
    public void abortBalanceChange( int tid, String token) throws RemoteException {
        this.validateToken(token);
    }

    @Override
    protected void alertAbort(Transaction<String, Integer> tx, boolean manual) throws InvalidOperationException {
        System.out.println("Aborted "+String.valueOf(tx.getId()));
    }

    @Override
    protected void alertCommit(Transaction<String, Integer> tx) throws InvalidOperationException {
        System.out.println("Commited "+String.valueOf(tx.getId()));
    }

    @Override
    protected void applyOperation(Transaction<String, Integer> tx, Operation<String, Integer> op) throws InvalidOperationException {
        UserData data = this.userBase.get( op.getRid() );
        data.setBalance( data.getBalace() + op.getQuantity().intValue() );
        this.userBase.put( op.getRid() , data );
        try {
            this.userWriter.writeFile( this.userBase );
        }catch ( Exception e) {
            throw new ServerException();
        }
    }
}
