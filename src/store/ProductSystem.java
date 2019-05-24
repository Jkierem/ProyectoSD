package store;

import interfaces.IClient;
import interfaces.IProductSystem;
import shared.exceptions.InvalidOperationException;
import shared.exceptions.NoSuchTransactionException;
import shared.logic.Operation;
import shared.logic.RMIClient;
import shared.logic.Transaction;
import shared.logic.TransactionalSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductSystem extends TransactionalSystem implements IProductSystem {
    private static int TRANSACTION_COUNT = 0;
    private static int VALIDATION_ORDER = 0;
    private Store store;
    private List<Transaction> trxs;
    private HashMap<String,RMIClient<IClient>> hosts;

    public ProductSystem(Store store) {
        this.store = store;
        this.trxs = new ArrayList<>();
        this.hosts = new HashMap<>();
    }

    public void registerClient( String host ){
        this.hosts.put(host, new RMIClient<>(host));
    }

    public int startTransaction(String host){
        if( !this.hosts.containsKey(host) ){
            this.registerClient(host);
        }
        TRANSACTION_COUNT++;
        this.trxs.add(new Transaction(TRANSACTION_COUNT,host));
        return TRANSACTION_COUNT;
    }

    public Product getProduct( int tid , int rid ) throws InvalidOperationException {
        Transaction t = this.getTransaction(tid);
        Product p = this.store.batchGetProductCopies(List.of(rid)).get(rid);
        t.addReadOperation(new Operation( p.getId() ));
        return p;
    }

    public List<Product> getAllProducts( int tid ) throws InvalidOperationException {
        Transaction tx = this.getTransaction(tid);
        HashMap<Integer,Product> ps = this.store.getAllProductCopies();
        List<Product> res = new ArrayList<>();
        for ( int key : ps.keySet() ){
            Operation op = new Operation( tid , key );
            tx.addReadOperation(op);
            res.add(ps.get(key));
        }
        return res;
    }

    public void attemptUpdateProductQuantity( int tid , int rid , int quantity ) throws InvalidOperationException {
        Transaction tx = this.getTransaction( tid );
        Operation op = new Operation( rid , quantity );
        tx.addWriteOperation(op);
    }

    public void abortTransaction( int tid ) throws InvalidOperationException {
        Transaction t = this.getTransaction(tid);
        t.markForRemoval();
        this.alertAbort( t , true);
        this.cleanTransactions();
    }

    private void abortTransaction( Transaction tx ) throws InvalidOperationException {
        tx.markForRemoval();
        this.alertAbort( tx , false );
        this.cleanTransactions();
    }

    public void commitTransaction( int tid ) throws InvalidOperationException {
        Transaction t = this.getTransaction(tid);
        if( !t.isReadOnly()){
            VALIDATION_ORDER++;
            t.markForValidation( VALIDATION_ORDER );
            this.validateTransactions( t );
            this.updateResources( t );
        }
        this.alertCommit( t );
        this.cleanTransactions();
    }

    private void alertAbort(Transaction tx , boolean manual ) throws InvalidOperationException{
        this.hosts.get( tx.getHost() ).getStub("Client").alertAbort( tx.getId() , manual);
    }

    private void alertCommit( Transaction tx ) throws InvalidOperationException{
        this.hosts.get( tx.getHost() ).getStub("Client").alertCommit( tx.getId() );
    }

    private void updateResources( Transaction tx ) throws InvalidOperationException{
        if( !tx.isReadOnly() ){
            HashMap<Integer,Product> products = new HashMap<>();
            for( Operation op : tx.getWriteOps() ){
                Product p = this.getProduct( tx.getId() , op.getRid() );
                p.changeQuantityBy( op.getQuantity() );
                products.put( p.getId() , p );
            }
            this.store.batchSetProducts(products);
        }
        tx.markForRemoval();
    }

    private void cleanTransactions(){
        this.trxs = this.trxs.stream()
                .filter(x -> !x.isReadyForRemoval())
                .collect(Collectors.toList());
    }

    private void validateTransactions( Transaction tv ) throws InvalidOperationException {
        for( Transaction ti : this.trxs ){
            if( !ti.equals(tv) ){
                if( !ti.isReadyForRemoval() && !ti.isAwaitingUpdate() ){
                    if( !tv.forwardValidate(ti) ){
                        this.abortTransaction( ti );
                    }
                }
            }
        }
    }

    private Optional<Transaction> findTransaction(int tid ){
        for( Transaction t : this.trxs ){
            if( t.getId() == tid ){
                return Optional.of( t );
            }
        }
        return Optional.empty();
    }

    private Transaction getTransaction(int tid ) throws NoSuchTransactionException {
        Optional<Transaction> maybeTrx = this.findTransaction(tid);
        if( maybeTrx.isEmpty() ){
            throw new NoSuchTransactionException(tid);
        }
        return maybeTrx.get();
    }

}
