package com.queryio.demo.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

import com.queryio.common.util.SecurityHandler;

import net.jpountz.lz4.LZ4JavaSafeInputStream;
import net.jpountz.lz4.LZ4JavaSafeOutputStream;

public class CustomEncryptionHandler {
	private static final Log LOG = LogFactory.getLog(CustomEncryptionHandler.class);

	private final static byte[] INIT_VECTOR = new byte[] { (byte) 0x8E, 0x12, 0x39, (byte) 0x9C, 0x07, 0x72, 0x6F, 0x5A,
			(byte) 0x8E, 0x12, 0x39, (byte) 0x9C, 0x07, 0x72, 0x6F, 0x5A };

	private static byte[] salt = null;

	private static final int ITERATION_COUNT = 65536;
	private static final int KEY_LEN = 256;

	private static final String SECRETKEY_ALGO = "PBKDF2WithHmacSHA1";
	private static final String SECRETKEYSPEC_ALGO = "AES";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

	public static final int COMPRESSION_TYPE_NONE = 0;
	public static final int COMPRESSION_TYPE_GZIP = 1;
	public static final int COMPRESSION_TYPE_SNAPPY = 2;
	public static final int COMPRESSION_TYPE_LZ4 = 3;

	public static final String COL_COMPRESSION_TYPE = "COMPRESSION_TYPE";
	public static final String COL_ENCRYPTION_TYPE = "ENCRYPTION_TYPE";

	public static final String SNAPPY = "SNAPPY";
	public static final String GZ = "GZ";
	public static final String LZ4 = "LZ4";
	public static final String NONE = "NONE";
	public static final String AES256 = "AES256";

	static {
		try {
			salt = "3�8O��*".getBytes("UTF-8"); // Created using
			// SecureRandom.getSeed(8);
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	public static boolean getEncryptionTypeValue(String type) {
		if (AES256.equals(type)) {
			return true;
		} else {
			return false;
		}
	}

	public static int getCompressionTypeValue(String type) {
		if (LZ4.equals(type)) {
			return COMPRESSION_TYPE_LZ4;
		} else if (SNAPPY.equals(type)) {
			return COMPRESSION_TYPE_SNAPPY;
		} else if (GZ.equals(type)) {
			return COMPRESSION_TYPE_GZIP;
		} else {
			return COMPRESSION_TYPE_NONE;
		}
	}

	public static String filterCompressionType(String type) {
		if (LZ4.equals(type)) {
			return LZ4;
		} else if (SNAPPY.equals(type)) {
			return SNAPPY;
		} else if (GZ.equals(type)) {
			return GZ;
		} else {
			return NONE;
		}
	}

	public static String filterEncryptionType(String type) {
		if (AES256.equals(type)) {
			return AES256;
		} else {
			return NONE;
		}
	}

	private Cipher cipher;

	private static Map<String, SecretKey> secretKeyMap = new HashMap<String, SecretKey>();

	boolean encrypt = false;

	public CustomEncryptionHandler(int mode, boolean encrypt, String encryptionKey) throws Exception {
		this.encrypt = encrypt;
		if (encrypt) {
			encryptionKey = SecurityHandler.decryptData(encryptionKey);

			if (!secretKeyMap.containsKey(encryptionKey)) {
				SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRETKEY_ALGO);
				KeySpec spec = new PBEKeySpec(encryptionKey.toCharArray(), salt, ITERATION_COUNT, KEY_LEN);
				SecretKey tmp = factory.generateSecret(spec);
				SecretKey secret = new SecretKeySpec(tmp.getEncoded(), SECRETKEYSPEC_ALGO);

				secretKeyMap.put(encryptionKey, secret);
			}

			cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(mode, secretKeyMap.get(encryptionKey), new IvParameterSpec(INIT_VECTOR));
		}
	}

	public InputStream getCompressedCipherInputStream(InputStream inputStream, int compressionType) throws IOException {
		InputStream responseStream = inputStream;
		if (encrypt) {
			LOG.debug("Decryption enabled");
			responseStream = new CipherInputStream(responseStream, cipher);
		}

		if (compressionType == CustomEncryptionHandler.COMPRESSION_TYPE_GZIP) {
			LOG.debug("Decompressing data - GZ");
			responseStream = new GZIPInputStream(responseStream);
		} else if (compressionType == CustomEncryptionHandler.COMPRESSION_TYPE_SNAPPY) {
			LOG.debug("Decompressing data - SNAPPY");
			responseStream = new SnappyInputStream(responseStream);
		} else if (compressionType == CustomEncryptionHandler.COMPRESSION_TYPE_LZ4) {
			LOG.debug("Decompressing data - LZ4");
			responseStream = new LZ4JavaSafeInputStream(responseStream);
		}

		return responseStream;
	}

	public OutputStream getCompressedCipherOutputStream(OutputStream outputStream, int compressionType)
			throws IOException {
		OutputStream responseStream = outputStream;
		if (encrypt) {
			LOG.debug("Encryption enabled");
			responseStream = new CipherOutputStream(responseStream, cipher);
		}

		if (compressionType == CustomEncryptionHandler.COMPRESSION_TYPE_GZIP) {
			LOG.debug("Compressing data - GZ");
			responseStream = new GZIPOutputStream(responseStream);
		} else if (compressionType == CustomEncryptionHandler.COMPRESSION_TYPE_SNAPPY) {
			LOG.debug("Compressing data - SNAPPY");
			responseStream = new SnappyOutputStream(responseStream);
		} else if (compressionType == CustomEncryptionHandler.COMPRESSION_TYPE_LZ4) {
			LOG.debug("Compressing data - LZ4");
			responseStream = new LZ4JavaSafeOutputStream(responseStream);
		}

		return responseStream;
	}
}