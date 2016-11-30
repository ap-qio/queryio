package com.os3.server.common;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class TokenGenerator {
	private static SecureRandom random = new SecureRandom();

	// private static String ALGORITHM_SHA1PRNG = "SHA1PRNG";

	public static String generateToken() throws NoSuchAlgorithmException {

		// SecureRandom sr = SecureRandom.getInstance(ALGORITHM_SHA1PRNG);

		return new BigInteger(320, random).toString(32);
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(generateToken());
	}
}
