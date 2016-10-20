package com.queryio.demo.adhoc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.InvalidInputException;
import org.apache.hadoop.mapreduce.security.TokenCache;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.util.StringUtils;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.demo.common.CombinedSplit;
import com.queryio.demo.common.CustomRecordReader;

public class AdHocInputFormat extends InputFormat<IntWritable, List<FileStatus>> {
	public static final String INPUT_DIR = "mapreduce.input.customfileinputformat.inputdir";
	public static final String SPLIT_MAXSIZE = "mapreduce.input.customfileinputformat.split.maxsize";
	public static final String SPLIT_MINSIZE = "mapreduce.input.customfileinputformat.split.minsize";
	public static final String PATHFILTER_CLASS = "mapreduce.input.custompathFilter.class";
	public static final String NUM_INPUT_FILES = "mapreduce.input.customfileinputformat.numinputfiles";

	private static final Log LOG = LogFactory.getLog(AdHocInputFormat.class);

	// private static String pathFilter = null;

	private static Pattern pathFilter = null;

	private int numSplits = 0;
	CombinedSplit combinedSplit = null;
	private int defaultUnitNUmSplits = 25;
	private int unitNumSplits = defaultUnitNUmSplits;

	public static void setPathFilter(String filePathFilter) {
		pathFilter = Pattern.compile(filePathFilter);
	}

	private static final PathFilter HIDDEN_FILE_FILTER = new PathFilter() {
		public boolean accept(Path p) {
			String name = p.getName();
			return !name.startsWith("_") && !name.startsWith(".") && !name.startsWith("job_");
		}
	};

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

	/**
	 * Get the lower bound on split size imposed by the format.
	 * 
	 * @return the number of bytes of the minimal split for this format
	 */
	protected long getFormatMinSplitSize() {
		return 1;
	}

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
		return true;
	}

	/**
	 * Set a PathFilter to be applied to the input paths for the map-reduce job.
	 * 
	 * @param job
	 *            the job to modify
	 * @param filter
	 *            the PathFilter class use for filtering the input paths.
	 */
	public static void setInputPathFilter(Job job, Class<? extends PathFilter> filter) {
		LOG.info("setInputPathFilter: " + filter);
		job.getConfiguration().setClass(PATHFILTER_CLASS, filter, PathFilter.class);
	}

	/**
	 * Set the minimum input split size
	 * 
	 * @param job
	 *            the job to modify
	 * @param size
	 *            the minimum size
	 */
	public static void setMinInputSplitSize(Job job, long size) {
		job.getConfiguration().setLong(SPLIT_MINSIZE, size);
	}

	/**
	 * Get the minimum split size
	 * 
	 * @param job
	 *            the job
	 * @return the minimum number of bytes that can be in a split
	 */
	public static long getMinSplitSize(JobContext job) {
		return job.getConfiguration().getLong(SPLIT_MINSIZE, 1L);
	}

	/**
	 * Set the maximum split size
	 * 
	 * @param job
	 *            the job to modify
	 * @param size
	 *            the maximum split size
	 */
	public static void setMaxInputSplitSize(Job job, long size) {
		job.getConfiguration().setLong(SPLIT_MAXSIZE, size);
	}

	/**
	 * Get the maximum split size.
	 * 
	 * @param context
	 *            the job to look at.
	 * @return the maximum number of bytes a split can include
	 */
	public static long getMaxSplitSize(JobContext context) {
		return context.getConfiguration().getLong(SPLIT_MAXSIZE, Long.MAX_VALUE);
	}

	/**
	 * Get a PathFilter instance of the filter set for the input paths.
	 * 
	 * @return the PathFilter instance set for the job, NULL if none has been
	 *         set.
	 */
	public static PathFilter getInputPathFilter(JobContext context) {
		Configuration conf = context.getConfiguration();
		Class<?> filterClass = conf.getClass(PATHFILTER_CLASS, null, PathFilter.class);
		LOG.info("filterClass: " + filterClass);
		return (filterClass != null) ? (PathFilter) ReflectionUtils.newInstance(filterClass, conf) : null;
	}

	/**
	 * List input directories. Subclasses may override to, e.g., select only
	 * files matching a regular expression.
	 * 
	 * @param job
	 *            the job to list input paths for
	 * @return array of FileStatus objects
	 * @throws IOException
	 *             if zero items.
	 */
	protected List<FileStatus> listStatus(JobContext job) throws IOException {
		List<FileStatus> result = new ArrayList<FileStatus>();
		Path[] dirs = getInputPaths(job);
		if (dirs.length == 0) {
			throw new IOException("No input paths specified in job");
		}

		// get tokens for all the required FileSystems..
		TokenCache.obtainTokensForNamenodes(job.getCredentials(), dirs, job.getConfiguration());

		List<IOException> errors = new ArrayList<IOException>();

		// creates a MultiPathFilter with the hiddenFileFilter and the
		// user provided one (if any).
		List<PathFilter> filters = new ArrayList<PathFilter>();
		filters.add(HIDDEN_FILE_FILTER);
		PathFilter jobFilter = getInputPathFilter(job);
		if (jobFilter != null) {
			filters.add(jobFilter);
		}
		PathFilter inputFilter = new MultiPathFilter(filters);

		for (int i = 0; i < dirs.length; ++i) {
			Path parentPath = dirs[i];
			FileSystem fs = parentPath.getFileSystem(job.getConfiguration());
			result.addAll(getFiles(job, fs, parentPath, inputFilter, errors));
		}

		if (!errors.isEmpty()) {
			throw new InvalidInputException(errors);
		}
		LOG.info("Total input paths to process : " + result.size());
		return result;
	}

	private List<FileStatus> getFiles(JobContext job, FileSystem fs, Path parentPath, PathFilter inputFilter, List<IOException> errors)
			throws IOException {

		boolean isRecursive = job.getConfiguration().getBoolean(QueryIOConstants.HIVE_QUERYIO_PARSE_RECURSIVE, false);
		LOG.info("isRecursive: " + isRecursive);
		boolean isFilterQuery = job.getConfiguration().getBoolean(QueryIOConstants.HIVE_QUERYIO_FILTER_APPLY, false);
		LOG.info("isFilterQuery: " + isFilterQuery);

		if (isFilterQuery) {
			return addFilterQueryPaths(job, fs, parentPath, inputFilter, errors);
		} else {
			return getFilePaths(job, fs, parentPath, inputFilter, errors, isRecursive);
		}
	}

	private List<FileStatus> addFilterQueryPaths(JobContext job, FileSystem fs, Path parentPath, PathFilter inputFilter, List<IOException> errors) {

		String filterQuery = null;
		String hdfsUri = null;

		String driverName = null;
		String connectionURL = null;
		String userName = null;
		String password = null;

		Connection connection = null;
		java.sql.Statement stmt = null;
		ResultSet rs = null;

		List<FileStatus> result = new ArrayList<FileStatus>();

		try {
			FileSystem hdfs = parentPath.getFileSystem(job.getConfiguration());

			if (!hdfs.exists(parentPath)) {
				// If root path (parent directory) dont exists then return
				return result;
			}

			String rootPath = StringUtils.escapeString(parentPath.toString());

			driverName = job.getConfiguration().get(QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_DRIVER_FILTER);
			LOG.info("driverName: " + driverName);
			connectionURL = job.getConfiguration().get(QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_URL_FILTER);
			LOG.info("connectionURL: " + connectionURL);
			userName = job.getConfiguration().get(QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_USERNAME_FILTER);
			LOG.info("userName: " + userName);
			password = job.getConfiguration().get(QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_PASSWORD_FILTER);
			LOG.info("password: " + password);
			filterQuery = job.getConfiguration().get(QueryIOConstants.HIVE_QUERYIO_FILTER_QUERY);
			LOG.info("filterQuery: " + filterQuery);
			hdfsUri = job.getConfiguration().get(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			LOG.info("hdfsUri: " + hdfsUri);

			try {
				Class.forName(driverName);
			} catch (ClassNotFoundException e) {
				LOG.fatal("addFilterQueryPaths: ", e);
				return result;
			}

			connection = DriverManager.getConnection(connectionURL, userName, password);

			stmt = connection.createStatement();
			rs = stmt.executeQuery(filterQuery);

			String t = null;
			String tPath = null;
			while (rs.next()) {
				t = rs.getString(1);
				if (t != null) {
					tPath = hdfsUri.concat(t);

					if (!hdfs.exists(new Path(tPath)))
						continue;

					if (tPath.startsWith(rootPath)) {

						if (pathFilter != null) {
							if (pathFilter.matcher(tPath).matches())
								result.add(hdfs.getFileStatus(new Path(tPath)));
						} else {
							result.add(hdfs.getFileStatus(new Path(tPath)));
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.fatal("addFilterQueryPaths: ", e);
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				LOG.fatal("addFilterQueryPaths: ", e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {
				LOG.fatal("addFilterQueryPaths: ", e);
			}
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				LOG.fatal("addFilterQueryPaths: ", e);
			}
		}
		return result;

	}

	private List<FileStatus> getFilePaths(JobContext job, FileSystem fs, Path p, PathFilter inputFilter, List<IOException> errors, boolean isRecursive)
			throws IOException, FileNotFoundException {

		List<FileStatus> result = new ArrayList<FileStatus>();

		FileStatus[] matches = fs.globStatus(p, inputFilter);
		if (matches == null) {
			errors.add(new IOException("Input path does not exist: " + p));
		} else if (matches.length == 0) {
			errors.add(new IOException("Input Pattern " + p + " matches 0 files"));
		} else {
			for (FileStatus globStat : matches) {
				if (globStat.isDirectory()) {
					for (FileStatus stat : fs.listStatus(globStat.getPath(), inputFilter)) {
						if (stat.isDirectory()) {
							if (isRecursive)
								result.addAll(getFilePaths(job, fs, stat.getPath(), inputFilter, errors, isRecursive));
						} else {
							if (pathFilter != null) {
								if (pathFilter.matcher(stat.getPath().toString()).matches())
									result.add(stat);
							} else {
								result.add(stat);
							}
						}
					}
				} else {
					if (pathFilter != null) {
						if (pathFilter.matcher(globStat.getPath().toString()).matches())
							result.add(globStat);
					} else {
						result.add(globStat);
					}
				}
			}
		}
		return result;
	}

	/**
	 * A factory that makes the split for this class. It can be overridden by
	 * sub-classes to make sub-types
	 */

	protected void makeSplit(Path file, long start, long length, String[] hosts, List<InputSplit> splits) {
		if (combinedSplit == null) {
			combinedSplit = new CombinedSplit();
		}
		combinedSplit.createSplit(file, start, length, hosts);
		numSplits++;

		if (numSplits >= unitNumSplits) {
			LOG.debug("numSplits: " + numSplits + " combinedSplit: " + combinedSplit.getAllSplits());
			splits.add(combinedSplit);
			numSplits = 0;
			combinedSplit = null;
		}

	}

	protected void makeLastSplit(List<InputSplit> splits) {
		if (combinedSplit != null)
			splits.add(combinedSplit);
	}

	/**
	 * Generate the list of files and make them into FileSplits.
	 * 
	 * @param job
	 *            the job context
	 * @throws IOException
	 */
	public List<InputSplit> getSplits(JobContext job) throws IOException {

		this.unitNumSplits = job.getConfiguration().getInt(QueryIOConstants.QUERYIO_UNIT_NUM_SPLITS, defaultUnitNUmSplits);
		// generate splits
		List<InputSplit> splits = new ArrayList<InputSplit>();
		List<FileStatus> files = listStatus(job);
		for (FileStatus file : files) {
			Path path = file.getPath();
			long length = file.getLen();
			if (length != 0) {
				FileSystem fs = path.getFileSystem(job.getConfiguration());
				BlockLocation[] blkLocations = fs.getFileBlockLocations(file, 0, length);
				makeSplit(path, 0, length, blkLocations[0].getHosts(), splits);
			} else {
				// Create empty hosts array for zero length files
				makeSplit(path, 0, length, new String[0], splits);
			}
		}
		// Save the number of input files for metrics/loadgen
		job.getConfiguration().setLong(NUM_INPUT_FILES, files.size());
		makeLastSplit(splits);
		LOG.info("Total # of splits: " + splits.size());
		return splits;
	}

	protected long computeSplitSize(long blockSize, long minSize, long maxSize) {
		return Math.max(minSize, Math.min(maxSize, blockSize));
	}

	protected int getBlockIndex(BlockLocation[] blkLocations, long offset) {
		for (int i = 0; i < blkLocations.length; i++) {
			// is the offset inside this block?
			if ((blkLocations[i].getOffset() <= offset) && (offset < blkLocations[i].getOffset() + blkLocations[i].getLength())) {
				return i;
			}
		}
		BlockLocation last = blkLocations[blkLocations.length - 1];
		long fileLength = last.getOffset() + last.getLength() - 1;
		throw new IllegalArgumentException("Offset " + offset + " is outside of file (0.." + fileLength + ")");
	}

	/**
	 * Sets the given comma separated paths as the list of inputs for the
	 * map-reduce job.
	 * 
	 * @param job
	 *            the job
	 * @param commaSeparatedPaths
	 *            Comma separated paths to be set as the list of inputs for the
	 *            map-reduce job.
	 */
	public static void setInputPaths(Job job, String commaSeparatedPaths) throws IOException {
		setInputPaths(job, StringUtils.stringToPath(getPathStrings(commaSeparatedPaths)));
	}

	/**
	 * Add the given comma separated paths to the list of inputs for the
	 * map-reduce job.
	 * 
	 * @param job
	 *            The job to modify
	 * @param commaSeparatedPaths
	 *            Comma separated paths to be added to the list of inputs for
	 *            the map-reduce job.
	 */
	public static void addInputPaths(Job job, String commaSeparatedPaths) throws IOException {
		for (String str : getPathStrings(commaSeparatedPaths)) {
			addInputPath(job, new Path(str));
		}
	}

	/**
	 * Set the array of {@link Path}s as the list of inputs for the map-reduce
	 * job.
	 * 
	 * @param job
	 *            The job to modify
	 * @param inputPaths
	 *            the {@link Path}s of the input directories/files for the
	 *            map-reduce job.
	 */
	public static void setInputPaths(Job job, Path... inputPaths) throws IOException {
		Configuration conf = job.getConfiguration();
		Path path = inputPaths[0].getFileSystem(conf).makeQualified(inputPaths[0]);
		StringBuffer str = new StringBuffer(StringUtils.escapeString(path.toString()));
		for (int i = 1; i < inputPaths.length; i++) {
			str.append(StringUtils.COMMA_STR);
			path = inputPaths[i].getFileSystem(conf).makeQualified(inputPaths[i]);
			str.append(StringUtils.escapeString(path.toString()));
		}
		conf.set(INPUT_DIR, str.toString());
	}

	/**
	 * Add a {@link Path} to the list of inputs for the map-reduce job.
	 * 
	 * @param job
	 *            The {@link Job} to modify
	 * @param path
	 *            {@link Path} to be added to the list of inputs for the
	 *            map-reduce job.
	 */
	public static void addInputPath(Job job, Path path) throws IOException {
		Configuration conf = job.getConfiguration();
		path = path.getFileSystem(conf).makeQualified(path);
		String dirStr = StringUtils.escapeString(path.toString());
		String dirs = conf.get(INPUT_DIR);
		conf.set(INPUT_DIR, dirs == null ? dirStr : dirs + "," + dirStr);
	}

	// This method escapes commas in the glob pattern of the given paths.
	private static String[] getPathStrings(String commaSeparatedPaths) {
		int length = commaSeparatedPaths.length();
		int curlyOpen = 0;
		int pathStart = 0;
		boolean globPattern = false;
		List<String> pathStrings = new ArrayList<String>();

		for (int i = 0; i < length; i++) {
			char ch = commaSeparatedPaths.charAt(i);
			switch (ch) {
			case '{': {
				curlyOpen++;
				if (!globPattern) {
					globPattern = true;
				}
				break;
			}
			case '}': {
				curlyOpen--;
				if (curlyOpen == 0 && globPattern) {
					globPattern = false;
				}
				break;
			}
			case ',': {
				if (!globPattern) {
					pathStrings.add(commaSeparatedPaths.substring(pathStart, i));
					pathStart = i + 1;
				}
				break;
			}
			}
		}
		pathStrings.add(commaSeparatedPaths.substring(pathStart, length));

		return pathStrings.toArray(new String[0]);
	}

	/**
	 * Get the list of input {@link Path}s for the map-reduce job.
	 * 
	 * @param context
	 *            The job
	 * @return the list of input {@link Path}s for the map-reduce job.
	 */
	public static Path[] getInputPaths(JobContext context) {
		String dirs = context.getConfiguration().get(INPUT_DIR, "");
		String[] list = StringUtils.split(dirs);
		Path[] result = new Path[list.length];
		for (int i = 0; i < list.length; i++) {
			result[i] = new Path(StringUtils.unEscapeString(list[i]));
		}
		return result;
	}

	public RecordReader<IntWritable, List<FileStatus>> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException,
			InterruptedException {
		return new CustomRecordReader();
	}

}