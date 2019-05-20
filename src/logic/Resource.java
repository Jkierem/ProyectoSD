package logic;

public class Resource {
    private int id;
    private int quantity;
    private String name;
    private Lock lock;

    public Resource(int id, int quantity, String name) {
        this.id = id;
        this.quantity = quantity;
        this.name = name;
        this.lock = new Lock();
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Lock getLock() {
        return lock;
    }

    public Resource copy(){
        return new Resource( this.id , this.quantity , this.name);
    }
}
