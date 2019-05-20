package logic;

public class Lock {

    private int transactionId;
    private boolean locked;

    public Lock() {
        this.transactionId = -1;
        this.locked = false;
    }

    public void lock( int tid ){
        if( this.locked ) {
            try {
                wait();
            }catch(Exception e){

            }
        }
        this.transactionId = tid;
        this.locked = true;
    }

    public void unlock( int tid ){
        if( this.transactionId == tid ){
            this.transactionId = -1;
            this.locked = LockState.FREE;
        }
    }

    public boolean hasAccess( int tid ){
        return !this.isLocked() || this.transactionId == tid;
    }

    public void forceUnlock(){
        this.transactionId = -1;
        this.locked = LockState.FREE;
    }

    public boolean isLocked(){
        return this.locked == LockState.LOCKED;
    }

    public int getOwner(){
        return this.transactionId;
    }
}
