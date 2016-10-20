package com.os3.server.common;

import java.io.ByteArrayInputStream;

public class ByteArrayInputStreamExt extends ByteArrayInputStream {

	/**
	 * @param buf
	 */
	public ByteArrayInputStreamExt(byte[] buf) {
		super(buf);
	}

	/**
	 * @param buf
	 * @param offset
	 * @param length
	 */
	public ByteArrayInputStreamExt(byte[] buf, int offset, int length) {
		super(buf, offset, length);
	}

	/**
	 * Returns internal buffer of ByteArrayInputStream for reuse.
	 * {@link ByteArrayInputStreamExt#reset(int)} should be called after buffer is populated with new data to reset position and set new length.
	 * @return
	 */
	public byte[] getBuffer() {
		return buf;
	}
	
	/**
	 * Reset ByteArrayInputStream buffer for reuse.
	 * @param length
	 */
	public void reset(int length) {
		pos = 0;
		count = length;
	}
}
