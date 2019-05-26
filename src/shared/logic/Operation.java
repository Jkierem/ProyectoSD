package shared.logic;

import java.util.Optional;

public class Operation<ID,Quantity extends Number> {

    private ID rid;
    private Optional<Quantity> maybeChange;

    public Operation(ID rid) {
        this.rid = rid;
        this.maybeChange = Optional.empty();
    }

    public Operation(ID rid, Quantity change) {
        this.rid = rid;
        this.maybeChange = Optional.of(change);
    }

    public boolean shareResource(Operation op ){
        return this.rid.equals( op.rid );
    }

    public ID getRid() {
        return rid;
    }

    public Number getQuantity(){
        return this.maybeChange.isEmpty() ? 0 : this.maybeChange.get();
    }
}
