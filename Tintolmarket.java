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
                System.out.println((String) inStream.readObject());
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }

		}else if(split[0].equals("sell")){
            if(split.length == 4){
                sell(split[1],split[2],split[3]);
                System.out.println((String) inStream.readObject());
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }

		}else if(split[0].equals("view")){
			if(split.length == 2){
                view(split[1]);
                System.out.println((String) inStream.readObject());
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }

		}else if(split[0].equals("buy")){
            if(split.length == 4){
                buy(split[1],split[2],split[3]);
                System.out.println((String) inStream.readObject());
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }
            
        }else if(split[0].equals("wallet")){
            if(split.length == 1){
                System.out.println(wallet(););
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }
			
		}else if(split[0].equals("classify")){
			if(split.length == 3){
                classify(split[1],split[2]);
                System.out.println((String) inStream.readObject());
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }

		}else if(split[0].equals("talk")){
            StringBuilder msg = new StringBuilder();
            for(int i = 2; i < split.length(); i++){
                msg.append(split[i]);
                msg.append(" ");
            }
            talk(split[1],msg.toString());
            System.out.println((String) inStream.readObject());
            
			
		}else if(split[0].equals("read")){
            if(split.length == 1){
                read();
                System.out.println((String) inStream.readObject());
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }
		}
    }

    public int wallet(){
        return user.getWallet();
    }

    public void classify( String wine, int stars)throws Exception{
        outToServer.writeBytes("classify " + wine + " " + stars);
    }

    public void talk (String user, String message)throws Exception{
        outToServer.writeBytes("talk/" + user + "/" + message);
    }
    
    public void read()throws Exception{
    	outToServer.writeBytes("read");
    }

    public void add(String wine, File image, int quantity, double value)throws Exception{
        outToServer.writeBytes("add " + wine + " "  + quantity+ " "+ value);
    }
    
    public void sell(String wine, double value, int quantity )throws Exception{
        outToServer.writeBytes("sell " + wine + " " + value + " " + quantity);
    }
    
    public void buy(String wine, String seller, int quantity )throws Exception {
        outToServer.writeBytes("buy " + wine + " " + seller + " " + quantity);
    }
    
    public void view (String wine)throws Exception {
    	outToServer.writeBytes("view "+ wine);
    }

}

