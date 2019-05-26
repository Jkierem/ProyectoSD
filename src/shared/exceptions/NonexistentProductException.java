package shared.exceptions;

import java.util.List;
import java.util.stream.Collectors;

public class NonexistentProductException extends InvalidOperationException {
    public NonexistentProductException(int rid) {
        super("Product with id "+String.valueOf(rid)+" does not exist");
    }

    public NonexistentProductException() {
        super("Product does not exist");
    }

    public NonexistentProductException(List<Integer> rids ){
        super("Products with the following IDs do not exist: [" + String.join( "," ,
                rids.stream().map(String::valueOf).collect(Collectors.toList())
        ) + "]");
    }
}
