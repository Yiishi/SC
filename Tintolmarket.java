import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.zip.Inflater;

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

        if(args.length == 3){

            String[] st = args[0].split(":");
            
            if(st.length == 2){
                portNumber = Integer.parseInt(st[1]);  
            }else{
                portNumber = 12345;
            }
            
            clientSocket = new Socket(st[0]/*hostname*/, portNumber);
            outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            inFromServer = new ObjectInputStream(clientSocket.getInputStream());
        
            outToServer.writeObject(args[1]);
            outToServer.writeObject(args[2]);
            Scanner ler = new Scanner(System.in);

            System.out.println((String) inFromServer.readObject()+"\n");

            String userId = (String) inFromServer.readObject();
            int wallet = (int) inFromServer.readObject();
            user = new User(userId, wallet);
            
            while(1 == 1){
                System.out.println("Menu");
                System.out.println("Adicionar um vinho ao catalogo : add wineName image");
                System.out.println("Colocar um vinho do catalogo a venda: sell wineName value quantity");
                System.out.println("Ver um vinho: view wineName");
                System.out.println("Comprar vinho: buy wineName seller quantity");
                System.out.println("Verificar carteira: wallet");
                System.out.println("Classificar vinho: classify wineName stars");
                System.out.println("Enviar mensagem: talk user message");
                System.out.println("Ler mensagens: read \n");

                String acao = ler.nextLine();
                avaliaAcao(acao);
            }
        }
    }

    public static void avaliaAcao(String acao ) throws Exception{
        String[] split = acao.split(" ");
		
		if(split[0].equals("add")){
            if(split.length == 3){
                add(split[1],split[2]);
                System.out.println((String) inFromServer.readObject());
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }

		}else if(split[0].equals("sell")){
            if(split.length == 4){
                sell(split[1],Double.parseDouble(split[2]),Integer.parseInt(split[3]));
                System.out.println((String) inFromServer.readObject());
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }

		}else if(split[0].equals("view")){
			if(split.length == 2){
                view(split[1]);
                System.out.println((String) inFromServer.readObject());
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }

		}else if(split[0].equals("buy")){
            if(split.length == 4){
                buy(split[1],split[2],Integer.parseInt(split[3]));
                System.out.println((String) inFromServer.readObject());
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }
            
        }else if(split[0].equals("wallet")){
            if(split.length == 1){
                System.out.println(wallet());
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }
			
		}else if(split[0].equals("classify")){
			if(split.length == 3){
                classify(split[1],Integer.parseInt(split[2]));
                System.out.println((String) inFromServer.readObject());
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }

		}else if(split[0].equals("talk")){
            StringBuilder msg = new StringBuilder();
            for(int i = 2; i < split.length; i++){
                msg.append(split[i]);
                msg.append(" ");
            }
            talk(split[1],msg.toString());
            System.out.println((String) inFromServer.readObject());
            
			
		}else if(split[0].equals("read")){
            if(split.length == 1){
                read();
                System.out.println((String) inFromServer.readObject());
            }else{
                System.out.println("Por favor preencha todos os requisirtos corretamente");
            }
		}
    }

    public static int wallet(){
        return user.getWallet();
    }

    public static void classify( String wine, int stars)throws Exception{
        outToServer.writeObject("classify " + wine + " " + stars);
    }

    public static void talk (String user, String message)throws Exception{
        outToServer.writeObject("talk/" + user + "/" + message);
    }
    
    public static void read()throws Exception{
    	outToServer.writeObject("read");
    }

    public static void add(String wine, String image)throws Exception{
        outToServer.writeObject("add " + wine + " " + image);
    }
    
    public static void sell(String wine, double value, int quantity )throws Exception{
        outToServer.writeObject("sell " + wine + " " + value + " " + quantity);
    }
    
    public static void buy(String wine, String seller, int quantity )throws Exception {
        outToServer.writeObject("buy " + wine + " " + seller + " " + quantity);
    }
    
    public static void view (String wine)throws Exception {
    	outToServer.writeObject("view "+ wine);
    }

}

