import java.security.*;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
public class Crypt {
	Cipher c;
	public Crypt () throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.c= Cipher.getInstance("RSA");
	}
	
	public Cipher Encrypt(Key key) throws InvalidKeyException {
		c.init(Cipher.ENCRYPT_MODE,key);
		return c;
	}
	
	public Cipher Decrypt(Key key)throws InvalidKeyException {
		c.init(Cipher.DECRYPT_MODE, key);
		return c;
	}

}
