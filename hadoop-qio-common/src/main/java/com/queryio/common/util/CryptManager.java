package com.queryio.common.util;

import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptManager {
	private static final String UTF_8 = "UTF-8";
	private static final String SUFFIX = "saKS98d$";
	private static final String MASTER_KEY = "master_key";
	
    private Cipher encryptor;
    private Cipher decryptor;

    public CryptManager(String key) throws Exception 
    {
        MessageDigest md = MessageDigest.getInstance("MD5");
        key = key + SUFFIX;
        byte[] digest = md.digest(key.getBytes(UTF_8));
	    Key secretkey=new SecretKeySpec(digest, "Blowfish");

        encryptor = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
        encryptor.init(Cipher.ENCRYPT_MODE, secretkey);

        decryptor = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
        decryptor.init(Cipher.DECRYPT_MODE, secretkey);
    }
	
    public static CryptManager createInstance() throws Exception
    {
    	return createInstance(MASTER_KEY);
    }
    
    public static CryptManager createInstance(String id) throws Exception
    {
        return new CryptManager(id);
    }
    
	/**
	 * @param data : String for encryption
	 * @return	   : Encrypted String.
	 */
	public String encryptData(String data)throws Exception
	{
		return new String(Base64.encode(encryptor.doFinal(data.getBytes(UTF_8))), UTF_8);
	}

	/**
	 * @param encryptedData : String for decryption.
	 * @param key	:    key used to decrypt encrypted data. Key Should be same which use for encryption.
	 * @return		: returns the decrypted plain text.
	 */
	public String decryptData(String encryptedData)throws Exception
	{
		return new String(decryptor.doFinal(Base64.decode(encryptedData.getBytes(UTF_8))), UTF_8);
	}
	
	public static void main(String args[]){
		 String test = "admin";
		 //rPGHmLbem3g=
		 //VXxmAMzxIKU=
		 try {
			CryptManager cm = CryptManager.createInstance();
			String encryptedString = cm.encryptData(test.toUpperCase());
			System.out.println("encryptedString : " + encryptedString);
			
			System.out.println("Decrypt :" + cm.decryptData(encryptedString));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
}