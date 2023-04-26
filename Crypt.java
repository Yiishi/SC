import java.io.*;
import java.io.IOException;
import java.io.OutputStream;
import java.security.*;
import javax.crypto.*;

public class Crypt {
	static Cipher c;
	public Crypt () throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.c= Cipher.getInstance("RSA");
	}
	
	public static byte[] Encrypt(Key key, StringBuilder message) throws InvalidKeyException, IOException {
		c.init(Cipher.ENCRYPT_MODE,key);
		byte[] b;
		b =(message.toString()).getBytes();
		return b;
	}
	
	public static String Decrypt(Key key, byte[] message)throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		c.init(Cipher.DECRYPT_MODE, key);
		 byte[] b = c.doFinal(message);	
		return new String(b);
	}

	public static void DecryptFile(Key key, OutputStream out, FileInputStream fis, FileOutputStream fos)throws InvalidKeyException, IOException {
		c.init(Cipher.DECRYPT_MODE, key);
		CipherInputStream cos = new CipherInputStream(fis, c);
		byte[] b = new byte[16];
		int i = fis.read(b);
        while (i != -1) {
         	fos.write(b, 0, i);
         	i = fis.read(b);
        }
		cos.close();
	}
	
	public static void EncryptFile(Key key, OutputStream out, FileInputStream fis, FileOutputStream fos)throws InvalidKeyException, IOException {
		c.init(Cipher.ENCRYPT_MODE, key);
		CipherOutputStream cos = new CipherOutputStream(fos, c);
		byte[] b = new byte[16];
		int i = fis.read(b);
        while (i != -1) {
         	cos.write(b, 0, i);
         	i = fis.read(b);
        }
		cos.close();
	}

}
