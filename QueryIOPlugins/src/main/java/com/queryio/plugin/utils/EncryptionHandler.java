package com.queryio.plugin.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import net.jpountz.lz4.LZ4JavaSafeInputStream;
import net.jpountz.lz4.LZ4JavaSafeOutputStream;

import org.apache.log4j.Logger;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

public class EncryptionHandler {
	private static Logger logger = Logger.getLogger(EncryptionHandler.class);

	//FIXME Hardcoded for now, should be moved to some secure file with restricted access.
	private static char[] password = "QueryIO123456qwert".toCharArray();

	
	private static byte[] initVector = new byte[] {
			  (byte) 0x8E, 0x12, 0x39, (byte) 0x9C, 0x07, 0x72, 0x6F, 0x5A,
			  (byte) 0x8E, 0x12, 0x39, (byte) 0x9C, 0x07, 0x72, 0x6F, 0x5A
			};
	
	private static byte[] salt = null; // Created using SecureRandom.getSeed(8);
	
	private static int iterationCount = 65536; 
	private static final int KEY_LEN = 256;
	
	private static final String SECRETKEY_ALGO = "PBKDF2WithHmacSHA1";
	private static final String SECRETKEYSPEC_ALGO = "AES";
	private static final String  TRANSFORMATION = "AES/CBC/PKCS5Padding";
	
	public static final int COMPRESSION_TYPE_NONE = 0;
	public static final int COMPRESSION_TYPE_GZIP = 1;
	public static final int COMPRESSION_TYPE_SNAPPY = 2;
	public static final int COMPRESSION_TYPE_LZ4 = 3;
	
	static {
		try{
			salt = "3���8O������*".getBytes("UTF-8"); // Created using
			// SecureRandom.getSeed(8);
		} catch(Exception e){
			throw new Error(e);
		}
	}
	
	//We are using same key each time, so instead of creating new instance, creating a static var.
	private static SecretKey secret; 
			
	private Cipher cipher;
	
	static {
		try{
			//TODO check if these are thread safe
			
				SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRETKEY_ALGO);
				KeySpec spec = new PBEKeySpec(password, salt, iterationCount, KEY_LEN);
				SecretKey tmp = factory.generateSecret(spec);
				secret = new SecretKeySpec(tmp.getEncoded(), SECRETKEYSPEC_ALGO);
		
		}catch(Exception e){
			logger.error("Error initializing Encryption handler ", e);
		}
	}
	
	public EncryptionHandler(int mode, boolean encrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		
			if(encrypt){
				cipher = Cipher.getInstance(TRANSFORMATION);
				cipher.init(mode, secret, new IvParameterSpec(initVector));
			}
		
	}

	public InputStream getCompressedCipherInputStream(InputStream inputStream, boolean encrypt, int compressionType) throws IOException {
		InputStream responseStream = inputStream;
		if (encrypt) {
			logger.debug("Decryption enabled");
			responseStream = new CipherInputStream(responseStream, cipher);
		}
		
		if (compressionType==EncryptionHandler.COMPRESSION_TYPE_GZIP) {
			logger.debug("Decompressing data - GZ");
			responseStream = new GZIPInputStream(responseStream);
		}
		else if (compressionType==EncryptionHandler.COMPRESSION_TYPE_SNAPPY) {
			logger.debug("Decompressing data - SNAPPY");
			responseStream = new SnappyInputStream(responseStream);
		}
		else if (compressionType==EncryptionHandler.COMPRESSION_TYPE_LZ4) {
			logger.debug("Decompressing data - LZ4");
			responseStream = new LZ4JavaSafeInputStream(responseStream);
		}
		
		return responseStream;
	}
	
	public OutputStream getCompressedCipherOutputStream(OutputStream outputStream, boolean encrypt, int compressionType) throws IOException {
		OutputStream responseStream = outputStream;
		if (encrypt) {
			logger.debug("Encryption enabled");
			responseStream = new CipherOutputStream(responseStream, cipher);
		} 
		
		if (compressionType==EncryptionHandler.COMPRESSION_TYPE_GZIP) {
			logger.debug("Compressing data - GZ");
			responseStream = new GZIPOutputStream(responseStream);
		}
		else if (compressionType==EncryptionHandler.COMPRESSION_TYPE_SNAPPY) {
			logger.debug("Compressing data - SNAPPY");
			responseStream = new SnappyOutputStream(responseStream);
		}
		else if (compressionType==EncryptionHandler.COMPRESSION_TYPE_LZ4) {
			logger.debug("Compressing data - LZ4");
			responseStream = new LZ4JavaSafeOutputStream(responseStream);
		}
		
		return responseStream;
	}

	public static void main(String[] args) throws Exception {
		EncryptionHandler handler = new EncryptionHandler(Cipher.ENCRYPT_MODE, true);
		byte[] encryptedBytes = handler.cipher.doFinal("AloqDmWyK/rX2e/8fgqxAw==".getBytes());
		
		System.out.println("Encrypted: " + String.valueOf(encryptedBytes));
		
		handler = new EncryptionHandler(Cipher.DECRYPT_MODE, true);
		System.out.println("Decrypted: " + new String(handler.cipher.doFinal(encryptedBytes)));
	}
}