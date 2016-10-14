package com.queryio.database.migrate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.MetadataConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.MigrationInfo;
import com.queryio.core.dao.MigrationInfoDAO;
import com.queryio.userdefinedtags.common.UserDefinedTagDAO;

public class ExportFromHDFSToDataBase implements Runnable{

	static String dataType[]=null;
	static String preparedStmtMethod[] = null;
	private MigrationInfo migrationInfo;
	private int unitCount = 10;
	boolean flag;
	private String username;
	private String password;
	static String delimiter;
	static String separatorValue;
	private String loginUser;
	public static String hdfsuri;
	private String loginUserGroup;
	
	private String tableName;
	
	private Connection connection = null;		
	private Statement statement = null;
	
	private Connection dbconnection = null;		
	private Statement dbstatement = null;
	
	private Configuration conf = null;
	
	private String responseJson = null;
	
	public StringBuilder binding = null;
	
	public StringBuilder insertQuery = null;
	
	public String createTableString = null;
	
	public Path path;
	
	public PoolingDataSource ps;
	public  boolean isFirstLineHeader = false;
	
	public ExportFromHDFSToDataBase(String user,String group, MigrationInfo migrationInfo,PoolingDataSource ps,String responseJSON, Configuration conf, Path path) throws Exception{
		this.conf = new Configuration();
		
		this.ps = ps;
		this.migrationInfo = migrationInfo;
		this.username = user;
		this.loginUserGroup = group;
		this.conf = conf;
		this.responseJson = responseJSON;
		
		hdfsuri = this.conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
		AppLogger.getLogger().debug("Original Conf hdfs uri  : " + conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY));
		AppLogger.getLogger().debug("After Assigning : " + this.conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY));
		createTableString = getCreateTableString(this.responseJson, "");
		this.path = path;
		
	}
	@Override
	public void run(){
		flag = true;
		try{

			AppLogger.getLogger().debug("Initiating import");
			
			AppLogger.getLogger().debug("Compression Type: " + migrationInfo.getCompressionType());
			AppLogger.getLogger().debug("Encryption Type: " + migrationInfo.getEncryptionType());
			
			
			FileSystem dfs = FileSystem.get(this.conf);
			FileStatus[] fileList  =  dfs.listStatus(new Path(this.migrationInfo.getSourcePath())) ;
			unitCount = 5/100 * fileList.length;
			if(unitCount == 0)
				unitCount = 1;
						
			exportingToDataBase(this.path);
			
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
		
		}catch(Exception e)
		{
			AppLogger.getLogger().fatal("Error occured in migration.", e);
			AppLogger.getLogger().debug("Error occured in migration.", e);
			migrationInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
			migrationInfo.setStatus(QueryIOConstants.PROCESS_STATUS_FAILED);
		}finally{

			try{
				if(statement !=null)
					statement.close();
			}catch(Exception e){
				AppLogger.getLogger().debug("Error occured in closing statement.", e);
			}
		}if(flag){
			try	
			{	
				connection = CoreDBManager.getQueryIODBConnection();
				MigrationInfoDAO.update(connection, migrationInfo);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			finally
			{
				try
				{
					CoreDBManager.closeConnection(connection);
				}
				catch(Exception e)
				{
					AppLogger.getLogger().fatal("Error closing database connection.", e);
				}
			}
		}
	}
	public  String getCreateTableString(String json , String tableName)throws Exception{
		
		StringBuilder query = new StringBuilder();
		
		 JSONArray jsonObj = (JSONArray)new JSONParser().parse(json);
		 JSONObject obj = (JSONObject)jsonObj.get(0);
		 JSONObject metaObject = (JSONObject)obj.get("meta");
		 isFirstLineHeader = Boolean.parseBoolean(obj.get("header").toString());
		 AppLogger.getLogger().debug("isFirstLineHeader : " + isFirstLineHeader);
		 
		 separatorValue = obj.get("separator").toString();
		 AppLogger.getLogger().debug("separatorValue : " + separatorValue);
		 delimiter = obj.get("delimiter").toString();
		 AppLogger.getLogger().debug("delimiter : " + delimiter);
		 
		 tableName = obj.get("name").toString();
		 tableName = tableName.substring(0 , tableName.length() - 4);
		 this.tableName = tableName;
		 AppLogger.getLogger().debug("Table NAme : " + tableName);
		 
		 query.append("CREATE TABLE ");
		 query.append(tableName);
		 query.append("\n");
		 query.append("(");
		 
		 
		 JSONArray detailsArray = (JSONArray)metaObject.get("details");
		 int length = detailsArray.size();
		 
		 dataType = new String[length];
		 
		 String colNames[] = new String[length];

		 JSONObject columnNames = (JSONObject)metaObject.get("header");
		 
		 for(int i=0;i<length;i++){
			 JSONObject details = (JSONObject)detailsArray.get(i);
			 String key = details.get("index").toString();
			 
			 int arrayIndex = Integer.parseInt(key);
			 
			 String type = details.get("type").toString();
			  
			 dataType[arrayIndex] = type;
			 String name = columnNames.get(arrayIndex+"").toString();
			 
			 colNames[arrayIndex] = name;
		 }
		 
		 binding = new StringBuilder();
		 
		 insertQuery = new StringBuilder();
		 insertQuery.append("INSERT INTO ");
		 insertQuery.append(tableName + " ( ");
		 
		 binding.append("");
		 for(int i=1;i<length;i++){
			 String name = colNames[i];
			 query.append("\n" + colNames[i] + " ");
			 insertQuery.append(name + " , ");
			 binding.append("?,");
			 
			 query.append(dataType[i] + ",");
		 }
		 query.deleteCharAt(query.length()-1);
		 query.append("\n");
		 query.append(");");
		 
		 insertQuery.deleteCharAt(insertQuery.length()-1);
		 insertQuery.deleteCharAt(insertQuery.length()-1);
		 insertQuery.append(") VALUES (");
		 binding.deleteCharAt(binding.length()-1);
		 
		 insertQuery.append(binding + " ) ;");
		 
		 AppLogger.getLogger().debug("Insert Query : " + insertQuery);
		 
		 AppLogger.getLogger().debug("Creating Table Query : " + query.toString());
		 
		 
		 
		 return query.toString();
		 
	}
	public void exportingToDataBase(Path path) throws Exception {
		
		PreparedStatement ps = null;
		InputStream is = null;
		FileSystem dfs = null;
		try{
			dfs = FileSystem.get(conf);
			dbconnection = this.ps.getConnection();
			AppLogger.getLogger().debug("Table Name = " + this.tableName);
				try{
					dbstatement = dbconnection.createStatement();
					if(!UserDefinedTagDAO.checkIfTableExists(dbconnection, this.tableName))
						dbstatement.executeUpdate(createTableString);
				}catch(Exception e){
					AppLogger.getLogger().debug("Error occurred while creating table " , e);
				}
			
			ps = dbconnection.prepareStatement(insertQuery.toString());
			is= dfs.open(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			if(isFirstLineHeader)
				br.readLine();
			
			while((line = br.readLine()) !=null){
						parseLine(line , ps);
			}
			
		}catch(Exception e){
			AppLogger.getLogger().fatal("An Error occurred " + e.getMessage() ,e);
		}finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception e){
				AppLogger.getLogger().fatal("An Error occurred while closing is " + e.getMessage(), e);
			}try{
				if(dfs!=null)
					dfs.close();
			}catch(Exception e){
				AppLogger.getLogger().fatal("An Error occurred while closing dfs " + e.getMessage(), e);
			}try{
				if(ps!=null)
					ps.close();
			}catch(Exception e){
				AppLogger.getLogger().fatal("An Error occurred while closing ps " + e.getMessage(), e);
			}try{
				if(dbstatement!=null){
					dbstatement.close();
				}
			}catch (Exception e) {
				AppLogger.getLogger().fatal("An Error occurred while closing ps " + e.getMessage(), e);
			}try{
				if(dbconnection!=null){
					dbconnection.close();
				}
			}catch (Exception e) {
				AppLogger.getLogger().fatal("An Error occurred while closing ps " + e.getMessage(), e);
			}
		}
	}
	
	
	public void parseLine(String line , PreparedStatement ps)  
	        throws Exception
    {
        String regex = null;
        int i=1;
        if ((separatorValue != null) && (!separatorValue.isEmpty()))
        	regex = "\"([^" + separatorValue + "]*)\"|([^" + delimiter + "]+)";
        else
        	regex = "\"([^" + delimiter + "]*)\"|([^" + delimiter + "]+)";

        Matcher m = Pattern.compile(regex).matcher(line);
        while (m.find()) {
            if (m.group(1) != null) {
            	ps.setObject(i, castValueToCorrespondingObject(m.group(1), i));
            	i++;
            } else {
                ps.setObject(i, castValueToCorrespondingObject(m.group(2), i));
            	i++;
            }
        }
        ps.executeUpdate();
    }

	
	private Object castValueToCorrespondingObject(String value, int index) {
		String className = null;
		try {			
			if(dataType[index].trim().toUpperCase().startsWith("VARCHAR")) {
				return value;
			} else if(dataType[index].trim().toUpperCase().startsWith("DATETIME")) {	// To handle condition as SchemeDetection Class.
				return Date.parse(value);
			}
			else {			
				className = dataType[index].toUpperCase();
				Class classOfColumn = MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.get(className);
				Method valueOfMethod = classOfColumn.getMethod("valueOf", String.class);
				Object castedValue  = valueOfMethod.invoke(classOfColumn, value);
				AppLogger.getLogger().debug("casted Value : " + castedValue);
				return castedValue;
			}
		} catch(Exception e) {
			AppLogger.getLogger().fatal("Error casting to class '" + className + "' for value '" + value + "'", e);
			return value;
		}
		
	}
}
