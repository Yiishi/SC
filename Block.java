import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.logging.*;
public class Block {
	
    
	private String hash;
    private String previousHash;
    private String assinatura;

    private ArrayList<Transaction> transactions;
 
    public Block(String assinatura, String previousHash) {
        this.assinatura = assinatura;
        this.previousHash = previousHash;
        this.hash = calculateBlockHash();
    }
    
    public Block (String assinatura) {
    	this.assinatura=assinatura;
    	this.hash=calculateBlockHash();
    }
    
    public String calculateBlockHash() {
        String dataToHash = previousHash 
          + assinatura;
        MessageDigest digest = null;
        byte[] bytes = null;
        try { //mudar cenas dentro de try catch para ser thread safe
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }
    
    
    
    public int getTransactionsLength() {
    	return transactions.size();
    }
    
    public String addTransaction(Transaction transaction) {
    	if(transactions.size()==5) {
    		return "este bloco, já tem 5 transacoes, adicione um bloco novo a blockchain";
    	}
    	transactions.add(transaction);
    	return "transacao adicionada";
    }
    
    public void List() {
    	
    }

}
