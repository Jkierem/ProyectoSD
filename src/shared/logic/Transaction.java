package shared.logic;

import java.util.ArrayList;
import java.util.List;

public class Transaction {

    public enum State {
        WORKING, VALIDATION, UPDATE, FINISHED;
    }
    private int id;

    private String host;
    private State state;
    private List<Operation> readOps;
    private List<Operation> writeOps;
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

    public void addReadOperation(Operation op ){
        this.readOps.add(op);
    }

    public void addWriteOperation( Operation op ){
        this.writeOps.add(op);
    }

    public void markForValidation( int order ){
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

    public List<Operation> getWriteOps(){
        return this.writeOps;
    }

    public boolean equals( Transaction t ){
        return t.id == this.id;
    }

    public boolean forwardValidate( Transaction ti ){
        boolean valid = true;
        for( Operation writeOp : this.writeOps ){
            for( Operation readOp : ti.readOps ){
                if( writeOp.shareResource(readOp) ){
                    valid = false;
                }
            }
        }
        return valid;
    }
}
