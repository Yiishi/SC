public class Wines {

    private String username = null;
    private double price = null;
    private LinkedList classification = new LinkedList();
    private int quantity = null;
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
        quantity + i;
    }

    public void addClassification(int i){
        classification.addLast(i);
    }

    public void classify(int i){
        double count = classification.size();
        double classif = 0;
        for(int i = 0 i < count, i++){
            classif += classification.get(i);
        }
        return classif/count;
    }
}