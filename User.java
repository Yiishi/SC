public class User {

    private String username;
    private int wallet = 0;

    public User(String name){
        username = name;
        wallet = 200;
    }

    public String getUsername(){
        return username;
    }

    public int getWallet(){
        return wallet;
    }
    
    public void Soldsomething(double price) {
    	wallet+=price;
    }
    
    public void Boughtsomething(double price) {
    	wallet-=price;
    }
}