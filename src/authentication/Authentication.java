package authentication;

import interfaces.IAuthentication;
import interfaces.IClient;
import shared.exceptions.InvalidOperationException;
import shared.exceptions.ServerException;
import shared.logic.*;
import shared.exceptions.UnauthorizedException;
import shared.exceptions.UserNotFoundException;
import shared.utils.ConditionalLogger;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Authentication extends TransactionalSystem<String,Integer> implements IAuthentication {
    private static final String NEW_USER_PASSWORD="__NEW_USER_PASSWORD__";
    private static final String ADMIN_TOKEN="849cf295c3276fa8674b76535ba206e4f4ae2260977ccb4d4dabe897f20aeb83";

    private HashMap<String, UserData> userBase;
    private UserWriter userWriter;
    private ConditionalLogger logger;

    public Authentication(String fileName) throws IOException{
        this.userBase = new UserReader(fileName, NEW_USER_PASSWORD).readFile();
        this.userWriter = new UserWriter(fileName);
        this.logger = new ConditionalLogger(false);
    }

    public Authentication(String fileName, ConditionalLogger logger ) throws IOException{
        this.userBase = new UserReader(fileName, NEW_USER_PASSWORD).readFile();
        this.userWriter = new UserWriter(fileName);
        this.logger = logger;
    }

    public Authentication(String fileName, boolean verbose) throws IOException{
        this.userBase = new UserReader(fileName, NEW_USER_PASSWORD).readFile();
        this.userWriter = new UserWriter(fileName);
        this.logger = new ConditionalLogger(verbose,"Auth: ");
    }

    public HashMap<String, UserData> getUserBase() {
        return userBase;
    }

    private AuthResponse failedLogin(){
        return new AuthResponse(false,"");
    }

    private AuthResponse successfulLogin( boolean isAdmin){
        return new AuthResponse(true,isAdmin ? ADMIN_TOKEN : "" );
    }

    @Override
    public AuthResponse attemptLogin(String user, String password) throws RemoteException {
        this.logger.log("Attempting login with " + user);
        if ( this.userBase.containsKey(user) ){
            UserData userData = this.userBase.get(user);
            if( userData.getPassword().equals( NEW_USER_PASSWORD ) ){
                this.logger.log("Assigning new password to "+user);
                userData.setPassword(password);
                this.userBase.put( user , userData );
                try {
                    this.userWriter.writeFile( this.userBase );
                }catch ( Exception e) {
                    throw new ServerException();
                }
                return this.successfulLogin( userData.isAdmin() );
            }else{
                boolean result = this.userBase.get( user ).getPassword().equals( password );
                if( result ){
                    this.logger.log("Successful login with " + user);
                    return this.successfulLogin( userData.isAdmin() );
                }else{
                    this.logger.log("Failed login with " + user);
                    return this.failedLogin();
                }
            }
        }else{
            this.logger.log("Failed login with " + user);
            return this.failedLogin();
        }
    }

    private void validateUser( String user ) throws UserNotFoundException {
        if( !this.userBase.containsKey(user) ){
            this.logger.log("Attempting to get user: " + user);
            throw new UserNotFoundException(user);
        }
    }

    private void validateToken( String token ) throws UnauthorizedException {
        if( !token.equals(ADMIN_TOKEN) ){
            this.logger.log("Received invalid token: " + token );
            throw new UnauthorizedException(token);
        }
    }

    @Override
    public int getBalance(String user) throws RemoteException {
        this.validateUser(user);
        this.logger.log("Getting balance of user " + user );
        return this.userBase.get(user).getBalance();
    }

    @Override
    public int attemptChangeBalance(String user, int amount, String token) throws RemoteException {
        this.validateToken(token);
        this.validateUser(user);
        int tid = this.createTransaction("NONE");
        this.addWriteOperation( tid , user, amount);
        this.logger.log("Created transaction " + tid );
        return tid;
    }

    @Override
    public int attemptChangeBalanceWithResponse(String host, String binding, String user, int amount, String token) throws RemoteException {
        this.validateToken(token);
        this.validateUser(user);
        int tid = this.createTransaction(host, binding);
        this.addReadOperation( tid , user );
        this.addWriteOperation( tid , user, amount);
        this.logger.log("Created transaction " + tid );
        return tid;
    }

    @Override
    public void commitBalanceChange( int tid, String token) throws RemoteException {
        this.validateToken(token);
        this.logger.log("Received commit of transaction " + tid);
        this.commitTransaction( tid );
    }

    @Override
    public void abortBalanceChange( int tid, String token) throws RemoteException {
        this.validateToken(token);
        this.logger.log("Manual abort of transaction " + tid);
        this.manualAbort(tid);
    }

    private boolean validateTransactionOperations(Transaction<String,Integer> tx ){
        for( Operation<String,Integer> op : tx.getWriteOps() ){
            UserData data = this.userBase.get(op.getRid());
            if( (data.getBalance() + op.getQuantity().intValue()) < 0 ){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canCommitBalanceChange(int tid, String token) throws RemoteException {
        this.validateToken(token);
        Transaction<String,Integer> tx = this.getTransaction(tid);
        boolean result = this.validateTransactionOperations(tx);
        String str = result ? "SUCCESS" : "FAILURE";
        this.logger.log("Validating transaction " + tid + " Result: "+ str);
        return result;
    }

    @Override
    protected void alertAbort(Transaction<String, Integer> tx, boolean manual) throws RemoteException {
        this.logger.log("Aborted "+String.valueOf(tx.getId()));
        if( !tx.getHost().equals("NONE") ){
            RMIClient<IClient> client = new RMIClient<>(tx.getHost());
            IClient stub = client.getStub(tx.getBinding());
            stub.alertAbort(tx.getId(),manual);
        }
    }

    @Override
    protected void alertCommit(Transaction<String, Integer> tx) throws RemoteException {
        this.logger.log("Committed "+String.valueOf(tx.getId()));
        if( !tx.getHost().equals("NONE") ){
            RMIClient<IClient> client = new RMIClient<>(tx.getHost());
            IClient stub = client.getStub(tx.getBinding());
            stub.alertCommit(tx.getId());
        }
    }

    @Override
    protected void applyOperation(Transaction<String, Integer> tx, Operation<String, Integer> op) throws InvalidOperationException {
        UserData data = this.userBase.get( op.getRid() );
        data.setBalance( data.getBalance() + op.getQuantity().intValue() );
        this.userBase.put( op.getRid() , data );
        try {
            this.userWriter.writeFile( this.userBase );
        }catch ( Exception e) {
            throw new ServerException();
        }
    }

    @Override
    protected boolean beforeCommit(Transaction<String, Integer> tx) {
        return this.validateTransactionOperations( tx );
    }

    public void printUserBase(){
        for ( String key : this.userBase.keySet()){
            UserData data = this.userBase.get(key);
            StringBuilder builder = new StringBuilder();
            builder.append("User: ");
            builder.append(key);
            builder.append(" Password: ");
            builder.append(data.getPassword());
            builder.append(" Balance: ");
            builder.append(data.getBalance());
            builder.append(" IsAdmin: ");
            builder.append(data.isAdmin());
            System.out.println(builder.toString());
        }
    }

    @Override
    public List<String> getUserList(String token) throws RemoteException {
        this.validateToken(token);
        return new ArrayList<>(this.userBase.keySet());
    }
}
