package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.database.TableConstants;
import com.queryio.core.bean.MigrationInfo;

public class MigrationInfoDAO {
	public static void insert(Connection connection, MigrationInfo migrationStatus) throws Exception{
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_INSERT_MIGRATIONINFO);
			int i=1;
			ps.setString(i++, migrationStatus.getNamenodeId());
			ps.setBoolean(i++, migrationStatus.isImportType());
			ps.setString(i++, migrationStatus.getTitle());
			ps.setTimestamp(i++, migrationStatus.getStartTime());
			ps.setTimestamp(i++, migrationStatus.getEndTime());
			ps.setString(i++, migrationStatus.getDataStore());
			ps.setString(i++, migrationStatus.getDestinationPath());
			ps.setString(i++, migrationStatus.getSourcePath());
			ps.setString(i++, migrationStatus.getStatus());
			ps.setDouble(i++, migrationStatus.getProgress());
			ps.setBoolean(i++, migrationStatus.isSecure());
			ps.setBoolean(i++, migrationStatus.isUnzip());
			ps.setString(i++, migrationStatus.getCompressionType());
			ps.setString(i++, migrationStatus.getEncryptionType());
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}	
	}
	
	public static MigrationInfo getById(Connection connection, int id) throws Exception{
		MigrationInfo migrationInfo = new MigrationInfo();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = connection.prepareStatement(QueryConstants.PREPARED_QRY_SELECT_MIGRATIONINFO_ID);
			pst.setInt(1, id);
			rs = pst.executeQuery();
			
			while(rs.next()){			
				migrationInfo.setId(rs.getInt(ColumnConstants.COL_MIGRATIONINFO_ID));
				migrationInfo.setNamenodeId(rs.getString(ColumnConstants.COL_MIGRATIONINFO_NAMENODEID));
				migrationInfo.setImportType(rs.getBoolean(ColumnConstants.COL_MIGRATIONINFO_ISIMPORTTYPE));
				migrationInfo.setTitle(rs.getString(ColumnConstants.COL_MIGRATIONINFO_TITLE));
				migrationInfo.setDataStore(rs.getString(ColumnConstants.COL_MIGRATIONINFO_DATASTORE));
				migrationInfo.setStartTime(rs.getTimestamp(ColumnConstants.COL_MIGRATIONINFO_STARTTIME));
				migrationInfo.setEndTime(rs.getTimestamp(ColumnConstants.COL_MIGRATIONINFO_ENDTIME));
				migrationInfo.setDestinationPath(rs.getString(ColumnConstants.COL_MIGRATIONINFO_DESTINATIONPATH));
				migrationInfo.setSourcePath(rs.getString(ColumnConstants.COL_MIGRATIONINFO_SOURCEPATH));
				migrationInfo.setStatus(rs.getString(ColumnConstants.COL_MIGRATIONINFO_STATUS));
				migrationInfo.setProgress(rs.getDouble(ColumnConstants.COL_MIGRATIONINFO_PROGRESS));
				migrationInfo.setSecure(rs.getBoolean(ColumnConstants.COL_MIGRATIONINFO_ISSECURE));
				migrationInfo.setUnzip(rs.getBoolean(ColumnConstants.COL_MIGRATIONINFO_UNZIP));
				migrationInfo.setCompressionType(rs.getString(ColumnConstants.COL_MIGRATIONINFO_COMPRESSION_TYPE));
				migrationInfo.setEncryptionType(rs.getString(ColumnConstants.COL_MIGRATIONINFO_ENCRYPTION_TYPE));
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(pst, rs);
		}
		return migrationInfo;	
	}
	public static MigrationInfo getByTitle(Connection connection, String title) throws Exception{
		MigrationInfo migrationInfo = new MigrationInfo();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = connection.prepareStatement(QueryConstants.PREPARED_QRY_SELECT_MIGRATIONINFO_TITLE);
			pst.setString(1, title);
			rs = pst.executeQuery();
			
			while(rs.next()){			
				migrationInfo.setId(rs.getInt(ColumnConstants.COL_MIGRATIONINFO_ID));
				migrationInfo.setNamenodeId(rs.getString(ColumnConstants.COL_MIGRATIONINFO_NAMENODEID));
				migrationInfo.setImportType(rs.getBoolean(ColumnConstants.COL_MIGRATIONINFO_ISIMPORTTYPE));
				migrationInfo.setTitle(rs.getString(ColumnConstants.COL_MIGRATIONINFO_TITLE));
				migrationInfo.setDataStore(rs.getString(ColumnConstants.COL_MIGRATIONINFO_DATASTORE));
				migrationInfo.setStartTime(rs.getTimestamp(ColumnConstants.COL_MIGRATIONINFO_STARTTIME));
				migrationInfo.setEndTime(rs.getTimestamp(ColumnConstants.COL_MIGRATIONINFO_ENDTIME));
				migrationInfo.setDestinationPath(rs.getString(ColumnConstants.COL_MIGRATIONINFO_DESTINATIONPATH));
				migrationInfo.setSourcePath(rs.getString(ColumnConstants.COL_MIGRATIONINFO_SOURCEPATH));
				migrationInfo.setStatus(rs.getString(ColumnConstants.COL_MIGRATIONINFO_STATUS));
				migrationInfo.setProgress(rs.getDouble(ColumnConstants.COL_MIGRATIONINFO_PROGRESS));
				migrationInfo.setSecure(rs.getBoolean(ColumnConstants.COL_MIGRATIONINFO_ISSECURE));
				migrationInfo.setUnzip(rs.getBoolean(ColumnConstants.COL_MIGRATIONINFO_UNZIP));
				migrationInfo.setCompressionType(rs.getString(ColumnConstants.COL_MIGRATIONINFO_COMPRESSION_TYPE));
				migrationInfo.setEncryptionType(rs.getString(ColumnConstants.COL_MIGRATIONINFO_ENCRYPTION_TYPE));
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(pst, rs);
		}
		return migrationInfo;	
	}
	
	
	
	public static MigrationInfo delete(Connection connection, int id) throws Exception{
		MigrationInfo migrationInfo = new MigrationInfo();
		PreparedStatement pst = null;
		try{
			pst = connection.prepareStatement("DELETE FROM " + TableConstants.TABLE_MIGRATIONINFO +" WHERE " + ColumnConstants.COL_MIGRATIONINFO_ID +" = ?");
			pst.setInt(1, id);
			CoreDBManager.executeUpdateStatement(connection, pst);
		}finally{
			DatabaseFunctions.closePreparedStatement(pst);
		}
		return migrationInfo;	
	}
	
	public static MigrationInfo get(Connection connection, Timestamp timestamp) throws Exception{
		MigrationInfo migrationInfo = new MigrationInfo();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = connection.prepareStatement(QueryConstants.PREPARED_QRY_SELECT_MIGRATIONINFO);
			pst.setTimestamp(1, timestamp);
			rs = pst.executeQuery();
			
			while(rs.next()){			
				migrationInfo.setId(rs.getInt(ColumnConstants.COL_MIGRATIONINFO_ID));
				migrationInfo.setNamenodeId(rs.getString(ColumnConstants.COL_MIGRATIONINFO_NAMENODEID));
				migrationInfo.setImportType(rs.getBoolean(ColumnConstants.COL_MIGRATIONINFO_ISIMPORTTYPE));
				migrationInfo.setTitle(rs.getString(ColumnConstants.COL_MIGRATIONINFO_TITLE));
				migrationInfo.setDataStore(rs.getString(ColumnConstants.COL_MIGRATIONINFO_DATASTORE));
				migrationInfo.setStartTime(rs.getTimestamp(ColumnConstants.COL_MIGRATIONINFO_STARTTIME));
				migrationInfo.setEndTime(rs.getTimestamp(ColumnConstants.COL_MIGRATIONINFO_ENDTIME));
				migrationInfo.setDestinationPath(rs.getString(ColumnConstants.COL_MIGRATIONINFO_DESTINATIONPATH));	
				migrationInfo.setSourcePath(rs.getString(ColumnConstants.COL_MIGRATIONINFO_SOURCEPATH));
				migrationInfo.setStatus(rs.getString(ColumnConstants.COL_MIGRATIONINFO_STATUS));
				migrationInfo.setProgress(rs.getDouble(ColumnConstants.COL_MIGRATIONINFO_PROGRESS));
				migrationInfo.setSecure(rs.getBoolean(ColumnConstants.COL_MIGRATIONINFO_ISSECURE));
				migrationInfo.setUnzip(rs.getBoolean(ColumnConstants.COL_MIGRATIONINFO_UNZIP));
				migrationInfo.setCompressionType(rs.getString(ColumnConstants.COL_MIGRATIONINFO_COMPRESSION_TYPE));
				migrationInfo.setEncryptionType(rs.getString(ColumnConstants.COL_MIGRATIONINFO_ENCRYPTION_TYPE));
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(pst, rs);
		}
		return migrationInfo;
	}
	
	public static void update(Connection connection, MigrationInfo migrationStatus) throws Exception{
		PreparedStatement ps = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_UPDATE_MIGRATIONSTATUS);
			ps.setDouble(1, migrationStatus.getProgress());
			ps.setString(2, migrationStatus.getStatus());
			ps.setTimestamp(3, migrationStatus.getStartTime());
			ps.setTimestamp(4, migrationStatus.getEndTime());
			ps.setInt(5, migrationStatus.getId());
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}	
	}
	
	public static ArrayList getAll(Connection connection) throws Exception{
		ArrayList data = new ArrayList();
		Statement st = null;
		ResultSet rs = null;
		try{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM " + TableConstants.TABLE_MIGRATIONINFO + " ORDER BY " + ColumnConstants.COL_MIGRATIONINFO_STARTTIME + " DESC");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
			
			while(rs.next()){
				ArrayList rowData = new ArrayList();				
				rowData.add(rs.getInt(ColumnConstants.COL_MIGRATIONINFO_ID));				
				rowData.add(rs.getBoolean(ColumnConstants.COL_MIGRATIONINFO_ISIMPORTTYPE));
				rowData.add(rs.getString(ColumnConstants.COL_MIGRATIONINFO_NAMENODEID));
				rowData.add(rs.getString(ColumnConstants.COL_MIGRATIONINFO_TITLE));			
				rowData.add(rs.getString(ColumnConstants.COL_MIGRATIONINFO_DATASTORE));			
				rowData.add(rs.getString(ColumnConstants.COL_MIGRATIONINFO_SOURCEPATH));	
				rowData.add(rs.getString(ColumnConstants.COL_MIGRATIONINFO_DESTINATIONPATH));								
				rowData.add(sdf.format(rs.getTimestamp(ColumnConstants.COL_MIGRATIONINFO_STARTTIME).getTime()));
				Timestamp endTime = rs.getTimestamp(ColumnConstants.COL_MIGRATIONINFO_ENDTIME);
				if(endTime != null && endTime.getTime()<rs.getTimestamp(ColumnConstants.COL_MIGRATIONINFO_STARTTIME).getTime()){
					endTime = null;
				}
				if(endTime != null){
					rowData.add(sdf.format(endTime.getTime()));
				}else{
					rowData.add("-");
				}											
				rowData.add(rs.getString(ColumnConstants.COL_MIGRATIONINFO_STATUS));
				rowData.add(rs.getDouble(ColumnConstants.COL_MIGRATIONINFO_PROGRESS));
				data.add(rowData);
			}
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return data;		
	}

}