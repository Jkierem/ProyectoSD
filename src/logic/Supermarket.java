package logic;

import java.util.ArrayList;

public class Supermarket implements Store {
    private ArrayList<Resource> resources;

    public int getResourceQuantity(int tid, int id){
        for ( Resource r : this.resources ){
            if( !r.getLock().hasAccess(tid) && r.getId() == id ){
                return r.getQuantity();
            }
        }
        return 0;
    }

    public synchronized int setReourceQuantity(int tid, int id , int quantity){
        for ( Resource r : this.resources ){
            if( !r.getLock().hasAccess(tid) && r.getId() == id ){
                r.getLock().lock(tid);
                r.setQuantity(quantity);
            }
        }
    }

    public int startTransaction(ArrayList<Integer> resourceIds){}
    public void abort(int tid){}
    public void commit(int tid){}
}
