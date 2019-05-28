package store;

import interfaces.IAuthentication;
import interfaces.IClient;
import interfaces.IProductSystem;
import shared.exceptions.InvalidOperationException;
import shared.exceptions.UnauthorizedException;
import shared.logic.*;
import shared.utils.ConditionalLogger;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductSystem extends TransactionalSystem<Integer,Integer> implements IProductSystem {

    private class ClientData {
        private RMIClient<IClient> client;
        private String binding;

        public ClientData(RMIClient<IClient> client, String binding) {
            this.client = client;
            this.binding = binding;
        }

        public RMIClient<IClient> getClient() {
            return client;
        }

        public String getBinding() {
            return binding;
        }
    }

    private Store store;
    private RMIClient<IAuthentication> authClient;
    private HashMap<String,ClientData> hosts;
    private ConditionalLogger logger;

    private static final String TOKEN = "849cf295c3276fa8674b76535ba206e4f4ae2260977ccb4d4dabe897f20aeb83";

    private void validateToken( String token ) throws UnauthorizedException {
        if( !token.equals(TOKEN) ){
            this.logger.log("Received invalid token: " + token );
            throw new UnauthorizedException(token);
        }
    }

    public ProductSystem(Store store, String authHost) {
        super();
        this.store = store;
        this.authClient = new RMIClient<>(authHost);
        this.hosts = new HashMap<>();
        this.logger = new ConditionalLogger(false,"Market: ");
    }

    public ProductSystem(Store store, String authHost, boolean verbose) {
        super();
        this.store = store;
        this.authClient = new RMIClient<>(authHost);
        this.hosts = new HashMap<>();
        this.logger = new ConditionalLogger(verbose,"Market: ");
    }

    @Override
    public void registerClient( String host , String binding ){
        this.logger.log("Registering host: " + host);
        RMIClient<IClient> client = new RMIClient<>(host);
        this.hosts.put(host, new ClientData(client,binding));
    }

    @Override
    public int startProductTransaction(String host , String binding){
        if( !this.hosts.containsKey(host) ){
            this.registerClient(host,binding);
        }
        int tid = this.createTransaction(host);
        this.logger.log("Transaction created id " + tid);
        return tid;
    }

    @Override
    public void abortProductTransaction( int tid ) throws RemoteException {
        this.manualAbort( tid );
    }

    @Override
    public void attemptPurchase(int tid , String user ) throws RemoteException {
        this.logger.log("Attempting purchase of transaction " + tid + " by user " + user);
        Transaction<Integer,Integer> tx = this.getTransaction(tid);
        if( !this.canCommitProductTransaction(tx) ){
            this.forceAbort( tx );
        }else{
            IAuthentication auth = this.authClient.getStub("Auth");
            int authId = auth.attemptChangeBalance(user, getTotalCost(tx),TOKEN);

            if( !auth.canCommitBalanceChange(authId,TOKEN) ){
                auth.abortBalanceChange(authId,TOKEN);
                this.forceAbort( tx );
            }else{
                auth.commitBalanceChange(authId,TOKEN);
                this.commitTransaction( tid );
            }

        }
    }

    @Override
    public void attemptAdminRestock(int tid, String token) throws RemoteException {
        this.logger.log("Attempting transaction " + tid + " by admin");
        this.validateToken(token);
        Transaction<Integer,Integer> tx = this.getTransaction(tid);
        if( !this.canCommitProductTransaction(tx) ){
            this.forceAbort( tx );
        }else {
            this.commitTransaction(tid);
        }
    }

    @Override
    public Product getProduct( int tid , int rid ) throws RemoteException {
        Product p = this.getSingleProduct( rid );
        this.addReadOperation(tid,rid);
        return p;
    }

    @Override
    public List<Product> getAllProducts( ) throws RemoteException {
        this.logger.log("Get of all products");
        HashMap<Integer,Product> ps = this.store.getAllProductCopies();
        List<Product> res = new ArrayList<>();
        for ( int key : ps.keySet() ){
            res.add( ps.get(key) );
        }
        return res;
    }

    @Override
    public void attemptUpdateProductQuantity( int tid , int rid , int quantity ) throws RemoteException {
        this.logger.log("Adding write operation to " + tid + " on resource "+ rid + " with quantity of " + quantity );
        this.addWriteOperation(tid,rid,quantity);
    }

    private Product getSingleProduct( int rid ) throws InvalidOperationException {
        return this.store.batchGetProductCopies(List.of(rid)).get(rid);
    }

    @Override
    protected void alertAbort(Transaction<Integer,Integer> tx , boolean manual ) throws RemoteException {
        this.logger.log("Signaling abort of " + tx.getId() + " manual: " + manual);
        ClientData client = this.hosts.get(tx.getHost());
        String binding = client.getBinding();
        client.getClient().getStub(binding).alertAbort( tx.getId() , manual);
    }

    @Override
    protected void alertCommit( Transaction<Integer,Integer> tx ) throws RemoteException{
        this.logger.log("Signaling commit of " + tx.getId() );
        ClientData client = this.hosts.get(tx.getHost());
        String binding = client.getBinding();
        this.logger.log("Binding: " + binding);
        client.getClient().getStub(binding).alertCommit( tx.getId() );
    }

    @Override
    protected void applyOperation(Transaction<Integer,Integer> tx, Operation<Integer,Integer> op) throws RemoteException {
        Product p = this.getSingleProduct( op.getRid() );
        this.logger.log("" + -1 * op.getQuantity().intValue());
        p.changeQuantityBy( -1 * op.getQuantity().intValue() );
        this.store.setProduct(p);
    }

    private boolean canCommitProductTransaction( Transaction<Integer,Integer> tx ){
        try{
            for( Operation<Integer,Integer> op : tx.getWriteOps() ){
                Product p = this.getSingleProduct(op.getRid());
                if( p.getQuantity() + (-1 * op.getQuantity().intValue()) < 0 ){
                    return false;
                }
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private int getTotalCost(Transaction<Integer,Integer> tx ) throws InvalidOperationException{
        int total = 0;
        for( Operation<Integer,Integer> op : tx.getWriteOps() ){
            total += -1 * op.getQuantity().intValue() * this.getSingleProduct(op.getRid()).getValue();
        }
        return total;
    }
}
