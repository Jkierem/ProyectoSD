package logic;

import java.util.ArrayList;

public class Transaction {
    private int id;
    private ArrayList<Integer> lockedResourceIds;

    public Transaction(int id , ArrayList<Integer> lockedResourceIds) {
        this.lockedResourceIds = lockedResourceIds;
        this.id = id;
    }
}
