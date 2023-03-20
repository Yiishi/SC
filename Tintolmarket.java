import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Tintolmarket {
	BufferedReader br;
	File wines = new File("wines.txt");
	File users = new File ("userLog.txt");
	File winesforsale = new File("winesforsale.txt");
    static User user;
    private static String hostName;
    private static int portNumber;
    private static Socket clientSocket;
    private static ObjectOutputStream outToServer;
    private static ObjectInputStream inFromServer;
    
    public static void main(String[] args) throws Exception{

        if(args.length == 4){

            String[] st = args[1].split(":");
            
            if(st.length == 2){
                portNumber = Integer.parseInt(st[1]);  
            }else{
                portNumber = 12345;
            }
            clientSocket = new Socket(st[0]/*hostname*/, portNumber);
            outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            inFromServer = new ObjectInputStream(clientSocket.getInputStream());
        }

        Scanner ler = new Scanner(System.in);
        user = (User) inFromServer.readObject();
        
        while(true){
            System.out.println("Menu");
            System.out.println("Adicionar um vinho ao catalogo : add wineName image");
            System.out.println("Colocar um vinho do catalogo a venda: sell wineName value quantity");
            System.out.println("Ver um vinho: view wineName");
            System.out.println("Comprar vinho: buy wineName seller quantity");
            System.out.println("Verificar carteira: wallet");
            System.out.println("Classificar vinho: classify wineName stars");
            System.out.println("Enviar mensagem: talk user message");
            System.out.println("Ler mensagens: read");

            String acao = ler.nextLine();
            avaliaAcao(acao);
        }

    }

    public void avaliaAcao(String acao){
        String[] split = acao.split(" ");
		
		if(split[0].equals("add")){
            if(split.length == 3){
                add(split[1],split[2]);
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }
		}else if(split[0].equals("sell")){
            if(split.length == 4){
                sell(split[1],split[2],split[3]);
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }

		}else if(split[0].equals("view")){
			if(split.length == 2){
                view(split[1]);
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }

		}else if(split[0].equals("buy")){
            if(split.length == 4){
                buy(split[1],split[2],split[3]);
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }
            
        }else if(split[0].equals("wallet")){
            if(split.length == 1){
                wallet();
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }
			
		}else if(split[0].equals("classify")){
			if(split.length == 3){
                classify(split[1],split[2]);
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }

		}else if(split[0].equals("talk")){
            
			
		}else if(split[0].equals("read")){
            if(split.length == 1){
                read();
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }
			
		}

    }

    public int wallet(){
        return user.getWallet();
    }

    public void classify(String wine, int stars){
        
    }

    
    public void talk (String user, String message){

    }
    
    

    public String read(){
    	return "";
    }

    
    
    public void add(Wines wine, File image)throws Exception{
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

