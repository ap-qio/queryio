package com.os3.server.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class OS3CheckSum {

	private Checksum cksum;
	private MessageDigest mdigest;
	private StringBuilder result;
	// Hexadecimal digits
	final static char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * Creates a message digest with the specified algorithm name.
	 *
	 * @param algorithm
	 *            the standard name of the digest algorithm.
	 *
	 */

	public OS3CheckSum(String algorithm) throws NoSuchAlgorithmException {
		if (algorithm.equals("CRC32")) {
			this.cksum = new CRC32();
		} else if (algorithm.equals("Adler32")) {
			this.cksum = new Adler32();
		} else {
			this.mdigest = MessageDigest.getInstance(algorithm);
		}
	}

	/**
	 * Updates the current checksum or digest with the specified array of bytes.
	 *
	 * @param input
	 *            the byte array to update the checksum or digest with
	 * @param offset
	 *            the start offset of the data
	 * @param len
	 *            the number of bytes to use for the update
	 */
	public void update(byte[] input, int offset, int len) {
		if (cksum != null) {
			cksum.update(input, offset, len);
		} else {
			mdigest.update(input, offset, len);
		}
	}

	/**
	 * Updates the checksum or digest using the specified array of bytes.
	 *
	 * @param input
	 *            the array of bytes.
	 */
	public void update(byte[] input) {
		if (cksum != null) {
			cksum.update(input, 0, input.length);
		} else {
			mdigest.update(input);
		}
	}

	/**
	 * Returns the current checksum or digest value.
	 *
	 * @return the current checksum or digest value
	 */
	public String getValue() {
		if (cksum != null) {
			String value = Long.toString(cksum.getValue());
			reset(); // Resetting here for consistency as MessageDigest resets
			return value;
		} else {
			byte[] value = mdigest.digest();
			if (result == null) {
				result = new StringBuilder();
			} else {
				result.setLength(0);
			}
			for (int i = 0; i < value.length; i++) {
				result.append(HEX_DIGITS[(value[i] & 0xf0) >>> 4]);
				result.append(HEX_DIGITS[(value[i] & 0x0f)]);
			}
			return result.toString();
		}
	}

	/**
	 * Resets the checksum or digest to its initial value.
	 */
	public void reset() {
		if (cksum != null) {
			cksum.reset();
		} else {
			mdigest.reset();
		}
	}

	/**
	 * @param algorithm
	 *            which will validated by the present time object
	 * @return true if current object using the same algorithm else false.
	 */
	public boolean isValid(String algorithm) {
		if (this.mdigest != null) {
			return this.mdigest.getAlgorithm().equals(algorithm);
		} else if (this.cksum != null) {
			return this.cksum.getClass().getName().equalsIgnoreCase(algorithm);
		}
		return false;
	}
}
