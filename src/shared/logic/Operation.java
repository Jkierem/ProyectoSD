package shared.logic;

import java.util.Optional;

public class Operation {

    private int rid;
    private Optional<Integer> maybeChange;

    public Operation(int rid) {
        this.rid = rid;
        this.maybeChange = Optional.empty();
    }

    public Operation(int rid, int change) {
        this.rid = rid;
        this.maybeChange = Optional.of(change);
    }

    public boolean shareResource(Operation op ){
        return this.rid == op.rid;
    }

    public int getRid() {
        return rid;
    }

    public int getQuantity(){
        return this.maybeChange.isEmpty() ? 0 : this.maybeChange.get();
    }
}
