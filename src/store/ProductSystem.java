package store;

import interfaces.IClient;
import interfaces.IProductSystem;
import shared.exceptions.InvalidOperationException;
import shared.logic.Operation;
import shared.logic.RMIClient;
import shared.logic.Transaction;
import shared.logic.TransactionalSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductSystem extends TransactionalSystem<Integer,Integer> implements IProductSystem {

    private Store store;
    private HashMap<String,RMIClient<IClient>> hosts;

    public ProductSystem(Store store) {
        super();
        this.store = store;
        this.hosts = new HashMap<>();
    }

    @Override
    public void registerClient( String host ){
        this.hosts.put(host, new RMIClient<>(host));
    }

    @Override
    public int startProductTransaction(String host){
        if( !this.hosts.containsKey(host) ){
            this.registerClient(host);
        }
        return this.createTransaction(host);
    }

    @Override
    public void abortProductTransaction( int tid ) throws InvalidOperationException {
        this.manualAbort( tid );
    }

    @Override
    public void finishProductTransaction(int tid) throws InvalidOperationException {
        this.commitTransaction( tid );
    }

    @Override
    public Product getProduct( int tid , int rid ) throws InvalidOperationException {
        Product p = this.getSingleProduct( rid );
        this.addReadOperation(tid,rid);
        return p;
    }

    @Override
    public List<Product> getAllProducts( int tid ) throws InvalidOperationException {
        HashMap<Integer,Product> ps = this.store.getAllProductCopies();
        List<Product> res = new ArrayList<>();
        for ( int key : ps.keySet() ){
            this.addReadOperation(tid,key);
        }
        return res;
    }

    @Override
    public void attemptUpdateProductQuantity( int tid , int rid , int quantity ) throws InvalidOperationException {
        this.addWriteOperation(tid,rid,quantity);
    }

    private Product getSingleProduct( int rid ) throws InvalidOperationException {
        return this.store.batchGetProductCopies(List.of(rid)).get(rid);
    }

    @Override
    protected void alertAbort(Transaction<Integer,Integer> tx , boolean manual ) throws InvalidOperationException{
        this.hosts.get( tx.getHost() ).getStub("Client").alertAbort( tx.getId() , manual);
    }

    @Override
    protected void alertCommit( Transaction<Integer,Integer> tx ) throws InvalidOperationException{
        this.hosts.get( tx.getHost() ).getStub("Client").alertCommit( tx.getId() );
    }

    @Override
    protected void applyOperation(Transaction<Integer,Integer> tx, Operation<Integer,Integer> op) throws InvalidOperationException {
        Product p = this.getSingleProduct( op.getRid() );
        p.changeQuantityBy( op.getQuantity().intValue() );
        this.store.setProduct(p);
    }
}
