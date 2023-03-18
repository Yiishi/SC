    /***************************************************************************
*   Seguranca e Confiabilidade 2020/21
*
*
***************************************************************************/

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

public class TintolmarketServer{
	BufferedReader br;
	BufferedReader br2;
	File wines = new File("wines.txt");
	File users = new File ("userLog.txt");
	File winesforsale = new File("winesforsale.txt");
	User currentUser;
	private int port;
	private ArrayList<User> userList;
	private ArrayList<Wines> winesList;

	public TintolmarketServer(int port) throws Exception{
		this.port = port;
		userList = new ArrayList<User>();
		winesList = new ArrayList<Wines>();

		br = new BufferedReader(new FileReader("users.txt"));
		String st;
		String split[] = new String[3];;
        while((st = br.readLine())!=null) {
			split = st.split(" ");
			userList.add(new User(split[0],Integer.parseInt(split[1])));
		}

		br = new BufferedReader(new FileReader("wines.txt"));
		String st2;
		String split2[] = new String[5];;
        while((st2 = br.readLine())!=null) {
			split2 = st2.split(" ");
			winesList.add(new Wines(split2[0], split2[1], Integer.parseInt(split[2]), Integer.parseInt(split[3])));
		}

	}

	public static void main(String[] args) throws Exception{
		System.out.println("servidor: main");
		TintolmarketServer server = new TintolmarketServer(Integer.parseInt(args[1]));//Lidar exeção para caso o porto nao ser um Int!!!!
		server.startServer(args);
	}

	public void startServer (String[] args){
		ServerSocket sSoc = null;
        
		try {
            if (args[1] == null){
                sSoc = new ServerSocket(12345);
            }else{
                sSoc = new ServerSocket( Integer.parseInt(args[1]));
            }
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
         
		while(true) {
			try {
				Socket inSoc = sSoc.accept();
				ServerThread newServerThread = new ServerThread(inSoc);
				newServerThread.start();
		    }
		    catch (IOException e) {
		        e.printStackTrace();
		    }
		    
		}
		//sSoc.close();
	}


	//Threads utilizadas para comunicacao com os clientes
	class ServerThread extends Thread {

		private Socket socket = null;

		ServerThread(Socket inSoc) {
			socket = inSoc;
			System.out.println("thread do server para cada cliente");
		}
 
		public void run(){
			try {
				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

				String user = null;
				String passwd = null;
			
				try {
					
					user = (String)inStream.readObject();
					passwd = (String)inStream.readObject();
					System.out.println("thread: depois de receber a password e o user");
				}catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				
				
				/*
				*TODO: analizar userIs e password
				*enviar user outStream.writeObject(user);
				*analizar inputs do client
				**/
                BufferedReader br = new BufferedReader(new FileReader(new File("userLog.txt")));
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("userLog.txt")));
                String creds = user + ":" + passwd;
                boolean userExists = false;

                for (String x = br.readLine(); x != null; x = br.readLine()){
                    if(x.equals(creds)){
                        userExists = true;
                        break;
                    }
                }

				if(userExists == false){
					bw.write(user+","+passwd, MIN_PRIORITY, MAX_PRIORITY);
				} else{
					for(User u : userList) {
						if(u.getWinename().equals(user)){
							currentUser = u;
							break;
						}
					}
				}

				outStream.close();
				inStream.close();
 			
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public request avaluateRequest(String str){

		//Avaliar o conteudo enviado do cliente para o servidor

		return null;
	}

	/**
	 * @param request
	 */
	public void processRquest(request request){
		switch(request){
			case ADD:
				addWine();
				break;

			case BUY:
				buyWine();
				break;
				
			case CLASSIFY:
				classifyWine(wine, stars);
				break;

			case READ:
				readMessege();
				break;

			case SELL:
				sellWine(wine, quantity);
				break;

			case TALK:
				talk();
				break;

			case VIEW:
				viewWine();
				break;
				
			default:
				break;

		}
	}

	private void addWine() {
	}
	
	private void buyWine() {
	}

	private void classifyWine(String wine, int stars) {
		for(Wines w : winesList) {
			if(w.getWinename().equals(wine)){
				w.classify(stars);
				break;
			}
		}
	}

	private void readMessege() {
	}

	private String sellWine(Wines wine, double value, int quantity )throws Exception{
        String name= wine.getWinename();
        br = new BufferedReader(new FileReader(wines));
        String st;
        while((st = br.readLine())!=null) {
        	if(name.equals(st)) {
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

	private void talk(String user, String message, ) {
		if((new File("chat.txt")).exists()){
			FileWriter fw= new FileWriter (chat, true);
        	BufferedWriter bw= new BufferedWriter(fw);
			bw.write(user + "/" + message + "/" + currentUser.getUsername());
		}

	}

	private void viewWine() {
	}

}