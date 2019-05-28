package client;

import store.Product;

import java.util.HashMap;

public class State {
    private String user;
    private String token;
    private HashMap<Integer, Product> cart;

    public State() {
        this.token = "";
        this.user = "";
        this.cart = new HashMap<>();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void addProductToCart( int id , int quantity , String name , int cost ){
        if( cart.containsKey( id ) ){
            int newQuantity = cart.get(id).getQuantity() + quantity;
            cart.put( id , new Product( id , newQuantity , name , cost ));
        }else{
            cart.put( id , new Product( id , quantity , name , cost ));
        }
    }

    public HashMap<Integer, Product> getCart(){
        return this.cart;
    }

    public void wipeCart(){
        this.cart = new HashMap<>();
    }
}
