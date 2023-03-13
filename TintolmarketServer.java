    /***************************************************************************
*   Seguranca e Confiabilidade 2020/21
*
*
***************************************************************************/

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TintolmarketServer{


	public static void main(String[] args) {
		System.out.println("servidor: main");
		TintolmarketServer server = new TintolmarketServer();
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
                
                if (user.length() != 0){
					outStream.writeObject(new Boolean(true));
				}
				else {
					outStream.writeObject(new Boolean(false));
				}

				//TODO: refazer
				//este codigo apenas exemplifica a comunicacao entre o cliente e o servidor
				//nao faz qualquer tipo de autenticacao

                BufferedReader br = new BufferedReader(new FileReader(new File("userLog.txt")));
                String creds = user + ":" + passwd;
                boolean userExists = false;

                for (String x = br.readLine(); x != null; x = br.readLine()){
                    if(x.equals(creds)){
                        userExists = true;
                        break;
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
}