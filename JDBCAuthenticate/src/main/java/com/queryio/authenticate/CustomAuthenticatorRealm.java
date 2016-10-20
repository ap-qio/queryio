package com.queryio.authenticate;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;


public class CustomAuthenticatorRealm extends RealmBase{

private String username;

private String password;
private DetailBean dbDetail;

public static String getMD5Hash(String message)
		throws UnsupportedEncodingException, NoSuchAlgorithmException {
	MessageDigest digest = MessageDigest.getInstance("MD5");
	digest.update(message.getBytes(), 0, message.length());
	return new BigInteger(1, digest.digest()).toString(16);
}

public Principal authenticate(String username, String credentials) {
	this.username = username;
	this.password = credentials;
	CoreDBConfigParser dbConfig = null;
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try{
		String query = "SELECT * FROM USERS WHERE USERNAME=? AND PASSWORD=?";
		String xmlPath = getDbConfigFilePath();
		dbConfig = new CoreDBConfigParser();
		dbConfig.loadDatabaseConfiguration(xmlPath);
		this.dbDetail = dbConfig.getDBDetail();
		String jarDir = getDbConfigFilePath().substring(0,getDbConfigFilePath().lastIndexOf("/"));
		jarDir = jarDir.substring(0,jarDir.lastIndexOf("/"));
		File file = new File(jarDir+File.separator+"jdbcJars"+File.separator+this.dbDetail.getDriverJar());
		URL u = file.toURI().toURL();
		URLClassLoader ucl = new URLClassLoader(new URL[] { u });
		Driver driver = (Driver)Class.forName(this.dbDetail.getDriverName(), true, ucl).newInstance();
		DriverManager.registerDriver(new DriverShim(driver));
		
		//Class.forName(this.dbDetail.getDriverName());
		con = DriverManager.getConnection (this.dbDetail.getUrl(),this.dbDetail.getUserName(),this.dbDetail.getPassword());
		stmt = con.prepareStatement(query);
		stmt.setString(1, username);
		stmt.setString(2, SecurityHandler.encryptData(password));
		
		rs = stmt.executeQuery();
		if(rs.next()){
			return getPrincipal(this.username);
		}
	}
	catch(Exception e){
		  System.out.println("Caught Exception while SQL transactions, "+e);
	}
	finally {
		   	try {
		   		if (rs != null)
		   			rs.close();
		   	} catch (Exception e) {
		   		System.out.println("Caught Exception while closing Result Set: "+e);
		   	}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				System.out.println("Caught Exception while closing Statement: "+e);
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				System.out.println("Caught Exception while closing Connection: "+e);
			}
	}
	return null;
}

@Override
protected Principal getPrincipal(String username) {
	List<String> roles = new ArrayList<String>();
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String roleQuery = "SELECT USERNAME, ROLENAME FROM USER_ROLES";
	try{
		String jarDir = getDbConfigFilePath().substring(0,getDbConfigFilePath().lastIndexOf("/"));
		jarDir = jarDir.substring(0,jarDir.lastIndexOf("/"));
		File file = new File(jarDir+File.separator+"jdbcJars"+File.separator+this.dbDetail.getDriverJar());
		URL u = file.toURI().toURL();
		URLClassLoader ucl = new URLClassLoader(new URL[] { u });
		Driver driver = (Driver)Class.forName(this.dbDetail.getDriverName(), true, ucl).newInstance();
		DriverManager.registerDriver(new DriverShim(driver));
		con =  DriverManager.getConnection(this.dbDetail.getUrl(), this.dbDetail.getUserName(),this.dbDetail.getPassword());
		stmt = con.createStatement();
		rs = stmt.executeQuery(roleQuery);
		while(rs.next()){
			if(rs.getString("USERNAME").equals(this.username)){
				 roles.add(rs.getString("ROLENAME"));
			}
		}
	}
	catch(Exception  e){
		System.out.println("Caught excpetion while sql transactions, "+e);
	}
	finally{
		try {
	   		if (rs != null)
	   			rs.close();
	   	} catch (Exception e) {
	   		System.out.println("Caught Exception while closing Result Set: "+e);
	   	}
		try {
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			System.out.println("Caught Exception while closing Statement: "+e);
		}
		try {
			if (con != null)
				con.close();
		} catch (Exception e) {
			System.out.println("Caught Exception while closing Connection: "+e);
		}
	}

    return new GenericPrincipal(username, password, roles);
}

@Override
protected String getPassword(String string) {
    return password;
}

@Override
protected String getName() {
    return username;
}

/* Custom variables, see <Realm> element */
private String dbConfigFilePath;

public String getDbConfigFilePath() {
	return dbConfigFilePath;
}

public void setDbConfigFilePath(String dbConfigFilePath) {
	this.dbConfigFilePath = dbConfigFilePath;
}

}