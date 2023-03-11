public class User {

    private String username = null;
    private int wallet = null;

    public User(String name){
        username = name;
        wallet = 200;
    }

    public String getUsername(){
        return username;
    }

    public String getWallet(){
        return wallet;
    }
}