
/***************************************************************************
*   Seguranca e Confiabilidade 2020/21
*
*
***************************************************************************/

import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

public class TintolmarketServer {
	BufferedReader br;
	BufferedReader br2;
	File wines = new File("wines.txt");
	File userlog = new File("userLog.txt");
	File users = new File("users.txt");
	File winesforsale = new File("winesforsale.txt");
	File chat = new File("chat.txt");
	private int port;
	private ArrayList<User> userList;
	private ArrayList<Wines> winesList;
	private ArrayList<Wines> winesForSaleList;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;

	public TintolmarketServer(int port) throws Exception {
		this.port = port;
		userList = new ArrayList<User>();
		winesList = new ArrayList<Wines>();
		winesForSaleList = new ArrayList<Wines>();

		userList = (ArrayList<User>) readObjectFromFile(users);
		winesList = (ArrayList<Wines>) readObjectFromFile(wines);
		winesForSaleList = (ArrayList<Wines>) readObjectFromFile(winesforsale);


		/** 
		br = new BufferedReader(new FileReader("users.txt"));
		String st;
		String split[] = new String[3];
		while ((st = br.readLine()) != null) {
			split = st.split(" ");
			userList.add(new User(split[0], Integer.parseInt(split[1])));
		}

		br = new BufferedReader(new FileReader("wines.txt"));
		String st2;
		String split2[] = new String[5];
		while ((st2 = br.readLine()) != null) {
			split2 = st2.split(" ");
			winesList
					.add(new Wines(split2[0], split2[1], Integer.parseInt(split[2]), Integer.parseInt(split[3]), null));
		}

		br = new BufferedReader(new FileReader("winesForSale.txt"));
		String st3;
		String split3[] = new String[2];
		while ((st3 = br.readLine()) != null) {
			split3 = st3.split(" ");
			winesForSaleList.add(new Wines(split2[0], "", 0, 0, split2[1]));
		}
		*/
	}

	public static void main(String[] args) throws Exception {
		System.out.println("servidor: main");
		TintolmarketServer server = new TintolmarketServer(Integer.parseInt(args[0]));// Lidar exeção para caso o porto
																						// nao ser um Int!!!!
		server.startServer(args);
	}

	public void startServer(String[] args) {
		ServerSocket sSoc = null;

		try {
			if (args[0] == null) {
				sSoc = new ServerSocket(12345);
			} else {
				sSoc = new ServerSocket(Integer.parseInt(args[0]));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		while (true) {
			try {
				Socket inSoc = sSoc.accept();
				ServerThread newServerThread = new ServerThread(inSoc);
				newServerThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		// sSoc.close();
	}

	public User getUser(String userID) throws Exception {
		for (User i : userList) {
			if (i.getUsername().equals(userID)) {
				return i;
			}
		}
		return null;
	}

	public Wines getWine(String wineID) throws Exception {
		for (Wines i : winesList) {
			if (i.getWinename().equals(wineID)) {
				return i;
			}
		}
		return null;
	}

	public Wines getWineForSale(String wineID) throws Exception {
		for (Wines i : winesForSaleList) {
			if (i.getWinename().equals(wineID)) {
				return i;
			}
		}
		return null;
	}

	// Threads utilizadas para comunicacao com os clientes
	class ServerThread extends Thread {

		private Socket socket = null;

		ServerThread(Socket inSoc) {
			socket = inSoc;
			System.out.println("thread do server para cada cliente");
		}

		public void run() {
			try {
				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
				dataInputStream = new DataInputStream(socket.getInputStream());
				File diretorio = new File("\\images");
				if(!diretorio.exists()) {
					diretorio.mkdirs();
				}else {
					System.out.println("Diretorio ja existente");
				}
				
				String user = null;
				String passwd = null;

				try {

					user = (String) inStream.readObject();
					passwd = (String) inStream.readObject();
					System.out.println("thread: depois de receber a password e o user");
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}

				User currentUser = null;
				BufferedReader br = new BufferedReader(new FileReader(new File("userLog.txt")));
				boolean correctPass = false;
				boolean userExists = false;
				boolean closed = false;

				for (String x = br.readLine(); x != null; x = br.readLine()) {
					String[] s = x.split(":");
					if (s[0].equals(user) && s[1].equals(passwd)) {
						userExists = true;
						correctPass = true;
						break;
					}
				}

				if (userExists == false) {

					currentUser = new User(user, 200);
					userList.add(currentUser);
					writeObjectToFile(users, userList);
					outStream.writeObject("user criado");

				} else if (userExists == true && correctPass == true) {

					for (User u : userList) {
						if (u.getUsername().equals(user)) {
							currentUser = u;
							break;
						}
					}
					outStream.writeObject("autenticado");
				
				

				} else {
					outStream.writeObject("password incorreta");
					closed = true;
				}

				FileWriter fw= new FileWriter (userlog, true);
				String s = user + " " + passwd;
				writeFile(fw, s);

				/** 
				FileWriter fw1= new FileWriter (users, true);
				String s1 = user + " 200";
				writeFile(fw1, s1);
				*/

				outStream.writeObject(currentUser.getUsername());
				outStream.writeObject(currentUser.getWallet());
				
				while (!closed) {
					avaluateRequest((String) inStream.readObject(), currentUser, outStream);
				}

				outStream.close();
				inStream.close();				
				socket.close();

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				outStream.close();
				inStream.close();				
				socket.close();
			}
		}
	}

	public void avaluateRequest(String str, User currentUser, ObjectOutputStream outStream) throws Exception {

		String[] split = str.split(" ", 2);
		String[] split2 = str.split(" ");

		if (split[0].equals("add")) {

			addWine(split2[1], split2[2], outStream);

		} else if (split[0].equals("sell")) {

			sellWine(split2[1], Double.parseDouble(split2[2]), Integer.parseInt(split2[3]), currentUser, outStream);

		} else if (split[0].equals("view")) {

			viewWine(currentUser, split2[1], outStream);

		} else if (split[0].equals("buy")) {

			buyWine(split2[1], split2[2], Integer.parseInt(split2[3]), currentUser, outStream);

		} else if (split[0].equals("classify")) {

			classifyWine(split2[1], Integer.parseInt(split2[2]), currentUser, outStream);

		} else if (split[0].contains("talk")) {
			System.out.println("avaliaRequest");
			String[] split3 = str.split("/");
			talk(split3[1], split3[2], currentUser, outStream);

		} else if (str.equals("read")) {

			readMessege(currentUser, outStream);
		}

	}

	private void addWine(String wine, String image, ObjectOutputStream outStream) throws Exception {
		if (getWine(wine) == null) {
			Wines newWine = new Wines(wine, "", 0, 0, image);
			byte[] buffer = new byte[Integer.MAX_VALUE];
		    int bytes = dataInputStream.read(buffer,0,buffer.length);
		    FileOutputStream f = new FileOutputStream("images\\"+image);
		    f.write(buffer,0,bytes);
			winesList.add(newWine);

			/**FileWriter fw= new FileWriter (wines, true);
			String s = wine + " " + image;
			writeFile(fw, s);*/

			writeObjectToFile(wines, winesList);

			// BufferedWriter bw= new BufferedWriter(fw);
			// bw.write( wine + image);
			// bw.newLine();
			// bw.close();
			outStream.writeObject("O vinho foi adicionado ao catalogo");
		} else {
			outStream.writeObject("O vinho que deseja adicionar ja se encontra no catalogo");
		}
	}

	private void buyWine(String wineID, String sellerID, int quantity, User currentUser, ObjectOutputStream outStream)
			throws Exception {
		Wines wine = getWineForSale(wineID);
		if ((wine == null)) {
			// throw new Exception ("Ocorreu um erro, vinho não está registado para venda");
			outStream.writeObject("Ocorreu um erro, vinho nao esta registado para venda");

		} else if (!(wine.getUsername().equals(sellerID))) {
			// throw new Exception ("vendedor não vende este vinho");
			outStream.writeObject("vendedor nao vende este vinho");

		} else if (quantity > wine.getQuantity()) {
			// throw new Exception ("Quantidade demasiado elevada para o Stock existente");
			outStream.writeObject("Quantidade demasiado elevada para o Stock existente");

		} else if ((wine.getPrice() * wine.getQuantity()) > currentUser.getWallet()) {
			// throw new Exception ("Não tem dinheiro suficiente");
			outStream.writeObject("Nao tem dinheiro suficiente");

		} else {
			wine.sell(quantity);
			writeObjectToFile(winesforsale, winesForSaleList);
			User seller = getUser(sellerID);
			seller.Soldsomething(wine.getPrice());
			currentUser.Boughtsomething(wine.getPrice());
			// UpdateWinesForSale(1 , String wineID, int quantity);
			outStream.writeObject("Compra bem sucedida");
		}
	}

	private void classifyWine(String wineID, int stars, User currentUser, ObjectOutputStream outStream)
			throws Exception {
		if (getWine(wineID) == null) {
			outStream.writeObject("O vinho que pretende avaliar nao se encontra no catalogo");
		} else {
			Wines wine = getWine(wineID);
			wine.classify(stars);
			writeObjectToFile(winesforsale, winesForSaleList);
			outStream.writeObject("Obrigado pela sua avaliacao");
		}
	}

	private void readMessege(User currentUser, ObjectOutputStream outStream) throws Exception {
		br = new BufferedReader(new FileReader("chat.txt"));
		boolean noMSG = true;
		String st9;
		String split9[] = new String[3];
		StringBuilder msg = new StringBuilder();
		while ((st9 = br.readLine()) != null) {
			split9 = st9.split("/");
			if (split9[0].equals(currentUser.getUsername())) {
				msg.append(split9[1]).append("  Message from : ").append(split9[2]);
				msg.append("/n");
				noMSG = false;
			}
		}
		outStream.writeObject(msg.toString());
	}

	private void sellWine(String name, double value, int quantity, User currentUser, ObjectOutputStream outStream)
			throws Exception {
		if (getWine(name) == null) {
			outStream.writeObject("O vinho que pretende vender nao se encontra no catalogo");
		} else {
			Wines newWine = new Wines(name, currentUser.getUsername(), value, quantity, null);
			System.out.println(value + " , "+ newWine.getPrice());
			winesForSaleList.add(newWine);
			writeObjectToFile(winesforsale, winesForSaleList);
			outStream.writeObject("Vinho colocado a venda com sucesso");
		}

	}

	private void talk(String user, String message, User currentUser, ObjectOutputStream outStream) throws Exception {
		System.out.println("aqui");

		if(getUser(user) == null){
			outStream.writeObject("Usuario nao existente");
		}else{
			if ((new File("chat.txt")).exists()) {
				FileWriter fw = new FileWriter(chat, true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(user + "/" + message + "/" + currentUser.getUsername());
				bw.newLine();
				bw.close();
				outStream.writeObject("Mensagem enviada");
			} else {
				File f = new File("chat.txt");
				f.createNewFile();
				FileWriter fw = new FileWriter(chat, true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(user + "/" + message + "/" + currentUser.getUsername());
				bw.newLine();
				bw.close();
				outStream.writeObject("Mensagem enviada");
			}
		}
		
	}

	private void viewWine(User currentUser, String wineID, ObjectOutputStream outStream) throws Exception {
		Wines wine = getWine(wineID);
		Wines wine2 = getWineForSale(wineID);
		outStream.writeObject("Vinho : "+wine2.getWinename()+ " vendido por: "+wine2.getUsername()+ " preço: "+wine2.getPrice()+" quantidade: "+wine2.getQuantity()+" com classificacao: "+wine2.getClassify());
	    byte[] buffer = new byte[Integer.MAX_VALUE];
	    FileInputStream f = new FileInputStream("images\\ "+wine.getimage());
	    int bytes = f.read(buffer,0,buffer.length);
	    dataOutputStream.write(buffer,0,bytes);
	}

	private void writeObjectToFile(File f, Object o) throws IOException{
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(o);
		oos.close();
	}

	private Object readObjectFromFile(File f) throws IOException, ClassNotFoundException{
		FileInputStream fi = new FileInputStream(f);
		ObjectInputStream oi = new ObjectInputStream(fi);
		Object o = oi.readObject();
		oi.close();
		return o;
	}

	private void writeFile(FileWriter f, String s) throws IOException{
		BufferedWriter bw = new BufferedWriter(f);
		bw.write(s);
		bw.newLine();
		bw.close();

	}
	/** 
	private void rewriteUsers() throws IOException {
		FileWriter fw= new FileWriter (users, false);
		for (User u : userList) {
			String s = u.getUsername() + " " + u.getWallet();
			writeFile(fw, s);
		}
	}

	private void rewriteWineforsale() throws IOException {
		FileWriter fw= new FileWriter (winesforsale, false);
		for (Wines w : winesForSaleList) {
			String s = w.getWinename() + " " + w.getUsername() + " " + w.get;
			writeFile(fw, s);
		}
	}
	*/
}