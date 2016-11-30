package com.queryio.config.db;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.ArrayList;

import com.queryio.common.dao.GenericDBConfigDAO;
import com.queryio.common.database.DBTypeProperties;

public class GenericDBConfigManager {
	public static String[] getAllDatabaseNames() throws FileNotFoundException {
		return GenericDBConfigDAO.getAllDatabaseNames();
	}

	public static ArrayList<String> getAllGenericDataTypes() {
		return GenericDBConfigDAO.getAllGenericDataTypes();
	}

	public static String getGenericDataType(String type, DBTypeProperties props) {
		return GenericDBConfigDAO.getGenericDataType(type, props);
	}

	private static Connection verifyDatabaseSettings(String driverName, String url, String username, String password,
			String jarFile) throws Exception {
		return GenericDBConfigManager.verifyDatabaseSettings(driverName, url, username, password, jarFile);
	}

	public static String getDefaultSchemaFromDBName(String dbName) {
		return GenericDBConfigManager.getDefaultSchemaFromDBName(dbName);
	}

	public static void main(String args[]) {
		try {
			String arr[] = getAllDatabaseNames();
			for (int i = 0; i < arr.length; i++)
				System.out.println(arr[i]);
			System.out.println(getAllGenericDataTypes().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
