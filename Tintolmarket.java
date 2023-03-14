import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Tintolmarket {
	BufferedReader br;
	File wines = new File("wines.txt");
	File users = new File ("userLog.txt");
	File winesforsale = new File("winesforsale.txt");
    private String hostName;
    private int portNumber;
    private Socket clientSocket;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;

    public Tintolmarket(int port, String userId, String passWord) throws UnknownHostException, IOException {
        this.portNumber = port;
        this.clientSocket = new Socket(hostName, portNumber);
        this.outToServer = new DataOutputStream(clientSocket.getOutputStream());
        this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public int wallet(User user){
        return user.getWallet();
    }

    public void classify( Wines wine, int stars){
        
    }

    
    public void talk (String user, String message){

    }
    
    

    public String read(){
    	return "";
    }

    
    
    public void add(Wines wine, File image, int quantity)throws Exception{
     String name= wine.getWinename();
     br = new BufferedReader(new FileReader(wines));
     String st;
     while((st=br.readLine())!=null) {
    	if(name==st) {
    		throw new Exception("vinho já existe");
    	}
     }
     FileWriter fw= new FileWriter (wines, true);
     BufferedWriter bw= new BufferedWriter(fw);
     bw.write(name+" :"+image.getPath());
     wine.add(quantity);
     bw.newLine();
     bw.close();
    }
    
    
    
    
    public String sell(Wines wine, double value, int quantity )throws Exception{
        String name= wine.getWinename();
        br = new BufferedReader(new FileReader(wines));
        String st;
        while((st=br.readLine())!=null) {
        	if(name==st) {
        		FileWriter fw= new FileWriter (winesforsale, true);
        	     BufferedWriter bw= new BufferedWriter(fw);
        	     bw.write("Vinho: "+name+" ; vendedor : "+wine.getUsername()+ " ; quantidade : " + quantity + "; valor : "+value);
        	     bw.newLine();
        	     bw.close();
        	     return ("Vinho posto à venda, poderá ver a lista em "+winesforsale.getPath());
       		}
       }
       throw new Exception("vinho não existe");
    }
    
    
    public void buy (User buyer, Wines wine, User seller, int quantity )throws Exception {
    	if(!(wine.getUsername()==seller.getUsername())) {
    		throw new Exception ("vendedor não vende este vinho");
    	}
    	if (quantity>wine.getQuantity()) {
    		throw new Exception ("Quantidade demasiado elevada para o Stock existente");
    	}
    	if(wine.getPrice()>buyer.getWallet()) {
    		throw new Exception ("Não tem dinheiro suficiente");
    	}
    	wine.sell(quantity);
    	seller.Soldsomething(wine.getPrice());
    	buyer.Boughtsomething(wine.getPrice());
    	//txt wines for sale update quantity
    }
    
    public String view (Wines wine) {
    	
    	
    	return "";
    }

}

