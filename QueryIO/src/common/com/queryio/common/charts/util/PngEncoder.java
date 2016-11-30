/*
 * @(#)  PNGEncoder.java
 *
 * Copyright (C) 2002 Exceed Consultancy Services. All Rights Reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.charts.util;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * PngEncoder takes a Java Image object and creates a byte string which can be
 * saved as a PNG file. The Image is presumed to use the DirectColorModel.
 * 
 * @author Exceed Consultancy Services
 */
public class PngEncoder {

	/** Constant specifying that alpha channel should be encoded. */
	public static final boolean ENCODE_ALPHA = true;

	/** Constant specifying that alpha channel should not be encoded. */
	public static final boolean NO_ALPHA = false;

	/** Constants for filter (NONE) */
	public static final int FILTER_NONE = 0;

	/** Constants for filter (SUB) */
	public static final int FILTER_SUB = 1;

	/** Constants for filter (UP) */
	public static final int FILTER_UP = 2;

	/** Constants for filter (LAST) */
	public static final int FILTER_LAST = 2;

	/** IHDR tag. */
	private static final byte IHDR[] = { 73, 72, 68, 82 };

	/** IDAT tag. */
	private static final byte IDAT[] = { 73, 68, 65, 84 };

	/** IEND tag. */
	private static final byte IEND[] = { 73, 69, 78, 68 };

	/** The png bytes. */
	private byte[] pngBytes;

	/** The prior row. */
	private byte[] priorRow;

	/** The left bytes. */
	private byte[] leftBytes;

	/** If the reference of pngBytes was changed? */
	private boolean modified = false;

	/** The width. */
	private int width, height;

	/** The byte position. */
	private int bytePos, maxPos;

	/** CRC. */
	private final CRC32 crc = new CRC32();

	/** The CRC value. */
	private long crcValue;

	/** Encode alpha? */
	private boolean encodeAlpha;

	/** The filter type. */
	private int filter;

	/** The bytes-per-pixel. */
	private int bytesPerPixel;

	/** The compression level. */
	private int compressionLevel;

	/**
	 * Class constructor
	 * 
	 */
	public PngEncoder() {
		this(false, FILTER_NONE, 0);
	}

	/**
	 * Class constructor specifying whether to encode alpha.
	 * 
	 * @param encodeAlpha
	 *            Encode the alpha channel? false=no; true=yes
	 */
	public PngEncoder(final boolean encodeAlpha) {
		this(encodeAlpha, FILTER_NONE, 0);
	}

	/**
	 * Class constructor specifying whether to encode alpha, and filter to use.
	 * 
	 * @param encodeAlpha
	 *            Encode the alpha channel? false=no; true=yes
	 * @param whichFilter
	 *            0=none, 1=sub, 2=up
	 */
	public PngEncoder(final boolean encodeAlpha, final int whichFilter) {
		this(encodeAlpha, whichFilter, 0);
	}

	/**
	 * Class constructor specifying whether to encode alpha, filter to use, and
	 * compression level.
	 * 
	 * @param encodeAlpha
	 *            Encode the alpha channel? false=no; true=yes
	 * @param whichFilter
	 *            0=none, 1=sub, 2=up
	 * @param compLevel
	 *            0..9
	 */
	public PngEncoder(final boolean encodeAlpha, final int whichFilter, final int compLevel) {
		this.encodeAlpha = encodeAlpha;
		this.setFilter(whichFilter);
		if ((compLevel >= 0) && (compLevel <= 9)) {
			this.compressionLevel = compLevel;
		}
	}

	/**
	 * Creates an array of bytes that is the PNG equivalent of the current
	 * image, specifying whether to encode alpha or not.
	 * 
	 * @param image
	 *            A Java Image object which uses the DirectColorModel
	 * @param encodeAlpha
	 *            boolean false=no alpha, true=encode alpha
	 * @return an array of bytes, or null if there was a problem
	 */
	public byte[] pngEncode(final Image image, final boolean encodeAlpha, final byte[] bytes) {
		final byte[] pngIdBytes = { -119, 80, 78, 71, 13, 10, 26, 10 };

		if (image == null) {
			return null;
		}
		this.width = image.getWidth(null);
		this.height = image.getHeight(null);

		/*
		 * start with an array that is big enough to hold all the pixels (plus
		 * filter bytes), and an extra 200 bytes for header info
		 */
		if (bytes == null) {
			this.pngBytes = new byte[((this.width + 1) * this.height * 3) + 200];
			this.modified = true;
		} else {
			this.pngBytes = bytes;
		}

		/*
		 * keep track of largest byte written to the array
		 */
		this.maxPos = 0;

		this.bytePos = this.writeBytes(pngIdBytes, 0);

		this.width = image.getWidth(null);
		this.height = image.getHeight(null);

		this.writeHeader();
		if (this.writeImageData(image)) {
			this.writeEnd();
			this.pngBytes = this.resizeByteArray(this.pngBytes, this.maxPos);
		} else {
			this.pngBytes = null;
			this.modified = true;
		}
		return this.pngBytes;
	}

	/**
	 * Creates an array of bytes that is the PNG equivalent of the current
	 * image. Alpha encoding is determined by its setting in the constructor.
	 * 
	 * @param image
	 *            A Java Image object which uses the DirectColorModel
	 * @return an array of bytes, or null if there was a problem
	 */
	public byte[] pngEncode(final Image image, final byte[] pngBytes) {
		return this.pngEncode(image, this.encodeAlpha, pngBytes);
	}

	/**
	 * Set the alpha encoding on or off.
	 * 
	 * @param encodeAlpha
	 *            false=no, true=yes
	 */
	public void setEncodeAlpha(final boolean encodeAlpha) {
		this.encodeAlpha = encodeAlpha;
	}

	/**
	 * Retrieve alpha encoding status.
	 * 
	 * @return boolean false=no, true=yes
	 */
	public final boolean getEncodeAlpha() {
		return this.encodeAlpha;
	}

	/**
	 * Set the filter to use
	 * 
	 * @param whichFilter
	 *            from constant list
	 */
	public void setFilter(final int whichFilter) {
		this.filter = FILTER_NONE;
		if (whichFilter <= FILTER_LAST) {
			this.filter = whichFilter;
		}
	}

	/**
	 * Retrieve filtering scheme
	 * 
	 * @return int (see constant list)
	 */
	public final int getFilter() {
		return this.filter;
	}

	/**
	 * Set the compression level to use
	 * 
	 * @param level
	 *            0 through 9
	 */
	public void setCompressionLevel(final int level) {
		if ((level >= 0) && (level <= 9)) {
			this.compressionLevel = level;
		}
	}

	/**
	 * Retrieve compression level
	 * 
	 * @return int in range 0-9
	 */
	public final int getCompressionLevel() {
		return this.compressionLevel;
	}

	/**
	 * Increase or decrease the length of a byte array.
	 * 
	 * @param array
	 *            The original array.
	 * @param newLength
	 *            The length you wish the new array to have.
	 * @return Array of newly desired length. If shorter than the original, the
	 *         trailing elements are truncated.
	 */
	protected byte[] resizeByteArray(final byte[] array, final int newLength) {
		final int oldLength = array.length;
		byte[] newArray = null;
		if (oldLength < newLength) {
			newArray = new byte[newLength];
			System.arraycopy(array, 0, newArray, 0, Math.min(oldLength, newLength));
			this.modified = true;
		} else {
			newArray = array;
		}
		return newArray;
	}

	/**
	 * Write an array of bytes into the pngBytes array. Note: This routine has
	 * the side effect of updating maxPos, the largest element written in the
	 * array. The array is resized by 1000 bytes or the length of the data to be
	 * written, whichever is larger.
	 * 
	 * @param data
	 *            The data to be written into pngBytes.
	 * @param offset
	 *            The starting point to write to.
	 * @return The next place to be written to in the pngBytes array.
	 */
	protected int writeBytes(final byte[] data, final int offset) {
		this.maxPos = Math.max(this.maxPos, offset + data.length);
		if (data.length + offset > this.pngBytes.length) {
			this.pngBytes = this.resizeByteArray(this.pngBytes, this.pngBytes.length + Math.max(1000, data.length));
		}
		System.arraycopy(data, 0, this.pngBytes, offset, data.length);
		return offset + data.length;
	}

	/**
	 * Write an array of bytes into the pngBytes array, specifying number of
	 * bytes to write. Note: This routine has the side effect of updating
	 * maxPos, the largest element written in the array. The array is resized by
	 * 1000 bytes or the length of the data to be written, whichever is larger.
	 * 
	 * @param data
	 *            The data to be written into pngBytes.
	 * @param nBytes
	 *            The number of bytes to be written.
	 * @param offset
	 *            The starting point to write to.
	 * @return The next place to be written to in the pngBytes array.
	 */
	protected int writeBytes(final byte[] data, final int nBytes, final int offset) {
		this.maxPos = Math.max(this.maxPos, offset + nBytes);
		if (nBytes + offset > this.pngBytes.length) {
			this.pngBytes = this.resizeByteArray(this.pngBytes, this.pngBytes.length + Math.max(1000, nBytes));
		}
		System.arraycopy(data, 0, this.pngBytes, offset, nBytes);
		return offset + nBytes;
	}

	/**
	 * Write a two-byte integer into the pngBytes array at a given position.
	 * 
	 * @param n
	 *            The integer to be written into pngBytes.
	 * @param offset
	 *            The starting point to write to.
	 * @return The next place to be written to in the pngBytes array.
	 */
	protected int writeInt2(final int n, final int offset) {
		final byte[] temp = { (byte) ((n >> 8) & 0xff), (byte) (n & 0xff) };
		return this.writeBytes(temp, offset);
	}

	/**
	 * Write a four-byte integer into the pngBytes array at a given position.
	 * 
	 * @param n
	 *            The integer to be written into pngBytes.
	 * @param offset
	 *            The starting point to write to.
	 * @return The next place to be written to in the pngBytes array.
	 */
	protected int writeInt4(final int n, final int offset) {
		final byte[] temp = { (byte) ((n >> 24) & 0xff), (byte) ((n >> 16) & 0xff), (byte) ((n >> 8) & 0xff),
				(byte) (n & 0xff) };
		return this.writeBytes(temp, offset);
	}

	/**
	 * Write a single byte into the pngBytes array at a given position.
	 * 
	 * @param b
	 *            The integer to be written into pngBytes.
	 * @param offset
	 *            The starting point to write to.
	 * @return The next place to be written to in the pngBytes array.
	 */
	protected int writeByte(final int b, final int offset) {
		final byte[] temp = { (byte) b };
		return this.writeBytes(temp, offset);
	}

	/**
	 * Write a PNG "IHDR" chunk into the pngBytes array.
	 */
	protected void writeHeader() {
		int startPos;

		startPos = this.bytePos = this.writeInt4(13, this.bytePos);
		this.bytePos = this.writeBytes(IHDR, this.bytePos);
		this.bytePos = this.writeInt4(this.width, this.bytePos);
		this.bytePos = this.writeInt4(this.height, this.bytePos);
		this.bytePos = this.writeByte(8, this.bytePos); // bit depth
		this.bytePos = this.writeByte((this.encodeAlpha) ? 6 : 2, this.bytePos); // direct
		// model
		this.bytePos = this.writeByte(0, this.bytePos); // compression method
		this.bytePos = this.writeByte(0, this.bytePos); // filter method
		this.bytePos = this.writeByte(0, this.bytePos); // no interlace
		this.crc.reset();
		this.crc.update(this.pngBytes, startPos, this.bytePos - startPos);
		this.crcValue = this.crc.getValue();
		this.bytePos = this.writeInt4((int) this.crcValue, this.bytePos);
	}

	/**
	 * Perform "sub" filtering on the given row. Uses temporary array leftBytes
	 * to store the original values of the previous pixels. The array is 16
	 * bytes long, which will easily hold two-byte samples plus two-byte alpha.
	 * 
	 * @param pixels
	 *            The array holding the scan lines being built
	 * @param startPos
	 *            Starting position within pixels of bytes to be filtered.
	 * @param width
	 *            Width of a scanline in pixels.
	 */
	protected void filterSub(final byte[] pixels, final int startPos, final int width) {
		int i;
		final int offset = this.bytesPerPixel;
		final int actualStart = startPos + offset;
		final int nBytes = width * this.bytesPerPixel;
		int leftInsert = offset;
		int leftExtract = 0;

		for (i = actualStart; i < startPos + nBytes; i++) {
			this.leftBytes[leftInsert] = pixels[i]; // $IGN_Use_System_arrayCopy$
			pixels[i] = (byte) ((pixels[i] - this.leftBytes[leftExtract]) % 256);
			leftInsert = (leftInsert + 1) % 0x0f;
			leftExtract = (leftExtract + 1) % 0x0f;
		}
	}

	/**
	 * Perform "up" filtering on the given row. Side effect: refills the prior
	 * row with current row
	 * 
	 * @param pixels
	 *            The array holding the scan lines being built
	 * @param startPos
	 *            Starting position within pixels of bytes to be filtered.
	 * @param width
	 *            Width of a scanline in pixels.
	 */
	protected void filterUp(final byte[] pixels, final int startPos, final int width) {
		int i, nBytes;
		byte currentByte;

		nBytes = width * this.bytesPerPixel;

		for (i = 0; i < nBytes; i++) {
			currentByte = pixels[startPos + i];
			pixels[startPos + i] = (byte) ((pixels[startPos + i] - this.priorRow[i]) % 256);
			this.priorRow[i] = currentByte;
		}
	}

	/**
	 * Write the image data into the pngBytes array. This will write one or more
	 * PNG "IDAT" chunks. In order to conserve memory, this method grabs as many
	 * rows as will fit into 32K bytes, or the whole image; whichever is less.
	 * 
	 * 
	 * @param image
	 *            A Java Image object which uses the DirectColorModel
	 * @return true if no errors; false if error grabbing pixels
	 */
	protected boolean writeImageData(final Image image) {
		int rowsLeft = this.height; // number of rows remaining to write
		int startRow = 0; // starting row to process this time through
		int nRows; // how many rows to grab at a time

		byte[] scanLines; // the scan lines to be compressed
		int scanPos; // where we are in the scan lines
		int startPos; // where this line's actual pixels start (used for
		// filtering)

		byte[] compressedLines; // the resultant compressed lines
		int nCompressed; // how big is the compressed area?

		// int depth; // color depth ( handle only 8 or 32 )

		PixelGrabber pg;

		this.bytesPerPixel = (this.encodeAlpha) ? 4 : 3;

		final Deflater scrunch = new Deflater(this.compressionLevel);
		final ByteArrayOutputStream outBytes = new ByteArrayOutputStream(1024); // $IGN_Close_streams$

		final DeflaterOutputStream compBytes = new DeflaterOutputStream(new BufferedOutputStream(outBytes), scrunch); // $IGN_Close_streams$
		try {
			while (rowsLeft > 0) {
				nRows = Math.min(32767 / (this.width * (this.bytesPerPixel + 1)), rowsLeft);
				// nRows = rowsLeft;

				final int[] pixels = new int[this.width * nRows];

				pg = new PixelGrabber(image, 0, startRow, this.width, nRows, pixels, 0, this.width);
				try {
					pg.grabPixels();
				} catch (final Exception e) {
					System.err.println("interrupted waiting for pixels!"); //$NON-NLS-1$
					return false;
				}
				if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
					System.err.println("image fetch aborted or errored"); //$NON-NLS-1$
					return false;
				}

				/*
				 * Create a data chunk. scanLines adds "nRows" for the filter
				 * bytes.
				 */
				scanLines = new byte[this.width * nRows * this.bytesPerPixel + nRows];

				if (this.filter == FILTER_SUB) {
					this.leftBytes = new byte[16];
				}
				if (this.filter == FILTER_UP) {
					this.priorRow = new byte[this.width * this.bytesPerPixel];
				}

				scanPos = 0;
				startPos = 1;
				for (int i = 0; i < this.width * nRows; i++) {
					if (i % this.width == 0) {
						scanLines[scanPos++] = (byte) this.filter;
						startPos = scanPos;
					}
					scanLines[scanPos++] = (byte) ((pixels[i] >> 16) & 0xff);
					scanLines[scanPos++] = (byte) ((pixels[i] >> 8) & 0xff);
					scanLines[scanPos++] = (byte) ((pixels[i]) & 0xff);
					if (this.encodeAlpha) {
						scanLines[scanPos++] = (byte) ((pixels[i] >> 24) & 0xff);
					}
					if ((i % this.width == this.width - 1) && (this.filter != FILTER_NONE)) {
						if (this.filter == FILTER_SUB) {
							this.filterSub(scanLines, startPos, this.width);
						}
						if (this.filter == FILTER_UP) {
							this.filterUp(scanLines, startPos, this.width);
						}
					}
				}

				/*
				 * Write these lines to the output area
				 */
				compBytes.write(scanLines, 0, scanPos);

				startRow += nRows;
				rowsLeft -= nRows;
			}
			compBytes.close();

			/*
			 * Write the compressed bytes
			 */
			compressedLines = outBytes.toByteArray();
			nCompressed = compressedLines.length;

			this.crc.reset();
			this.bytePos = this.writeInt4(nCompressed, this.bytePos);
			this.bytePos = this.writeBytes(IDAT, this.bytePos);
			this.crc.update(IDAT);
			this.bytePos = this.writeBytes(compressedLines, nCompressed, this.bytePos);
			this.crc.update(compressedLines, 0, nCompressed);

			this.crcValue = this.crc.getValue();
			this.bytePos = this.writeInt4((int) this.crcValue, this.bytePos);
			scrunch.finish();
			return true;
		} catch (final IOException e) {
			System.err.println(e.toString());
			return false;
		}
	}

	/**
	 * Write a PNG "IEND" chunk into the pngBytes array.
	 */
	protected void writeEnd() {
		this.bytePos = this.writeInt4(0, this.bytePos);
		this.bytePos = this.writeBytes(IEND, this.bytePos);
		this.crc.reset();
		this.crc.update(IEND);
		this.crcValue = this.crc.getValue();
		this.bytePos = this.writeInt4((int) this.crcValue, this.bytePos);
	}

	/**
	 * returns if the reference of the bytes was modified method isModified
	 * 
	 * @return
	 */
	public final boolean isModified() {
		return this.modified;
	}

	public final int getLastPosition() {
		return this.maxPos;
	}

}
