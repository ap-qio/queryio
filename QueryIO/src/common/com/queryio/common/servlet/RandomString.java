package com.queryio.common.servlet;

import java.util.Random;

public class RandomString {

	private static final char[] SYMBOLS = new char[62];
	private static final char[] DIGITS = new char[10];

	static {
		for (int idx = 0; idx < 10; ++idx)
			DIGITS[idx] = (char) ('0' + idx);
		for (int idx = 0; idx < 26; ++idx)
			SYMBOLS[idx] = (char) ('a' + idx);
		for (int idx = 26; idx < 52; ++idx)
			SYMBOLS[idx] = (char) ('A' + idx - 26);
	}

	private final Random random = new Random();

	private final char[] buf;

	public RandomString(int length) {
		if (length < 1)
			length = 8;
		buf = new char[length];
	}

	public String nextString() {
		for (int idx = 0; idx < buf.length - 1; ++idx) {
			if (idx % 2 == 0)
				buf[idx] = SYMBOLS[26 + random.nextInt(26)];
			else
				buf[idx] = SYMBOLS[random.nextInt(26)];
		}
		buf[buf.length - 1] = DIGITS[random.nextInt(DIGITS.length)];
		return new String(buf);
	}

	// public static void main(String[] args) {
	//
	// RandomString rs = new RandomString(10);
	// for (int i = 0; i < 10; i++) {
	// System.out.println(rs.nextString());
	// }
	// }
}