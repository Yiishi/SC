import java.security.PublicKey;

/**
 * @author Diogo Matos fc52808
 * @author David Guilherme fc56333
 * @author Vitor Medeiros fc56351
 */

public class User {

    private String username;
    private int wallet = 0;
    private PublicKey pk;

    public User(String name, int wallet, PublicKey pk){
        username = name;
        this.wallet = wallet;
        this.pk = pk;
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

    public PublicKey getPk() {
        return pk;
    }
}