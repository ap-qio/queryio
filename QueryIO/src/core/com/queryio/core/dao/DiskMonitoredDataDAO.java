package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.core.bean.DiskMonitoredData;

public class DiskMonitoredDataDAO {
	public static void addMonitoredData(Connection connection, int hostId, String diskName, float diskReadPerSec, float diskWritesPerSec, String diskHealthStatus) throws Exception{
		PreparedStatement deletePst = null;
		PreparedStatement insertPst = null;
		try{
			deletePst = connection.prepareStatement(QueryConstants.QRY_DELETE_DISKMONITOREDDATA);
			deletePst.setInt(1, hostId);
			deletePst.setString(2, diskName);
			deletePst.execute();
			
			insertPst = connection.prepareStatement(QueryConstants.QRY_INSERT_DISKMONITOREDDATA);
			insertPst.setInt(1, hostId);
			insertPst.setString(2, diskName);
			insertPst.setFloat(3, diskReadPerSec);
			insertPst.setFloat(4, diskWritesPerSec);
			insertPst.setString(5, diskHealthStatus);
			insertPst.execute();
		}
		finally{
			DatabaseFunctions.closePreparedStatement(insertPst);
			DatabaseFunctions.closePreparedStatement(deletePst);
		}
	}
	public static DiskMonitoredData getMonitoredData(Connection connection, int hostId, String diskName) throws Exception{
		DiskMonitoredData data = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try{
			pst = connection.prepareStatement(QueryConstants.QRY_SELECT_DISKMONITOREDDATA_DISK);
			pst.setInt(1, hostId);
			pst.setString(2, diskName);
			rs = pst.executeQuery();
			if(rs.next()){
				data = new DiskMonitoredData();
				data.setHostId(hostId);
				data.setDiskName(diskName);
				data.setDiskByteReadsPerSec(rs.getFloat(ColumnConstants.COL_DISKMONITOREDDATA_DSKBYTEREADSPERSEC));
				data.setDiskByteWritesPerSec(rs.getFloat(ColumnConstants.COL_DISKMONITOREDDATA_DSKBYTEWRITESPERSEC));
				data.setDiskHealthStatus(rs.getString(ColumnConstants.COL_DISKMONITOREDDATA_DISKHEALTHSTATUS));
			}
			
		}finally{
			DatabaseFunctions.closeSQLObjects(pst, rs);
		}
		return data;
	}
	public static ArrayList getMonitoredDataOfAllDisks(Connection connection, int hostId) throws Exception{
		ArrayList result = new ArrayList();
		DiskMonitoredData data = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try{
			pst = connection.prepareStatement(QueryConstants.QRY_SELECT_DISKMONITOREDDATA);
			pst.setInt(1, hostId);
			rs = pst.executeQuery();
			if(rs.next()){
				data = new DiskMonitoredData();
				data.setHostId(hostId);
				data.setDiskName(rs.getString(ColumnConstants.COL_DISKMONITOREDDATA_DISKNAME));
				data.setDiskByteReadsPerSec(rs.getFloat(ColumnConstants.COL_DISKMONITOREDDATA_DSKBYTEREADSPERSEC));
				data.setDiskByteWritesPerSec(rs.getFloat(ColumnConstants.COL_DISKMONITOREDDATA_DSKBYTEWRITESPERSEC));
				data.setDiskHealthStatus(rs.getString(ColumnConstants.COL_DISKMONITOREDDATA_DISKHEALTHSTATUS));
				result.add(data);
			}			
		}finally{
			DatabaseFunctions.closeSQLObjects(pst, rs);
		}
		return result;
	}
}
