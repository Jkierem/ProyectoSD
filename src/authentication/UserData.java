package authentication;

import shared.utils.Pair;

public class UserData {
    Pair<String,Integer> data;

    public UserData(String password, Integer balance) {
        this.data = new Pair<>(password,balance);
    }

    public String getPassword(){
        return this.data.getFirst();
    }

    public int getBalace(){
        return this.data.getSecond();
    }

    public void setPassword( String pass ){
        this.data.setFirst(pass);
    }

    public void setBalance( int balance ){
        this.data.setSecond(balance);
    }
}
