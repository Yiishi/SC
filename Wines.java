public class Wines {

    private String username = null;
    private int price = null;
    private int classification = null;
    private int quantity = null;
    private image;

    public User(String username, int price, int quantity, image){
        this.username = username;
        this.price = price;
        this.quantity =  quantity;
        this.image = image;
    }

    public String getUsername(){
        return username;
    }

    public String getPrice(){
        return price;
    }

    public String getQuantity(){
        return quantity;
    }

    public String getimage(){
        return image;
    }

    public void sell(int i){
        return quantity - i;
    }

    public void add(int i){
        return quantity + i;
    }

    public void classify(int i){
        
    }
}