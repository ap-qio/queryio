package com.queryio.common.util;

import java.io.IOException;
import java.io.Reader;

/**
 * Read text from a character-input stream, buffering characters so as to
 * provide for the efficient reading of characters, arrays, and lines.
 *
 * <p>
 * The buffer size may be specified, or the default size may be used. The
 * default is large enough for most purposes.
 *
 * <p>
 * In general, each read request made of a Reader causes a corresponding read
 * request to be made of the underlying character or byte stream. It is
 * therefore advisable to wrap a BufferedReader around any Reader whose read()
 * operations may be costly, such as FileReaders and InputStreamReaders. For
 * example,
 *
 * <pre>
 * BufferedReader in = new BufferedReader(new FileReader("foo.in"));
 * </pre>
 *
 * will buffer the input from the specified file. Without buffering, each
 * invocation of read() or readLine() could cause bytes to be read from the
 * file, converted into characters, and then returned, which can be very
 * inefficient.
 *
 * <p>
 * Programs that use DataInputStreams for textual input can be localized by
 * replacing each DataInputStream with an appropriate BufferedReader.
 *
 */

public class LineNumberReader {

	protected Object lock;
	private Reader in;

	private char cb[];
	private int nChars, nextChar;

	private static final int INVALIDATED = -2;
	private static final int UNMARKED = -1;
	private int markedChar = UNMARKED;
	private int readAheadLimit = 0; /* Valid only when markedChar > 0 */

	/** If the next character is a line feed, skip it */
	private boolean skipLF = false;

	private static int defaultCharBufferSize = 8192;
	private static int defaultExpectedLineLength = 80;

	/** The current line number */
	private int lineNumber = 0;
	private int currentLineNumber = 0;

	/**
	 * Create a buffering character-input stream that uses an input buffer of
	 * the specified size.
	 *
	 * @param in
	 *            A Reader
	 * @param sz
	 *            Input-buffer size
	 *
	 * @exception IllegalArgumentException
	 *                If sz is <= 0
	 */
	public LineNumberReader(Reader in, int sz) {
		this.lock = in;
		if (sz <= 0)
			throw new IllegalArgumentException("Buffer size <= 0");
		this.in = in;
		cb = new char[sz];
		nextChar = nChars = 0;
	}

	/**
	 * Create a buffering character-input stream that uses a default-sized input
	 * buffer.
	 *
	 * @param in
	 *            A Reader
	 */
	public LineNumberReader(Reader in) {
		this(in, defaultCharBufferSize);
	}

	/** Check to make sure that the stream has not been closed */
	private void ensureOpen() throws IOException {
		if (in == null)
			throw new IOException("Stream closed");
	}

	/**
	 * Fill the input buffer, taking the mark into account if it is valid.
	 */
	private void fill() throws IOException {
		int dst;
		if (markedChar <= UNMARKED) {
			/* No mark */
			dst = 0;
		} else {
			/* Marked */
			int delta = nextChar - markedChar;
			if (delta >= readAheadLimit) {
				/* Gone past read-ahead limit: Invalidate mark */
				markedChar = INVALIDATED;
				readAheadLimit = 0;
				dst = 0;
			} else {
				if (readAheadLimit <= cb.length) {
					/* Shuffle in the current buffer */
					System.arraycopy(cb, markedChar, cb, 0, delta);
					markedChar = 0;
					dst = delta;
				} else {
					/* Reallocate buffer to accomodate read-ahead limit */
					char ncb[] = new char[readAheadLimit];
					System.arraycopy(cb, markedChar, ncb, 0, delta);
					cb = ncb;
					markedChar = 0;
					dst = delta;
				}
				nextChar = nChars = delta;
			}
		}

		int n;
		do {
			n = in.read(cb, dst, cb.length - dst);
		} while (n == 0);
		if (n > 0) {
			nChars = dst + n;
			nextChar = dst;
		}
	}

	/**
	 * Read a line of text. A line is considered to be terminated by any one of
	 * a line feed ('\n'), a carriage return ('\r'), or a carriage return
	 * followed immediately by a linefeed.
	 *
	 * @param ignoreLF
	 *            If true, the next '\n' will be skipped
	 *
	 * @return A String containing the contents of the line, not including any
	 *         line-termination characters, or null if the end of the stream has
	 *         been reached
	 * 
	 * @see java.io.LineNumberReader#readLine()
	 *
	 * @exception IOException
	 *                If an I/O error occurs
	 */
	String readLine(boolean ignoreLF) throws IOException {
		StringBuffer s = null;
		int startChar;
		boolean omitLF = ignoreLF || skipLF;

		synchronized (lock) {
			ensureOpen();

			// bufferLoop:
			for (;;) {

				if (nextChar >= nChars)
					fill();
				if (nextChar >= nChars) { /* EOF */
					if (s != null && s.length() > 0) {
						currentLineNumber = lineNumber + 1;
						return s.toString();
					} else
						return null;
				}
				boolean eol = false;
				char c = 0;
				int i;

				/* Skip a leftover '\n', if necessary */
				if (omitLF && (cb[nextChar] == '\n'))
					nextChar++;
				skipLF = false;
				omitLF = false;

				charLoop: for (i = nextChar; i < nChars; i++) {
					c = cb[i];
					if ((c == '\n') || (c == '\r')) {
						eol = true;
						break charLoop;
					}
				}

				startChar = nextChar;
				nextChar = i;

				if (eol) {
					String str;
					if (s == null) {
						str = new String(cb, startChar, i - startChar);
					} else {
						s.append(cb, startChar, i - startChar);
						str = s.toString();
					}
					nextChar++;
					if (c == '\r') {
						skipLF = true;
					}
					lineNumber++;
					currentLineNumber = lineNumber;
					return str;
				}

				if (s == null)
					s = new StringBuffer(defaultExpectedLineLength);
				s.append(cb, startChar, i - startChar);
			}
		}
	}

	/**
	 * Read a line of text. A line is considered to be terminated by any one of
	 * a line feed ('\n'), a carriage return ('\r'), or a carriage return
	 * followed immediately by a linefeed.
	 *
	 * @return A String containing the contents of the line, not including any
	 *         line-termination characters, or null if the end of the stream has
	 *         been reached
	 *
	 * @exception IOException
	 *                If an I/O error occurs
	 */
	public String readLine() throws IOException {
		return readLine(false);
	}

	/**
	 * Tell whether this stream is ready to be read. A buffered character stream
	 * is ready if the buffer is not empty, or if the underlying character
	 * stream is ready.
	 *
	 * @exception IOException
	 *                If an I/O error occurs
	 */
	public boolean ready() throws IOException {
		synchronized (lock) {
			ensureOpen();

			/*
			 * If newline needs to be skipped and the next char to be read is a
			 * newline character, then just skip it right away.
			 */
			if (skipLF) {
				/*
				 * Note that in.ready() will return true if and only if the next
				 * read on the stream will not block.
				 */
				if (nextChar >= nChars && in.ready()) {
					fill();
				}
				if (nextChar < nChars) {
					if (cb[nextChar] == '\n')
						nextChar++;
					skipLF = false;
				}
			}
			return (nextChar < nChars) || in.ready();
		}
	}

	/**
	 * Close the stream.
	 *
	 * @exception IOException
	 *                If an I/O error occurs
	 */
	public void close() throws IOException {
		synchronized (lock) {
			if (in == null)
				return;
			in.close();
			in = null;
			cb = null;
		}
	}

	public int getLineNumber() {
		return currentLineNumber;
	}

	public int getFinishedLineNumber() {
		return lineNumber;
	}
}
