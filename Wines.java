import java.io.*;
public class Wines {

    private String username;
    String winename;
    private double price;
    private int classification;
    private int quantity;
    private File image;

    public Wines(String username,String winename, int price, int quantity){
    	this.winename=winename;
        this.username = username;
        this.price = price;
        this.quantity =  quantity;
        this.image = new File("Wine.png");
    }

    public String getUsername(){
        return username;
    }
    
    public String getWinename() {
    	return winename;
    }

    public double getPrice(){
        return price;
    }

    public int getQuantity(){
        return quantity;
    }

    public File getimage(){
        return image;
    }

    public void sell(int i){
       quantity-=i ;
    }

    public void add(int i){
        quantity += i;
    }

    public void classify(int i){
        
    }

}