package shared.logic;

import java.util.ArrayList;
import java.util.List;

public class Transaction<ID,Quantity extends Number> {

    public enum State {
        WORKING, VALIDATION, UPDATE, FINISHED;
    }
    private int id;

    private String host;
    private State state;
    private List<Operation<ID,Quantity>> readOps;
    private List<Operation<ID,Quantity>> writeOps;
    public Transaction(int id, String host) {
        this.id = id;
        this.host = host;
        this.state = State.WORKING;
        this.readOps = new ArrayList<>();
        this.writeOps = new ArrayList<>();
    }

    public String getHost() {
        return this.host;
    }

    public int getId(){
        return this.id;
    }

    public void addReadOperation(Operation<ID,Quantity> op ){
        this.readOps.add(op);
    }

    public void addWriteOperation( Operation<ID,Quantity> op ){
        this.writeOps.add(op);
    }

    public void markForValidation(){
        this.state = State.VALIDATION;
    }

    public void markForRemoval(){
        this.state = State.FINISHED;
    }

    public boolean isAwaitingUpdate(){ return this.state == State.UPDATE; }
    public boolean isReadyForRemoval(){ return this.state == State.FINISHED; }

    public boolean isReadOnly(){
        return this.writeOps.size() == 0;
    }

    public List<Operation<ID,Quantity>> getWriteOps(){
        return this.writeOps;
    }

    public boolean equals( Transaction t ){
        return t.id == this.id;
    }

    public boolean forwardValidate( Transaction<ID,Quantity> ti ){
        boolean valid = true;
        for( Operation<ID,Quantity> writeOp : this.writeOps ){
            for( Operation<ID,Quantity> readOp : ti.readOps ){
                if( writeOp.shareResource(readOp) ){
                    valid = false;
                }
            }
        }
        return valid;
    }
}
