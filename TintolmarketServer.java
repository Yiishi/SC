
/***************************************************************************
*   Seguranca e Confiabilidade 2020/21
*
*
***************************************************************************/

/**
 * @author Diogo Matos fc52808
 * @author David Guilherme fc56333
 * @author Vitor Medeiros fc56351
 */

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.*;
import javax.print.attribute.standard.NumberOfInterveningJobs;



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
	private ArrayList<Block> blockchain;
	private File diretorio;
	private String passwordCifra;
	private String keyStore;
	private String passwordKeyStore;
	private int TransactionID=1;
	private int blockID=1;
	private String certificado;
	private SecretKey secret;
	private Key privateKey;


	/**
	 * @param port
	 * @throws Exception
	 */

	@SuppressWarnings({"unchecked"})
	public TintolmarketServer(int port, String pass) throws Exception {
		this.port = port;
		userList = new ArrayList<User>();
		winesList = new ArrayList<Wines>();
		winesForSaleList = new ArrayList<Wines>();
		blockchain= new ArrayList<Block>();
		passwordCifra = pass;
		diretorio = new File("/images");


				

		br = new BufferedReader(new FileReader("users.txt"));
        if(!(br.readLine() == null)){
            userList = transformarEmUserUser((ArrayList<String>)readObjectFromFile(users));
        }

        br = new BufferedReader(new FileReader("wines.txt"));
        if(!(br.readLine() == null)){
            winesList = transformarEmWinesWines((ArrayList<String>)readObjectFromFile(wines));
        }

        br = new BufferedReader(new FileReader("winesforsale.txt"));
        if(!(br.readLine() == null)){
            winesForSaleList = transformarEmWinesWinesForSale((ArrayList<String>)readObjectFromFile(winesforsale));
        }

	}

	public static void main(String[] args) throws Exception {
		System.out.println("servidor: main");

		if(args.length == 0){
			TintolmarketServer server = new TintolmarketServer(12345, args[0]);
			server.startServer(args);
		}else{
			TintolmarketServer server = new TintolmarketServer(Integer.parseInt(args[0]), args[1]);
			server.startServer(args);
		}

		
		
		
	}

	public void startServer(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
		//ServerSocket sSoc = null;
		
		SSLServerSocket sSoc = null;
		try {
			if (args.length == 3) {
				

				String salt = "12345678";
				SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
				KeySpec spec = new PBEKeySpec(passwordCifra.toCharArray(), salt.getBytes(), 65536, 256);
				secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");



				keyStore = args[1];
				passwordKeyStore = args[2];

				System.setProperty("javax.net.ssl.keyStore", keyStore);
				System.setProperty("javax.net.ssl.keyStorePassword", passwordKeyStore);
		
				SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
				sSoc = (SSLServerSocket) sslServerSocketFactory.createServerSocket(12345);
			} else {
				passwordCifra = args[1];
				keyStore = args[2];
				passwordKeyStore = args[3];

				System.setProperty("javax.net.ssl.keyStore", keyStore);
				System.setProperty("javax.net.ssl.keyStorePassword", passwordKeyStore);
		
				SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
				sSoc = (SSLServerSocket) sslServerSocketFactory.createServerSocket(Integer.parseInt(args[0]));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		while (true) {
			try {
				//Socket inSoc = sSoc.accept();
				Socket inSoc = (Socket) sSoc.accept();
				ServerThread newServerThread = new ServerThread(inSoc);
				newServerThread.start();

				//new ServerThread(sSoc.accept()).start( ); // uma thread por ligação
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
				//private and public key initializing (i believe same has to be done on Client side, but not sure, for further investigation)
				// KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
				// kpg.initialize(2048);
				// KeyPair kp= kpg.generateKeyPair();
				// PublicKey ku = kp.getPublic();
				// PrivateKey kr = kp.getPrivate();
				// Cipher c = Cipher.getInstance("RSA");
				// c.init(Cipher.ENCRYPT_MODE, kr);
				
				//cipher initializing to cipher everything, after that instead of using outstream cos.write on every method (i believe)
				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
				// CipherOutputStream cos= new CipherOutputStream(outStream,c);
				/*
				 * for decypher could be changing the instream and then the argument for evaluate request(last argument)
				 * byte [] keyEnconded2 (what's read from the file)
				 * SecretKeySpec keySpec = new SecretKeySpec(keyEncoded2, "RSA")
				 * c.init(Cipher.DECRYPT_MODE, keySpec2)
				 *(the encrypt has to be also done from client side for our decypher to actually work)
				 */
				String user = null;


				try {

					user = (String) inStream.readObject();
					
					// passwd = (String) inStream.readObject();
					System.out.println("thread: depois de receber a password e o user");
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}

				User currentUser = null;
				
				KeyStore kstore = KeyStore.getInstance("PKCS12");
				FileInputStream kfile = new FileInputStream(keyStore); //keystore
				kstore.load(kfile, passwordKeyStore.toCharArray());
				privateKey = kstore.getKey("server", passwordKeyStore.toCharArray());
				FileOutputStream fos = new FileOutputStream("userLog.txt");
				FileInputStream fis = new FileInputStream("EncryptedUserLog.cif");

				FileReader fr = new FileReader("EncryptedUserLog.cif");
				BufferedReader br2 = new BufferedReader(fr);

				if((br2.readLine()) != null){
					Crypt.DecryptFile(privateKey);
				}
				br2.close();
				fis.close();
				fos.close();
				BufferedReader br = new BufferedReader(new FileReader(new File("userLog.txt")));
				boolean userExists = false;


				for (String x = br.readLine(); x != null; x = br.readLine()) {
					String[] split = x.split(":");
					if (split[0].equals(user)) {
						certificado = split[1];
						userExists = true;
						break;
					}
				}

				long nonse;
				Random rd = new Random();
				nonse = rd.nextLong();
				outStream.writeObject(nonse);
				if (userExists == false) {
					
					outStream.writeObject("404 NOT FOUND");

					Long received_nonse = (Long) inStream.readObject();
					byte[] encrypted = (byte[]) inStream.readObject();
					String keycert = (String) inStream.readObject();
					FileInputStream fis3 = new FileInputStream(keycert);
					CertificateFactory cf = CertificateFactory.getInstance("X509");
					Certificate cert = cf.generateCertificate(fis3);	
					PublicKey pk = cert.getPublicKey();

					Long desencrypted = Long.parseLong(Crypt.Decrypt(pk, encrypted));

					if(received_nonse != nonse || desencrypted != nonse){
						outStream.writeObject("fechar");
					}else{
						outStream.writeObject("nao fechar");
					}

					currentUser = new User(user, 200, keycert);
					userList.add(currentUser);

					writeObjectToFile(users, transformarUser(userList));
					outStream.writeObject("Registado");
					FileWriter fw= new FileWriter (userlog, true);
					String s = user+":"+user+"pub.cer";
					writeFile(fw, s);

				} else{
					outStream.writeObject("202 ACCEPTED");
					String encrypted = (String) inStream.readObject();
					for (User u : userList) {
						if (u.getUsername().equals(user)) {
							currentUser = u;
							break;
						}
					}

					FileInputStream fis3 = new FileInputStream(currentUser.getPk());
					CertificateFactory cf = CertificateFactory.getInstance("X509");
					Certificate cert = cf.generateCertificate(fis3);	
					PublicKey pk = cert.getPublicKey();

					Long desencrypted = Long.parseLong(Crypt.Decrypt(pk, encrypted.getBytes()));
					if(desencrypted == nonse){
						outStream.writeObject("nao fechar");
						outStream.writeObject("autenticado");
					}else{
						outStream.writeObject("fechar");
					}
				}

				FileOutputStream fos2 = new FileOutputStream("EncryptedUserLog.txt");
				FileInputStream fis2 = new FileInputStream("userLog.txt");
				Certificate cert = (Certificate) kstore.getCertificate("server");
				PublicKey pk = cert.getPublicKey( ); 
				Crypt.EncryptFile(pk);
				fis2.close();
				fos2.close();
				outStream.writeObject(currentUser.getUsername());
				outStream.writeObject(currentUser.getWallet());

				boolean quit = false;
				while (quit == false) {
					String acao = (String) inStream.readObject();
					if(acao.equals("quit")){
						quit = true;
					}else{
						avaluateRequest(acao, currentUser, outStream, inStream);
					}
				}

				outStream.close();
				inStream.close();				
				socket.close();

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();					
			}
		}
	}

	public void avaluateRequest(String str, User currentUser, ObjectOutputStream outStream, ObjectInputStream inStream) throws Exception {

		String[] split = str.split(" ", 2);
		String[] split2 = str.split(" ");

		if (split[0].equals("add")) {

			addWine(split2[1], split2[2], outStream, inStream);

		} else if (split[0].equals("sell")) {

			sellWine(split2[1], Double.parseDouble(split2[2]), Integer.parseInt(split2[3]), currentUser, outStream, inStream);

		} else if (split[0].equals("view")) {


			viewWine(currentUser, split2[1], outStream);

		} else if (split[0].equals("buy")) {

			buyWine(split2[1], split2[2], Integer.parseInt(split2[3]), currentUser, outStream, inStream);

		} else if (split[0].equals("classify")) {

			classifyWine(split2[1], Integer.parseInt(split2[2]), currentUser, outStream);

		} else if (split[0].contains("talk")) {
			String[] split3 = str.split("/");
			talk(split3[1], split3[2], currentUser, outStream);

		} else if (str.equals("read")) {

			readMessege(currentUser, outStream);

		} else if (str.equals("wallet")){
			wallet(currentUser, outStream);
		}

	}

	private void wallet(User currentUser, ObjectOutputStream outStream) throws IOException {
		outStream.writeObject("saldo: "+ currentUser.getWallet());
	}

	private void addWine(String wine, String image, ObjectOutputStream outStream, ObjectInputStream inStream) throws Exception {
		if (getWine(wine) == null) {

			boolean b = false;
			outStream.writeObject(b);


			Wines newWine = new Wines(wine, "", 0, 0, image);
			winesList.add(newWine);
			
			//outStream.writeObject("O vinho foi adicionado ao catalogo");

			long fileSize = inStream.readLong();
			try {
				FileOutputStream fos = new FileOutputStream("images/"+image);

				byte[] buffer = new byte[1024];
				int bytesread = 0;
				long bytesRecived = 0;

				while (bytesRecived < fileSize){
					bytesread = inStream.read(buffer);
					
					if(bytesread == -1){
						break;
					}

					fos.write(buffer, 0, bytesread);
					bytesRecived += bytesread;
				}

				fos.flush();
				fos.close();
				writeObjectToFile(wines, transformarWines(winesList));

				outStream.writeObject("imagem adicionda com sucesso");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			boolean b = true;
			outStream.writeObject(b);
			outStream.writeObject("O vinho que deseja adicionar ja se encontra no catalogo");
		}
	}

	private void buyWine(String wineID, String sellerID, int quantity, User currentUser, ObjectOutputStream outStream, ObjectInputStream inStream)
		throws Exception {
			Signature s = (Signature) inStream.readObject();
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
				writeObjectToFile(winesforsale, transformarWinesForSale(winesForSaleList));
				for (User i : userList) {
					if (i.getUsername().equals(sellerID)) {
						i.Soldsomething(wine.getPrice());
						break;
					}
				}
			

			for (User i : userList) {
				if (i.getUsername().equals(currentUser.getUsername())) {
					i.Boughtsomething(wine.getPrice());
					break;
				}
			}
			int index = blockchain.size();
			if(index!=0 && blockchain.get(index-1).getTransactionsLength()<5) {
				blockchain.get(index-1).addTransaction(new Transaction(TransactionID, "buy", currentUser.getUsername(), wine.getPrice()));
				TransactionID++;
			}else {
				if(blockID!=1) {
					Block newblock=new Block(Integer.toString(blockID), blockchain.get(index-1).calculateBlockHash());
					newblock.addTransaction(new Transaction(TransactionID, "buy",currentUser.getUsername(), wine.getPrice()));
					blockchain.add(newblock);
					TransactionID++;
					blockID++;
				}else {
					Block newblock = new Block(Integer.toString(blockID));
					newblock.addTransaction(new Transaction(TransactionID, "buy",currentUser.getUsername(), wine.getPrice()));
					blockchain.add(newblock);
					TransactionID++;
					blockID++;
				}
			}
			
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
			writeObjectToFile(winesforsale, transformarWinesForSale(winesForSaleList));
			outStream.writeObject("Obrigado pela sua avaliacao");
		}
	}

	private void readMessege(User currentUser, ObjectOutputStream outStream) throws Exception {
		br = new BufferedReader(new FileReader("chat.txt"));
		// boolean noMSG = true;
		String st9;
		String split9[] = new String[3];
		StringBuilder msg = new StringBuilder();
		StringBuilder msg2 = new StringBuilder();
		boolean close = false;
		while ((st9 = br.readLine()) != null) {
			split9 = st9.split("/");
			if (split9[0].equals(currentUser.getUsername())) {
				msg.append(split9[1]);
				outStream.writeObject(msg.toString());
	
				msg2.append("  Message from : ").append(split9[2]).append("/n");
				outStream.writeObject(msg2.toString());
				outStream.writeObject(close);
				// noMSG = false;
			}
		}
		close = true;
		outStream.writeObject(close);
	}

	private void sellWine(String name, double value, int quantity, User currentUser, ObjectOutputStream outStream, ObjectInputStream inStream)
		throws Exception {
			Signature s = (Signature) inStream.readObject();
			if (getWine(name) == null) {
				outStream.writeObject("O vinho que pretende vender nao se encontra no catalogo");
			} else {
				Wines newWine = new Wines(name, currentUser.getUsername(), value, quantity, null);
				winesForSaleList.add(newWine);
				writeObjectToFile(winesforsale, transformarWinesForSale(winesForSaleList));
				int index = blockchain.size();
				if(index!=0 && blockchain.get(index-1).getTransactionsLength()<5) {
					blockchain.get(index-1).addTransaction(new Transaction(TransactionID, "buy", currentUser.getUsername(), newWine.getPrice()));
					TransactionID++;
				}else {
					if(blockID!=1) {
						Block newblock=new Block(Integer.toString(blockID), blockchain.get(index-1).calculateBlockHash());
						newblock.addTransaction(new Transaction(TransactionID, "buy",currentUser.getUsername(), newWine.getPrice()));
						blockchain.add(newblock);
						TransactionID++;
						blockID++;
					}else {
						Block newblock = new Block(Integer.toString(blockID));
						newblock.addTransaction(new Transaction(TransactionID, "buy",currentUser.getUsername(), newWine.getPrice()));
						blockchain.add(newblock);
						TransactionID++;
						blockID++;
					}
				outStream.writeObject("Vinho colocado a venda com sucesso");	
				}
		}
	}

	private void talk(String user, String message, User currentUser, ObjectOutputStream outStream) throws Exception {


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
		System.out.println(wine.getWinename());
		Wines wine2 = getWineForSale(wineID);

		String image = wine.getimage();

		outStream.writeObject(image);
		

		int i;
		File ifile = new File("images/"+image);
		if(ifile.exists()){
			System.out.println("ficheiro existe");
		}
        FileInputStream fis = new FileInputStream (ifile);

		outStream.writeLong(ifile.length());
		long fileLength = ifile.length();
		long acc = 0;
		byte[] buffer = new byte[1024];

		while (acc < fileLength){
			i = fis.read(buffer);

			if(i == -1){
				System.out.println("Erro ao enviar a imagem");
				break;
			}
			outStream.write(buffer, 0, i);
			acc += i;}
		
		fis.close();

		if(wine2 == null){
			outStream.writeObject("Vinho : "+wine.getWinename()+ "Classificacao media : "+wine.getClassify()+ 
								  "Imagem : "+wine.getimage());
		}else{
			outStream.writeObject("Vinho : "+wine2.getWinename()+ " vendido por: "+wine2.getUsername()+ " preco: "+wine2.getPrice()
		                         +" quantidade: "+wine2.getQuantity()+" com classificacao: "+wine2.getClassify());
		}
		
	    
		fis.close();
	}

	private void writeObjectToFile(File f, Object o) throws IOException{
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(o);
		oos.close();
	}

	private ArrayList<String> transformarUser(ArrayList<User> users){
		ArrayList<String> ars = new ArrayList<>();
		for (User u : users) {
			ars.add(u.getUsername() + " " + u.getWallet() + " " + u.getPk());
		}
		return ars;
	}

	private ArrayList<User> transformarEmUserUser(ArrayList<String> data) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, FileNotFoundException, CertificateException{
		ArrayList<User> users = new ArrayList<>();
		for (String s : data) {
			String[] split = s.split(" ");

			users.add(new User(split[0], Integer.parseInt(split[1]),split[2]));
			break;
			
		}
		return users; 
	}

	private ArrayList<String> transformarWines(ArrayList<Wines> wines){
		ArrayList<String> ars = new ArrayList<>();
		for (Wines w : wines) {
			ars.add(w.getWinename() +" "+ w.getimage());
			System.out.println(w.getimage());
		}
		return ars;
	}

	private ArrayList<Wines> transformarEmWinesWines(ArrayList<String> data){
		ArrayList<Wines> wines = new ArrayList<>();
		for (String s : data) {
			String[] split = s.split(" ");
			wines.add(new Wines(split[0],null,0,0, split[1]));
		}
		return wines;
	}

	private ArrayList<String> transformarWinesForSale(ArrayList<Wines> winesForSale){
		ArrayList<String> ars = new ArrayList<>();
		for (Wines w : winesForSale) {
			ars.add(w.getWinename() +" "+w.getUsername()+" "+w.getPrice()+" "+w.getQuantity());
		}
		return ars;
	}

	private ArrayList<Wines> transformarEmWinesWinesForSale(ArrayList<String> data){
		ArrayList<Wines> wines = new ArrayList<>();
		for (String s : data) {
			String[] split = s.split(" ");
			wines.add(new Wines(split[0],split[1],Double.parseDouble(split[2]),Integer.parseInt(split[3]), null));
		}
		return wines;
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

}