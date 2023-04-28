import java.io.*;
import java.io.IOException;
import java.io.OutputStream;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {
	static Cipher c;
	public Crypt () throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.c= Cipher.getInstance("RSA");
	}
	
	public static byte[] Encrypt(Key key, StringBuilder message) throws InvalidKeyException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		c= Cipher.getInstance("RSA");
		c.init(Cipher.ENCRYPT_MODE,key);
		byte[] b = message.toString().getBytes();
		c.update(b);
		return c.doFinal();
	}

	public static byte[] Encrypt(Key key, Long message) throws InvalidKeyException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		c= Cipher.getInstance("RSA");
		c.init(Cipher.ENCRYPT_MODE,key);
		byte[] b = message.toString().getBytes();
		c.update(b);
		return c.doFinal();
	}
	
	public static String Decrypt(Key key, byte[] message)throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		c= Cipher.getInstance("RSA");
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] b = c.doFinal(message);	
		return new String(b);
	}

	public static void DecryptFile(Key key)throws InvalidKeyException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
		
		FileInputStream fisD;
        FileOutputStream fosD;
        CipherInputStream cos2;

        fisD = new FileInputStream("EncryptedUserLog.cif");
		File del = new File("userLog.txt");
		if()
        fosD = new FileOutputStream("userLog.txt");

        // SecretKeySpec keySpec2 = new SecretKeySpec(key.getEncoded(), "RSA");
		c= Cipher.getInstance("RSA");
        c.init(Cipher.DECRYPT_MODE, key);

        cos2 = new CipherInputStream(fisD, c);
        byte[] bD = new byte[200];
        int j = cos2.read(bD);
        while (j != -1) {
            fosD.write(bD, 0, j);
            j = fisD.read(bD);
        }

        cos2.close();
        fisD.close();
        fosD.close();
		
		// FileReader fr = new FileReader("EncryptedUserLog.cif");
		// BufferedReader br = new BufferedReader(fr);

		// FileWriter fw = new FileWriter("userLog.txt");
		// BufferedWriter bw = new BufferedWriter(fw);

		// String s;
		// StringBuilder sb = new StringBuilder();

        // while ((s = br.readLine()) != null) {
        //  	sb.append(s);
		// 	bw.write(Decrypt(key, sb.toString().getBytes()));
		// 	sb.delete(0, sb.length());
        // }


		// bw.flush();
		// fw.flush();

		// fr.close();
		// br.close();
		// fw.close();
		// bw.close();
	}
	
	public static void EncryptFile(Key key)throws InvalidKeyException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, key);

        FileInputStream fis;
        FileOutputStream fos;
        CipherOutputStream cos;

        fis = new FileInputStream("userLog.txt");
        fos = new FileOutputStream("EncryptedUserLog.cif");


        cos = new CipherOutputStream(fos, c);
        byte[] b = new byte[16];
        int i = fis.read(b);
        while (i != -1) {
            cos.write(b, 0, i);
            i = fis.read(b);
        }
		File del = new File("userLog.txt");
		del.delete();
        cos.close();
        fis.close();
        fos.close();

	}

}
