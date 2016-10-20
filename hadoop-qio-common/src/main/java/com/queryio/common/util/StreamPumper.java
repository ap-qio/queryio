package com.queryio.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;


public class StreamPumper extends Thread
{
	private final Reader reader;
	private boolean endOfStream = false;
	static int id = 0;
//	private final int SLEEP_TIME = 100;
	private Writer writer;
	public static final int BUFFER_SIZE = 512;
	private boolean isProcessCompleted;

	// if given output stream is System.out or System.err then we should not
	// close writer only flushing will do. If we close standard out or err
	// streams then further witting to these streams will not be possible
	private boolean shouldCloseWriter = true;

	public StreamPumper(final Reader reader, final Writer writer)
	{
		this.reader = reader;
		this.writer = writer;
		if (this.writer == null)
		{
			this.writer = new BufferedWriter(new OutputStreamWriter(new OutputStreamExt()));// $IGN_Close_streams$
		}
	}

	public StreamPumper(final Reader reader, OutputStream out)
	{
		this.reader = reader;
		if (out == null)
		{
			out = new OutputStreamExt(); // $IGN_Close_streams$
		}
		// if given output stream is System.out or System.err then we should not
		// close writer only flushing will do. If we close standard out or err
		// streams then further witting to these streams will not be possible
		this.shouldCloseWriter = (out != System.out) && (out != System.err);
		this.writer = new BufferedWriter(new OutputStreamWriter(out));
	}

	/**
	 * @param is
	 * @param out
	 */
	public StreamPumper(final InputStream is, final OutputStream out)
	{
		this(new BufferedReader(new InputStreamReader(is)), out);
	}

	public void pumpStream() throws Exception
	{
		final char[] buf = new char[BUFFER_SIZE];
		if (!this.endOfStream)
		{
			final int bytesRead = this.reader.read(buf, 0, BUFFER_SIZE);
			if (bytesRead > 0)
			{
				this.writer.write(buf, 0, bytesRead);
				this.writer.flush();
			}
			else if (bytesRead == -1)
			{
				this.endOfStream = true;
			}
		}
	}

	public void run()
	{
		this.isProcessCompleted = false;
		try
		{
			while (!this.endOfStream)
			{
				this.pumpStream();
				if (!this.endOfStream)
				{
//					sleep(this.SLEEP_TIME);
				}
			}
		}
		catch (final InterruptedException ie)
		{
			// Do Nothing;
		}
		catch (final IOException ioe)
		{
			AppLogger.getLogger().fatal(ioe.getMessage(),ioe);
		}
		catch (final Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(),e);
		}
		finally
		{
			try
			{
				this.writer.flush();
				this.reader.close();
				if (this.shouldCloseWriter)
				{
					this.writer.close();
				}
				this.isProcessCompleted = true;
			}
			catch (final Exception ex)
			{
				// DO NOTHING
			}
		}
	}

	private static class OutputStreamExt extends OutputStream
	{
		public void write(final int b)
		{
			// do nothing
		}

		public void write(final byte b[], final int off, final int len)
		{
			// do nothing
		}
	}

	public final boolean isProcessCompleted()
	{
		return this.isProcessCompleted;
	}

}
