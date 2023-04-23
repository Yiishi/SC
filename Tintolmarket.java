/**
 * @author Diogo Matos fc52808
 * @author David Guilherme fc56333
 * @author Vitor Medeiros fc56351
 */


import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import javax.net.ssl.*;

public class Tintolmarket {
    BufferedReader br;
    File wines = new File("wines.txt");
    File users = new File("userLog.txt");
    File winesforsale = new File("winesforsale.txt");
    static User user;
    private static String hostName;
    private static int portNumber;
    private static Socket clientSocket;
    private static ObjectOutputStream outToServer;
    private static ObjectInputStream inFromServer;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;
    private static String truststore;
    private static String keystore;
    private static String passwordKeystore;
    public static void main(String[] args) throws Exception {
        try {
            if (args.length == 5) {

                truststore = args[1];
                keystore = args[2];
                passwordKeystore = args[3];

                String[] st = args[0].split(":");

                if (st.length == 2) {
                    portNumber = Integer.parseInt(st[1]);
                } else {
                    portNumber = 12345;
                }

                SSLSocketFactory sslSocketFactory =
                    (SSLSocketFactory) SSLSocketFactory.getDefault();

                SSLSocket clientSocket =
                    (SSLSocket) sslSocketFactory.createSocket(st[0]/* hostname */, portNumber);

                //clientSocket = new Socket(st[0]/* hostname */, portNumber);
                outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
                inFromServer = new ObjectInputStream(clientSocket.getInputStream());
                dataInputStream = new DataInputStream(clientSocket.getInputStream());
                dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

                outToServer.writeObject(args[4]);//user id sent to server
                //outToServer.writeObject(args[2]); já n há password normal

                Long nonce = (Long) inFromServer.readObject();
                
                Scanner ler = new Scanner(System.in);

                System.out.println((String) inFromServer.readObject() + "\n");

                String userId = (String) inFromServer.readObject();
                int wallet = (int) inFromServer.readObject();
                user = new User(userId, wallet);

                while (1 == 1) {
                    System.out.println("\nMenu");
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
        } catch (Exception e) {
            // TODO Auto-generated catch block
            outToServer.close();
            inFromServer.close();				
            clientSocket.close();
            dataOutputStream.close();
            dataInputStream.close();
        }
    }

    public static void avaliaAcao(String acao) throws Exception {
        String[] split = acao.split(" ");

        if (split[0].equals("add") || split[0].equals("a")) {
            if (split.length == 3) {
                add(split[1], split[2]);

                //System.out.println((String) inFromServer.readObject());    
                //enviaImagem(split[2], outToServer);

                int i;
                File ifile = new File(split[2]);
                if(ifile.exists()){
                    System.out.println("ficheiro existe");
                }
                FileInputStream fis = new FileInputStream (split[2]);

                System.out.println(ifile.length());
                outToServer.writeLong(ifile.length());
                long fileLength = ifile.length();
                long acc = 0;
                byte[] buffer = new byte[1024];

                while (acc < fileLength){
                    i = fis.read(buffer);

                    if(i == -1){
                        System.out.println("Erro ao enviar a imagem");
						break;
					}
                    System.out.println(i);
                    outToServer.write(buffer, 0, i);
                    acc += i;
                }
                outToServer.flush();
                fis.close();

                System.out.println((String) inFromServer.readObject()); 
                
                
            } else {
                System.out.println("Por favor preencha todos os requisitos corretamente");
            }

        } else if (split[0].equals("sell") || split[0].equals("s")) {
            if (split.length == 4) {
                sell(split[1], Double.parseDouble(split[2]), Integer.parseInt(split[3]));
                System.out.println((String) inFromServer.readObject());
            } else {
                System.out.println("Por favor preencha todos os requisitos corretamente");
            }

        } else if (split[0].equals("view") || split[0].equals("v")) {
            if (split.length == 2) {
                view(split[1]);
                System.out.println((String) inFromServer.readObject());
                
            } else {
                System.out.println("Por favor preencha todos os requisitos corretamente");
            }

        } else if (split[0].equals("buy") || split[0].equals("b")) {
            if (split.length == 4) {
                buy(split[1], split[2], Integer.parseInt(split[3]));
                System.out.println((String) inFromServer.readObject());
            } else {
                System.out.println("Por favor preencha todos os requisitos corretamente");
            }

        } else if (split[0].equals("wallet") || split[0].equals("w")) {
            if (split.length == 1) {
                wallet();
                System.out.println((String) inFromServer.readObject());
            } else {
                System.out.println("Por favor preencha todos os requisitos corretamente");
            }

        } else if (split[0].equals("classify") || split[0].equals("c")) {
            if (split.length == 3) {
                classify(split[1], Integer.parseInt(split[2]));
                System.out.println((String) inFromServer.readObject());
            } else {
                System.out.println("Por favor preencha todos os requisitos corretamente");
            }

        } else if (split[0].equals("talk") || split[0].equals("t")) {
            StringBuilder msg = new StringBuilder();
            for (int i = 2; i < split.length; i++) {
                msg.append(split[i]);
                msg.append(" ");
            }
            
            talk(split[1], msg.toString());
            System.out.println((String) inFromServer.readObject());

        } else if (split[0].equals("read") || split[0].equals("r")) {
            if (split.length == 1) {
                read();
                System.out.println((String) inFromServer.readObject());
            } else {
                System.out.println("Por favor preencha todos os requisitos corretamente");
            }
        } else {
            System.out.println("Por favor preencha todos os requisitos corretamente");
        }
    }

    public static void wallet() throws IOException {
        outToServer.writeObject("wallet");
    }

    public static void classify(String wine, int stars) throws Exception {
        outToServer.writeObject("classify " + wine + " " + stars);
    }

    public static void talk(String user, String message) throws Exception {
        System.out.println("talk");
        outToServer.writeObject("talk/" + user + "/" + message);
    }

    public static void read() throws Exception {
        outToServer.writeObject("read");
    }

    public static void add(String wine, String image) throws Exception {
         
        outToServer.writeObject("add " + wine + " " + image);
    }

    public static void sell(String wine, double value, int quantity) throws Exception {
        outToServer.writeObject("sell " + wine + " " + value + " " + quantity);
    }

    public static void buy(String wine, String seller, int quantity) throws Exception {
        outToServer.writeObject("buy " + wine + " " + seller + " " + quantity);
    }

    public static void view(String wine) throws Exception {
        
        outToServer.writeObject("view " + wine);

    }

    private static void enviaImagem (String image, ObjectOutputStream os) throws IOException{
        int i;
        File ifile = new File(image);
        if(ifile.exists()){
            System.out.println("ficheiro existe");
        }
        FileInputStream fis = new FileInputStream (image);

        System.out.println(ifile.length());
        os.writeLong(ifile.length());

        while ((i = fis.read()) > -1){
            System.out.println(i);
            os.write(i);
        }
    }

    private static void recebeImagem () throws IOException, ClassNotFoundException{

        String image = (String) inFromServer.readObject();
        FileOutputStream fout = new FileOutputStream(image);
    
        int i;
        while ( (i = dataInputStream.read()) > -1) {
            
            fout.write(i);
        }

    }
}
