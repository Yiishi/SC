/**
 * @author Diogo Matos fc52808
 * @author David Guilherme fc56333
 * @author Vitor Medeiros fc56351
 */

import java.util.ArrayList;
public class Wines {

    private String username;
    private String winename;
    private double price;
    private ArrayList<Integer> classification = new ArrayList<Integer>();
    private int quantity;
    private String image;

    public Wines(String winename ,String username , double price , int quantity , String image){
    	this.winename = winename;
        this.username = username;
        this.price = price;
        this.quantity =  quantity;
        this.image = image;
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

    public int getClassify(){
        int count = 0;
        int sum = 0;

        if(classification.size() != 0){
            for(int i : classification){
                sum += i;
                count++;
            }
            return sum/count ;
        }
        else
            return 0;
        
    }

    public String getimage(){
        return image;
    }

    public void sell(int i){
       quantity-=i ;
    }

    public void add(int i){
        quantity += i;
    }

    public void classify(int i){
        classification.add(i);
    }
}