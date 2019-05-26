package authentication;

public class UserData {
    String password;
    int balance;
    boolean isAdmin;

    public UserData(String password, int balance, boolean isAdmin) {
        this.password = password;
        this.balance = balance;
        this.isAdmin = isAdmin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

}