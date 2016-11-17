package com.queryio.plugin.datatags;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

public class ExecuteTagParser extends Thread{
	private static final Log LOG = LogFactory.getLog(ExecuteTagParser.class);
	private IDataTagParser tagParser;
	private String src = null;
	private PipedOutputStream pos;
	private InputStream is = null;
	private boolean done = false;
	
	public ExecuteTagParser(String src, IDataTagParser parser) throws IOException{
		this.src = src;
		this.tagParser = parser;
		
		PipedInputStream pis = new PipedInputStream(8192);
		pos = new PipedOutputStream(pis);
		is = pis;
	}
	
	@Override
	public void run(){
		try{
			LOG.info("Parsing for metadata using " + tagParser.getClass() + ".");
			tagParser.parseStream(is, UserDefinedTagUtils.getFileExtension(src));
			
			done = true;
			LOG.info("Parsing done.");
		}catch(Throwable e){
			LOG.fatal(e.getLocalizedMessage(), e);
			e.printStackTrace();
			pos = null;
		}finally{
			if(is != null){	
				try{
					is.close();
				}catch(Exception e){
					LOG.warn(e.getLocalizedMessage(), e);
				}
			}
		}
	}
	
	public void closePos() throws IOException{
		if(pos != null){			
			pos.close();
		}
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		if(!done && pos != null){
			try{
				pos.write(b, off, len);
			}catch(IOException e){
				if(!done){
					throw e;
				}
			}finally{
				if(pos!= null)
					pos.flush();
			}
		}		
	}
	public void write(int b) throws IOException {
		if(!done && pos != null){
			try{
				pos.write(b);
				pos.flush();
			}catch(IOException e){
				if(!done){
					throw e;
				}
			}			
		}
	}
	public void write(byte[] b) throws IOException {
		if(!done && pos != null){
			try{
				pos.write(b);
				pos.flush();
			}catch(IOException e){
				if(!done){
					throw e;
				}
			}
		}
	}
}