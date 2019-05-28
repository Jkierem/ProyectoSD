package shared.logic;

import shared.exceptions.InvalidOperationException;
import shared.exceptions.InvalidTransactionException;
import shared.exceptions.NoSuchTransactionException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class TransactionalSystem<ID,Quantity extends Number> {

    private static int TRANSACTION_COUNT = 0;
    private List<Transaction<ID,Quantity>> transactions = new ArrayList<>();

    public TransactionalSystem() {
    }

    protected int createTransaction(String host){
        TRANSACTION_COUNT++;
        this.transactions.add(new Transaction<>(TRANSACTION_COUNT,host));
        return TRANSACTION_COUNT;
    }

    protected int createTransaction(String host,String binding){
        TRANSACTION_COUNT++;
        this.transactions.add(new Transaction<>(TRANSACTION_COUNT,host,binding));
        return TRANSACTION_COUNT;
    }

    protected void manualAbort( int tid ) throws RemoteException {
        Transaction<ID,Quantity> t = this.getTransaction(tid);
        t.markForRemoval();
        this.alertAbort( t , true);
        this.cleanTransactions();
    }

    protected void forceAbort(Transaction<ID,Quantity> tx) throws RemoteException {
        tx.markForRemoval();
        this.alertAbort(tx, false );
        this.cleanTransactions();
    }

    private void cleanTransactions(){
        this.transactions = this.transactions.stream()
                .filter(x -> !x.isReadyForRemoval())
                .collect(Collectors.toList());
    }

    private void validateTransactions( Transaction<ID,Quantity> tv ) throws RemoteException {
        for( Transaction<ID,Quantity> ti : this.transactions){
            if( !ti.equals(tv) ){
                if( !ti.isReadyForRemoval() && !ti.isAwaitingUpdate() ){
                    if( !tv.forwardValidate(ti) ){
                        this.forceAbort( ti );
                    }
                }
            }
        }
    }

    private Optional<Transaction<ID,Quantity>> findTransaction(int tid ){
        for( Transaction<ID,Quantity> t : this.transactions){
            if( t.getId() == tid ){
                return Optional.of( t );
            }
        }
        return Optional.empty();
    }

    protected Transaction<ID,Quantity> getTransaction(int tid ) throws RemoteException {
        Optional<Transaction<ID,Quantity>> maybeTrx = this.findTransaction(tid);
        if( maybeTrx.isEmpty() ){
            throw new NoSuchTransactionException(tid);
        }
        return maybeTrx.get();
    }

    private void beforeCommitValidation( Transaction<ID,Quantity> tx ) throws RemoteException {
        if( !this.beforeCommit(tx) ){
            throw new InvalidTransactionException( tx.getId() );
        }
    }

    protected void commitTransaction( int tid ) throws RemoteException {
        Transaction<ID,Quantity> t = this.getTransaction(tid);
        if( !t.isReadOnly()){
            this.beforeCommitValidation( t );
            t.markForValidation( );
            this.validateTransactions( t );
            this.updateResources( t );
        }
        this.alertCommit( t );
        this.afterCommit( t );
        this.cleanTransactions();
    }

    private void updateResources( Transaction<ID,Quantity> tx ) throws RemoteException{
        for( Operation<ID,Quantity> op : tx.getWriteOps() ){
            this.applyOperation(tx,op);
        }
        tx.markForRemoval();
    }

    protected void addWriteOperation( int tid , ID rid , Quantity quantity ) throws RemoteException{
        Transaction<ID,Quantity> tx = this.getTransaction( tid );
        Operation<ID,Quantity> op = new Operation<>( rid , quantity );
        tx.addWriteOperation(op);
    }

    protected void addReadOperation( int tid , ID rid ) throws RemoteException{
        Transaction<ID,Quantity> tx = this.getTransaction( tid );
        Operation<ID,Quantity> op = new Operation<>( rid );
        tx.addReadOperation(op);
    }

    protected abstract void alertAbort(Transaction<ID,Quantity> tx , boolean manual ) throws RemoteException;
    protected abstract void alertCommit( Transaction<ID,Quantity> tx ) throws RemoteException;
    protected abstract void applyOperation( Transaction<ID,Quantity> tx ,Operation<ID,Quantity> op ) throws RemoteException;
    protected boolean beforeCommit( Transaction<ID,Quantity> tx ) {
        return true;
    }
    protected void afterCommit( Transaction<ID,Quantity> tx ) throws RemoteException {}

}
