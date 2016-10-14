package com.queryio.demo.job;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hdfs.DFSInputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.LocatedFileStatusFetcher;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.InvalidInputException;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;
import org.apache.hadoop.mapreduce.security.TokenCache;
import org.apache.hadoop.util.StopWatch;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.demo.common.CustomQIODFSInputStream;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

public class CustomRecordInputFormat extends
		FileInputFormat<LongWritable, Text> {

	public static final String COL_COMPRESSION_TYPE = "COMPRESSION_TYPE";
	public static final String COL_ENCRYPTION_TYPE = "ENCRYPTION_TYPE";
	final static String QUERY_START = "SELECT " + COL_COMPRESSION_TYPE + "," + COL_ENCRYPTION_TYPE + " FROM ";
	final static String QUERY_END = " WHERE FILEPATH=?";
	private static final Log LOG = LogFactory.getLog(CustomRecordInputFormat.class);
	
	private static JobContext context = null; 

	
	/**
	 * Is the given filename splitable? Usually, true, but if the file is stream
	 * compressed, it will not be.
	 * 
	 * <code>FileInputFormat</code> implementations can override this and return
	 * <code>false</code> to ensure that individual input files are never
	 * split-up so that {@link Mapper}s process entire files.
	 * 
	 * @param context
	 *            the job context
	 * @param filename
	 *            the file name to check
	 * @return is this file splitable?
	 */
	protected boolean isSplitable(JobContext context, Path filename) {
		LOG.debug("isSplitable: yes : " + filename.toString());
		if (context == null)
			this.context = context;
		return true;
	}
	
	/** List input directories.
	   * Subclasses may override to, e.g., select only files matching a regular
	   * expression. 
	   * 
	   * @param job the job to list input paths for
	   * @return array of FileStatus objects
	   * @throws IOException if zero items.
	   */
	@Override
	protected List<FileStatus> listStatus(JobContext job) throws IOException {
	    Path[] dirs = getInputPaths(job);
	    if (dirs.length == 0) {
	      throw new IOException("No input paths specified in job");
	    }
	    
	    // get tokens for all the required FileSystems..
	    TokenCache.obtainTokensForNamenodes(job.getCredentials(), dirs, 
	                                        job.getConfiguration());

	    // Whether we need to recursive look into the directory structure
	    boolean recursive = getInputDirRecursive(job);

	    // creates a MultiPathFilter with the hiddenFileFilter and the
	    // user provided one (if any).
	    List<PathFilter> filters = new ArrayList<PathFilter>();
	    filters.add(CustomInputFormat.HIDDEN_FILE_FILTER);
	    PathFilter jobFilter = getInputPathFilter(job);
	    if (jobFilter != null) {
	      filters.add(jobFilter);
	    }
	    PathFilter inputFilter = new MultiPathFilter(filters);
	    
	    List<FileStatus> result = null;

	    int numThreads = job.getConfiguration().getInt(LIST_STATUS_NUM_THREADS,
	        DEFAULT_LIST_STATUS_NUM_THREADS);
	    StopWatch sw = new StopWatch().start();
	    if (numThreads == 1) {
	      result = singleThreadedListStatus(job, dirs, inputFilter, recursive);
	    } else {
	      Iterable<FileStatus> locatedFiles = null;
	      try {
	        LocatedFileStatusFetcher locatedFileStatusFetcher = new LocatedFileStatusFetcher(
	            job.getConfiguration(), dirs, recursive, inputFilter, true);
	        locatedFiles = locatedFileStatusFetcher.getFileStatuses();
	      } catch (InterruptedException e) {
	        throw new IOException("Interrupted while getting file statuses");
	      }
	      result = com.google.common.collect.Lists.newArrayList(locatedFiles);
	    }
	    
	    sw.stop();
	    if (LOG.isDebugEnabled()) {
	      LOG.debug("Time taken to get FileStatuses: "
	          + sw.now(TimeUnit.MILLISECONDS));
	    }
	    LOG.info("Total input paths to process : " + result.size()); 
	    return result;
	  }

	private List<FileStatus> singleThreadedListStatus(JobContext job,
			Path[] dirs, PathFilter inputFilter, boolean recursive)
			throws IOException {
		List<FileStatus> result = new ArrayList<FileStatus>();
		List<IOException> errors = new ArrayList<IOException>();
		for (int i = 0; i < dirs.length; ++i) {
			Path p = dirs[i];
			FileSystem fs = p.getFileSystem(job.getConfiguration());
			FileStatus[] matches = fs.globStatus(p, inputFilter);
			if (matches == null) {
				errors.add(new IOException("Input path does not exist: " + p));
			} else if (matches.length == 0) {
				errors.add(new IOException("Input Pattern " + p
						+ " matches 0 files"));
			} else {
				for (FileStatus globStat : matches) {
					if (globStat.isDirectory()) {
						RemoteIterator<LocatedFileStatus> iter = fs
								.listLocatedStatus(globStat.getPath());
						while (iter.hasNext()) {
							LocatedFileStatus stat = iter.next();
							if (inputFilter.accept(stat.getPath())) {
								if (recursive && stat.isDirectory()) {
									addInputPathRecursively(result, fs,
											stat.getPath(), inputFilter);
								} else {
									FileStatus checkIfCompressedEncrypted = checkIfCompressedEncrypted(fs, stat);
									if (checkIfCompressedEncrypted != null) {
										result.add(checkIfCompressedEncrypted);
									}
								}
							}
						}
					} else {
						result.add(globStat);
					}
				}
			}
		}

		if (!errors.isEmpty()) {
			throw new InvalidInputException(errors);
		}
		return result;
	}
	    
	
	/**
	 * Add files in the input path recursively into the results.
	 * 
	 * @param result
	 *            The List to store all files.
	 * @param fs
	 *            The FileSystem.
	 * @param path
	 *            The input path.
	 * @param inputFilter
	 *            The input filter that can be used to filter files/dirs.
	 * @throws IOException
	 */
	@Override
	protected void addInputPathRecursively(List<FileStatus> result,
			FileSystem fs, Path path, PathFilter inputFilter)
			throws IOException {
		RemoteIterator<LocatedFileStatus> iter = fs.listLocatedStatus(path);
		while (iter.hasNext()) {
			LocatedFileStatus stat = iter.next();
			if (inputFilter.accept(stat.getPath())) {
				if (stat.isDirectory()) {
					addInputPathRecursively(result, fs, stat.getPath(), inputFilter);
				} else {
					FileStatus checkIfCompressedEncrypted = checkIfCompressedEncrypted(fs, stat);
					if (checkIfCompressedEncrypted != null) {
						result.add(checkIfCompressedEncrypted);
					}
				}
			}
		}
	}

	private FileStatus checkIfCompressedEncrypted(FileSystem fs, LocatedFileStatus stat) {
		try {
			Map<String, String> metadata = null;
			metadata = getObjectMetadata(context.getConfiguration(), stat.getPath().toString(), TableConstants.TABLE_HDFS_METADATA);
			
			if(metadata == null){
				metadata = getObjectMetadata(context.getConfiguration(), stat.getPath().toString(), ("DATATAGS_" + UserDefinedTagUtils.getFileExtension(stat.getPath().toString())).toUpperCase());
			}
			
			if(metadata == null){
				metadata = getObjectMetadata(context.getConfiguration(), stat.getPath().toString(), null);
			}
			
			LOG.debug("filepath: " + stat.getPath() + " metadata : " + metadata);
			
			if (metadata == null) {
				return null;
			}
			
			int compressionType = getCompressionTypeValue(metadata.get(COL_COMPRESSION_TYPE));
			boolean encryptionType = getEncryptionTypeValue(metadata.get(COL_ENCRYPTION_TYPE));
			
			
			if (compressionType > 0 || encryptionType) {
				return codecWiseDecompress(fs, stat);
			} else {
				return stat;
			}
		} catch (Exception e ) {
			LOG.fatal(e.getMessage(), e);
		}
		return null;
	}
	
	private FileStatus codecWiseDecompress(FileSystem fs, LocatedFileStatus stat) throws Exception {

		Path path = stat.getPath();

		String outputUri = generateTempJOBPath(path.toString());
		
		Path dPath = new Path(outputUri);

		InputStream in = null;
		OutputStream out = null;
		DFSInputStream dfsInputStream = null;

		String filePath = path.toString();
		if(filePath.contains("://")) {
			filePath = filePath.substring(filePath.indexOf("://") + 3);
			filePath = filePath.substring(filePath.indexOf("/"));
		}
		
		try {
			DistributedFileSystem dfs = (DistributedFileSystem) fs;
			dfsInputStream = (DFSInputStream) dfs.getClient().open(filePath);
			
			in = new CustomQIODFSInputStream(dfsInputStream, context.getConfiguration(), filePath);
			out = fs.create(dPath);
			IOUtils.copyBytes(in, out, context.getConfiguration());
		} finally {
			IOUtils.closeStream(in);
			IOUtils.closeStream(out);
		}
		fs.deleteOnExit(getJOBTempPath(dPath.toString()));
		return fs.getFileStatus(dPath);
	}
	
	private static String generateTempJOBPath(String filePath) {
		String suffix = filePath;
		String prefix = "";
		if(suffix.contains("://")) {
			int index = filePath.indexOf("://") + 3;
			suffix = suffix.substring(index);
			prefix = filePath.substring(0, index + suffix.indexOf("/") + 1);
			suffix = suffix.substring(suffix.indexOf("/"));
		}
		return (prefix + context.getJobID() + suffix);
	}
	
	private static Path getJOBTempPath(String filePath) {
		String suffix = filePath;
		String prefix = "";
		if(suffix.contains("://")) {
			int index = filePath.indexOf("://") + 3;
			suffix = suffix.substring(index);
			prefix = filePath.substring(0, index + suffix.indexOf("/") + 1);
			suffix = suffix.substring(suffix.indexOf("/"));
		}
		return new Path(prefix + context.getJobID());
	}
	
	public static int getCompressionTypeValue(String type) {
		if (QueryIOConstants.LZ4.equals(type)) {
			return QueryIOConstants.COMPRESSION_TYPE_LZ4;
		} else if (QueryIOConstants.SNAPPY.equals(type)) {
			return QueryIOConstants.COMPRESSION_TYPE_SNAPPY;
		} else if (QueryIOConstants.GZ.equals(type)) {
			return QueryIOConstants.COMPRESSION_TYPE_GZIP;
		} else {
			return QueryIOConstants.COMPRESSION_TYPE_NONE;
		}
	}
	
	public static boolean getEncryptionTypeValue(String type) {
		if (QueryIOConstants.AES256.equals(type)) {
			return true;
		} else {
			return false;
		}
	}
	 
	 public Map<String, String> getObjectMetadata(Configuration conf, String filePath, String tableName) throws Exception {

		if (filePath.contains("://")) {
			filePath = filePath.substring(filePath.indexOf("://") + 3);
			filePath = filePath.substring(filePath.indexOf("/"));
		}
		Map map = null;	
		Connection connection  = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet res = null;
		IDataTagParser tagParser = null;
		try {
			connection = UserDefinedTagResourceFactory.getConnectionWithPoolInit(conf, true);
			
			if(tableName==null){
				tagParser = UserDefinedTagResourceFactory.getParserFromConstructor(conf, filePath, null, null);
				
				tableName = UserDefinedTagResourceFactory.getTableName(tagParser, filePath);
			}
			
			DatabaseMetaData meta = connection.getMetaData();
			res = meta.getTables(null, null, null, 
			     new String[] {"TABLE"});
			boolean found = false;
			while (res.next()) {
				if(res.getString("TABLE_NAME").equalsIgnoreCase(tableName)){
			    	 found = true;
				}
			}
			
			if(!found){
				return null;
			}
			  
			stmt = connection.prepareStatement(QUERY_START + tableName + QUERY_END);
			
			stmt.setString(1, filePath);
			
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(stmt);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			if(rs.next()){
				map = new HashMap();
				for(int i=1; i<=rsmd.getColumnCount(); i++){
						map.put(rsmd.getColumnName(i).toUpperCase(), rs.getObject(rsmd.getColumnName(i)));
					}
				}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeResultSet(res);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(stmt);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception ex) {
				AppLogger.getLogger().fatal("Error closing database connection.", ex);
			}
		}
		return map;
	}

	
	@Override
	public RecordReader<LongWritable, Text> createRecordReader(InputSplit split,
			TaskAttemptContext context) throws IOException, InterruptedException {
		return new LineRecordReader();
	}
	
	public static void setJobContext(JobContext job) {
		context = job;
	}
	
	/**
	 * Proxy PathFilter that accepts a path only if all filters given in the
	 * constructor do. Used by the listPaths() to apply the built-in
	 * hiddenFileFilter together with a user provided one (if any).
	 */
	private static class MultiPathFilter implements PathFilter {
		private List<PathFilter> filters;

		public MultiPathFilter(List<PathFilter> filters) {
			this.filters = filters;
		}

		public boolean accept(Path path) {
			for (PathFilter filter : filters) {
				if (!filter.accept(path)) {
					return false;
				}
			}
			return true;
		}
	}
	

	
}
