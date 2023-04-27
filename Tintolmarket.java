/**
 * @author Diogo Matos fc52808
 * @author David Guilherme fc56333
 * @author Vitor Medeiros fc56351
 */


import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Scanner;
import java.util.zip.Inflater;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Tintolmarket {
    BufferedReader br;
    File wines = new File("wines.txt");
    File users = new File("userLog.txt");
    File winesforsale = new File("winesforsale.txt");
    static User user;
    private static int portNumber;
    private static Socket clientSocket;
    private static ObjectOutputStream outToServer;
    private static ObjectInputStream inFromServer;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;
    private static KeyStore trustStore;
    private static KeyStore keystore;
    private static Key privateKey;
    private static String passwordKeystore;
    
    public static void main(String[] args) throws Exception {
        //try {
            
            if (args.length == 5) { 

                String truststoreS = args[1];
                String keystoreS = args[2];
                passwordKeystore = args[3];

                System.setProperty("javax.net.ssl.trustStore", args[1]);
                System.setProperty("javax.net.ssl.trustStorePassword", "123456");   

                System.setProperty("javax.net.ssl.keyStore", args[2]);
                System.setProperty("javax.net.ssl.keyStorePassword", args[3]); 

                System.out.println("43");

                FileInputStream Tfile = new FileInputStream(truststoreS);
                System.out.println("46");
                trustStore = KeyStore.getInstance("PKCS12");
                System.out.println("48");
                trustStore.load(Tfile, passwordKeystore.toCharArray());
                System.out.println("50");
                FileInputStream kfile = new FileInputStream(keystoreS);
                keystore = KeyStore.getInstance("PKCS12");
                keystore.load(kfile, passwordKeystore.toCharArray());
                privateKey = keystore.getKey(args[4], passwordKeystore.toCharArray());
                Certificate cert = (Certificate) keystore.getCertificate(args[4]);
                PublicKey pk = cert.getPublicKey();
                
                String[] st = args[0].split(":");

                if (st.length == 2) {
                    portNumber = Integer.parseInt(st[1]);
                } else {
                    portNumber = 12345;
                }

                   

                SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

                SSLSocket clientSocket = (SSLSocket) sslSocketFactory.createSocket(st[0]/* hostname */, portNumber);

                //clientSocket = new Socket(st[0]/* hostname */, portNumber);
                outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
                inFromServer = new ObjectInputStream(clientSocket.getInputStream());
                dataInputStream = new DataInputStream(clientSocket.getInputStream());
                dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

                outToServer.writeObject(args[4]);//user id sent to server
                //outToServer.writeObject(args[2]); já n há password normal

                Long nonce = (Long) inFromServer.readObject();
                String flag = (String) inFromServer.readObject();
                Key myPrivateKey = keystore.getKey(args[4], "123456".toCharArray());
                //codificar o nonce com a private key
                byte[] encrypted = Crypt.Encrypt(myPrivateKey, nonce);
                if (flag.equals("202 ACCEPTED")) {
                    outToServer.writeObject(encrypted);
                    
                }else if(flag.equals("404 NOT FOUND")){
                    outToServer.writeObject(nonce);
                    outToServer.writeObject(encrypted);
                    outToServer.writeObject(pk);
                }

                Scanner ler = new Scanner(System.in);

                
                if(inFromServer.readObject().equals("fechar")){

                }else{
                    System.out.println((String) inFromServer.readObject() + "\n");
                    String userId = (String) inFromServer.readObject();
                    int wallet = (int) inFromServer.readObject();
                    user = new User(userId, wallet, pk);

                    

                    boolean quit = false;
                    while (quit == false) {
                        System.out.println("\nMenu");
                        System.out.println("Adicionar um vinho ao catalogo : add wineName image");
                        System.out.println("Colocar um vinho do catalogo a venda: sell wineName value quantity");
                        System.out.println("Ver um vinho: view wineName");
                        System.out.println("Comprar vinho: buy wineName seller quantity");
                        System.out.println("Verificar carteira: wallet");
                        System.out.println("Classificar vinho: classify wineName stars");
                        System.out.println("Enviar mensagem: talk user message");
                        System.out.println("Ler mensagens: read");
                        System.out.println("Fechar: quit\n");

                        String acao = ler.nextLine();
                        if(acao.equals("quit")){
                            outToServer.writeObject("quit");
                            quit = true;
                        }else{
                            avaliaAcao(acao);
                        }
                    }
                }
                ler.close();
                outToServer.close();
                inFromServer.close();				
                clientSocket.close();
                dataOutputStream.close();
                dataInputStream.close();
            }
        // } catch (Exception e) {
        //     outToServer.writeObject("quit");
        //     outToServer.close();
        //     inFromServer.close();				
        //     clientSocket.close();
        //     dataOutputStream.close();
        //     dataInputStream.close();
        // }
    }

    public static void avaliaAcao(String acao) throws Exception {
        String[] split = acao.split(" ");

        if (split[0].equals("add") || split[0].equals("a")) {
            if (split.length == 3) {
                add(split[1], split[2]);
                if((Boolean) inFromServer.readObject() == false){

                    System.out.println("aqui");
    
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
                        acc += i;}
                    
                    fis.close();
                    outToServer.flush();
                }
                
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
            Certificate cert2 = (Certificate) trustStore.getCertificate(split[1]);
            PublicKey pk = cert2.getPublicKey( );
            byte[] b = Crypt.Encrypt(pk, msg);
            talk(split[1], b);
            System.out.println((String) inFromServer.readObject());

        } else if (split[0].equals("read") || split[0].equals("r")) {
            if (split.length == 1) {
                read();
                boolean close = false;
                while (close == false) {
                    byte[] msg = (byte[]) inFromServer.readObject();
                    String msg2 = (String) inFromServer.readObject();
                    System.out.println(Crypt.Decrypt(privateKey, msg));
                    System.out.println(msg2);
                    if ((boolean) inFromServer.readObject() == true) {
                        close = true;  
                    }   
                }
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

    public static void talk(String user, byte[] b) throws Exception {
        System.out.println("talk");
        outToServer.writeObject("talk/" + user + "/" + b);
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

        String image =(String) inFromServer.readObject();

        long fileSize = inFromServer.readLong();

        System.out.println("aqui");

        try {
            FileOutputStream fos = new FileOutputStream("images/"+image);

            byte[] buffer = new byte[1024];
            int bytesread = 0;
            long bytesRecived = 0;

            System.out.println("file size: " + fileSize);
            while (bytesRecived < fileSize){
                bytesread = inFromServer.read(buffer);
                
                if(bytesread == -1){
                    break;
                }

                fos.write(buffer, 0, bytesread);
                bytesRecived += bytesread;
                System.out.println(bytesRecived);
            }

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
