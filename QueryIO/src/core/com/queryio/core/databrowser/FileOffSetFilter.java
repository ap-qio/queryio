package com.queryio.core.databrowser;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

public class FileOffSetFilter implements PathFilter {

	private int offSet;
	private int length;
	private int count ;
	
	public int getCount() {
		return this.count;
	}


	public void setCount(int count) {
		this.count = count;
	}


	
	public FileOffSetFilter(int offSet,int length) {
		super();
		this.offSet = offSet;
		this.length =length;
	
	
	}


	
	public boolean accept(Path path) {
		
		if(path.toUri().getPath().equals("/tmp")){
			return false;
		}	
		
		this.count++;	// To exclude '/tmp' directory to be included in File count in Data Browser. 
		
		if(this.count > length){
			return false;
		}
		if(this.count >= this.offSet && this.count <= this.length){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	

}
