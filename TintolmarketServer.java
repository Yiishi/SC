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
				User currentUser = null;
                BufferedReader br = new BufferedReader(new FileReader(new File("userLog.txt")));
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("userLog.txt")));
                boolean correctPass = false;
                boolean userExists = false;
				boolean closed = false;

                for (String x = br.readLine(); x != null; x = br.readLine()){
					String[] s = x.split(":");
                    if(s[0].equals(user) && s[1].equals(passwd)){
                        userExists = true;
						correctPass = true;
                        break;
                    }
                }

				if(userExists == false){

					bw.write(user+","+passwd, MIN_PRIORITY, MAX_PRIORITY);
					currentUser = new User(user, 200);
					outStream.writeBytes("user criado");

				} else if(userExists == true && correctPass == true){

					for(User u : userList) {
						if(u.getUsername().equals(user)){
							currentUser = u;
							break;
						}
					}
					outStream.writeBytes("autenticado");

				}else{
					outStream.writeBytes("password incorreta");;
					closed = true;
				}

				if(!closed){
					outStream.writeObject(currentUser);
					avaluateRequest((String) inStream.readObject(), currentUser);
				}
				outStream.close();
				inStream.close();
 			
				socket.close();

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void avaluateRequest(String str, User currentUser){

		String[] split = str.split(" ", 2);
		String[] split2 = str.split(" ");

		if(split[0].equals("add")){

			addWine(currentUser);

		}else if(split[0].equals("sell")){

			sellWine(wine, quantity, currentUser);

		}else if(split[0].equals("view")){

			viewWine(currentUser);

		}else if(split[0].equals("buy")){

			buyWine(currentUser);

		}else if(split[0].equals("classify")){
			
			classifyWine(wine, stars, currentUser);

		}else if(split[0].equals("talk")){

			talk(currentUser);

		}else if(split[0].equals("read")){
			
			readMessege(currentUser);
		}
		
	}

	/**
	 * @param request
	 *
	public void processRquest(request request){
		User currentUser;
		switch(request){
			case ADD:
				addWine(currentUser);
				break;

			case BUY:
				buyWine(currentUser);
				break;
				
			case CLASSIFY:
				classifyWine(wine, stars, currentUser);
				break;

			case READ:
				readMessege(currentUser);
				break;

			case SELL:
				sellWine(wine, quantity, currentUser);
				break;

			case TALK:
				talk(currentUser);
				break;

			case VIEW:
				viewWine(currentUser);
				break;
				
			default:
				break;

		}
	}**/

	private void addWine(User currentUser) {
	}
	
	private void buyWine(User currentUser) {
	}

	private void classifyWine(String wine, int stars) {
		for(Wines w : winesList) {
			if(w.getWinename().equals(wine)){
				w.classify(stars);
				break;
			}
		}
	}

	private void readMessege(User currentUser) {
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

	private void talk(String user, String message, User currentUser) {
		if((new File("chat.txt")).exists()){
			FileWriter fw= new FileWriter (chat, true);
        	BufferedWriter bw= new BufferedWriter(fw);
			bw.write(user + "/" + message + "/" + currentUser.getUsername());
		}

	}

	private void viewWine(User currentUser) {
	}

}