package com.queryio.common.servlet;

import java.util.Random;

public class RandomString {

	private static final char[] symbols = new char[62];
	private static final char[] digits = new char[10];

	static {
		for (int idx = 0; idx < 10; ++idx)
			digits[idx] = (char) ('0' + idx);
		for (int idx = 0; idx < 26; ++idx)
			symbols[idx] = (char) ('a' + idx);
		for (int idx = 26; idx < 52; ++idx)
			symbols[idx] = (char) ('A' + idx - 26);
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
				buf[idx] = symbols[26 + random.nextInt(26)];
			else
				buf[idx] = symbols[random.nextInt(26)];
		}
		buf[buf.length - 1] = digits[random.nextInt(digits.length)];
		return new String(buf);
	}

//	public static void main(String[] args) {
//
//		RandomString rs = new RandomString(10);
//		for (int i = 0; i < 10; i++) {
//			System.out.println(rs.nextString());
//		}
//	}
}