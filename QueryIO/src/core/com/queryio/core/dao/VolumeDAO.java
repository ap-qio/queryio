package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.core.bean.Node;
import com.queryio.core.bean.Volume;

public class VolumeDAO 
{
	public static ArrayList getAllVolumes(Connection connection, String nodeId) throws Exception
	{
		ArrayList volumes = new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try
		{
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_VOLUMES_FOR_NODEID);
			pst.setString(1, nodeId);
			
			rs = pst.executeQuery();
			
			Volume volume;
			while (rs.next())
			{
				String[] disks = rs.getString(ColumnConstants.COL_VOLUMES_DISK).split(",");
				String[] paths = rs.getString(ColumnConstants.COL_VOLUMES_PATH).split(",");
				for(int i = 0; i < disks.length; i ++){
					if(paths.length > i){
						volume = new Volume();
						volume.setNodeId(nodeId);				
						volume.setDisk(disks[i]);
						volume.setPath(paths[i]);						
						volumes.add(volume);
					}
				}
			}
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}		
		return volumes;
	}
	
	public static int getAllVolumesCount(Connection connection, String nodeId) throws Exception
	{
		PreparedStatement pst = null;
		ResultSet rs = null;
		int count = 0;
		try
		{
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_VOLUMES_FOR_NODEID);
			pst.setString(1, nodeId);
			
			rs = pst.executeQuery();
			
			if (rs.next())
			{				
				String result = rs.getString(ColumnConstants.COL_VOLUMES_PATH);		
				count = result.split(",").length;
			}
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}		
		return count;
	}
	
	public static int getAllDataNodeVolumesCount(Connection connection) throws Exception
	{
		int count = 0;
		
		ArrayList datanodes = NodeDAO.getAllDatanodes(connection);
	
		for(int i=0; i<datanodes.size(); i++)
		{
			count += VolumeDAO.getAllVolumesCount(connection, ((Node)datanodes.get(i)).getId());
		}
		
		return count;
	}
	
	public static String getDisk(Connection connection, String nodeId, String path) throws Exception
	{
		ArrayList list = getAllVolumes(connection, nodeId);
		path = path.replace("\\", "");
		Volume vol;
		for(int j = 0; j < list.size(); j ++){
			vol = (Volume)list.get(j);
			String volPath = vol.getPath();
//			volPath = vol.getPath().replace("\\", "").replace("/", "");	//TODO: Don't know why this was added.
			if(volPath.contains(path) || path.contains(volPath)){
				return vol.getDisk();
			}	
		}
		return null;
	}
	
	public static void addVolume(Connection connection, String nodeId, String diskName, String volumePath) throws SQLException
	{
		PreparedStatement pst = null;
		try
		{
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_INSERT_VOLUME);
			pst.setString(1, nodeId);
			pst.setString(2, diskName);
			pst.setString(3, volumePath);
			
			pst.executeUpdate();
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(pst);
		}		
	}
	
	public static void updateDisks(Connection connection, String nodeId, String diskName) throws SQLException
	{
		PreparedStatement pst = null;
		try
		{
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_UPDATE_VOLUME_DISK);
			
			pst.setString(1, diskName);
			pst.setString(2, nodeId);
			pst.executeUpdate();
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(pst);
		}		
	}
	
	public static void updatePath(Connection connection, String nodeId, String volumePath) throws SQLException
	{
		PreparedStatement pst = null;
		try
		{
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_UPDATE_VOLUME_PATH);
			
			pst.setString(1, volumePath);
			pst.setString(2, nodeId);
			pst.executeUpdate();
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(pst);
		}		
	}
	
	public static void removeVolumes(Connection connection, String nodeId) throws SQLException
	{
		PreparedStatement pst = null;
		try
		{
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_DELETE_VOLUME_FOR_NODEID);
			pst.setString(1, nodeId);
			
			pst.execute();
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(pst);
		}		
	}

	public static ArrayList getAllDisksForHosts(Connection connection, int hostId) throws Exception {
		ArrayList result = new ArrayList();
		ArrayList nodes = NodeDAO.getAllNodesForHost(connection, hostId);
		ArrayList list = null;
		Node node = null;
		Volume vol = null;
		for(int i = 0; i < nodes.size(); i ++){
			node = (Node)nodes.get(i);
			list = getAllVolumes(connection, node.getId());
			for(int j = 0; j < list.size(); j ++){
				vol = (Volume)list.get(j);
				if(!result.contains(vol.getDisk())){
					result.add(vol.getDisk());	
				}	
			}
		}
		return result;
	}
}
