public class Wines {

    private String username = null;
    private int price;
    private int classification;
    private int quantity;
    private Image image;

    public Wines(String username, int price, int quantity, Image image){
        this.username = username;
        this.price = price;
        this.quantity =  quantity;
        this.image = image;
    }

    public String getUsername(){
        return username;
    }

    public int getPrice(){
        return price;
    }

    public int getQuantity(){
        return quantity;
    }

    public Image getimage(){
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