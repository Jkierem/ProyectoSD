package store;

public class ProductBuilder {
    private int id;
    private int quantity;
    private String name;
    private int value;

    public ProductBuilder() { }

    public ProductBuilder(int id, int quantity, String name, int value) {
        this.id = id;
        this.quantity = quantity;
        this.name = name;
        this.value = value;
    }

    public ProductBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public ProductBuilder setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public ProductBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder setValue(int value) {
        this.value = value;
        return this;
    }

    public Product build(){
        return new Product(id,quantity,name,value);
    }

    public ProductBuilder copy(){
        return new ProductBuilder(id,quantity,name,value);
    }
}
