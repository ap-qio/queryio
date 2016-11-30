package com.queryio.core.datasources;

import java.sql.Connection;
import java.util.ArrayList;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.dao.DataSourceDAO;

public class DataSourceManager {

	public static DWRResponse addFTPDataSource(String id, String host, int port, String username, String password) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			int dC = DataSourceDAO.getDataConnectionType(connection, id);

			if (dC != -1) {
				response.setDwrResponse(false, "Data connection with specified id already exists", 500);
				return response;
			}

			DataSourceDAO.addDataConnection(connection, id, QueryIOConstants.DATA_CONNECTION_TYPE_FTP);

			FTPDataSource ds = new FTPDataSource();
			ds.setId(id);
			ds.setHost(host);
			ds.setPort(port);
			ds.setUsername(username);
			ds.setPassword(password);

			DataSourceDAO.addFTPDataSource(connection, ds);

			response.setDwrResponse(true, "Datasource added successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be added", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse updateFTPDataSource(String id, String host, int port, String username, String password) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			DataSourceDAO.deleteDataConnection(connection, id);
			addFTPDataSource(id, host, port, username, password);

			response.setDwrResponse(true, "Datasource updated successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be updated", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse addSFTPDataSource(String id, String host, int port, String username, String password) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			int dC = DataSourceDAO.getDataConnectionType(connection, id);

			if (dC != -1) {
				response.setDwrResponse(false, "Data connection with specified id already exists", 500);
				return response;
			}

			DataSourceDAO.addDataConnection(connection, id, QueryIOConstants.DATA_CONNECTION_TYPE_SFTP);

			SFTPDataSource ds = new SFTPDataSource();
			ds.setId(id);
			ds.setHost(host);
			ds.setPort(port);
			ds.setUsername(username);
			ds.setPassword(password);

			DataSourceDAO.addSFTPDataSource(connection, ds);

			response.setDwrResponse(true, "Datasource added successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be added", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse updateSFTPDataSource(String id, String host, int port, String username, String password) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			DataSourceDAO.deleteDataConnection(connection, id);
			addSFTPDataSource(id, host, port, username, password);

			response.setDwrResponse(true, "Datasource updated successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be updated", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse addHTTPDataSource(String id, String baseURL, String username, String password) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			int dC = DataSourceDAO.getDataConnectionType(connection, id);

			if (dC != -1) {
				response.setDwrResponse(false, "Data connection with specified id already exists", 500);
				return response;
			}

			DataSourceDAO.addDataConnection(connection, id, QueryIOConstants.DATA_CONNECTION_TYPE_HTTP);

			HTTPDataSource ds = new HTTPDataSource();
			ds.setId(id);
			ds.setBaseURL(baseURL);
			ds.setUserName(username);
			ds.setPassword(password);

			DataSourceDAO.addHTTPDataSource(connection, ds);

			response.setDwrResponse(true, "Datasource added successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be added", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse updateHTTPDataSource(String id, String baseURL, String username, String password) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			DataSourceDAO.deleteDataConnection(connection, id);
			addHTTPDataSource(id, baseURL, username, password);

			response.setDwrResponse(true, "Datasource updated successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be updated", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse addEmailDataSource(String id, String emailAddress, String password,
			String mailServerAddress, String accountName, String protocol, String socket, String port,
			String connectionTimeOut, String readTimeOut) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			int dC = DataSourceDAO.getDataConnectionType(connection, id);

			if (dC != -1) {
				response.setDwrResponse(false, "Data connection with specified id already exists", 500);
				return response;
			}

			int portInt = Integer.parseInt(port);
			long connTimeOut = Long.parseLong(connectionTimeOut);
			long rdTimeOut = Long.parseLong(readTimeOut);

			DataSourceDAO.addDataConnection(connection, id, QueryIOConstants.DATA_CONNECTION_TYPE_EMAIL);

			EmailDataSource ds = new EmailDataSource();
			ds.setId(id);
			ds.setEmailAddress(emailAddress);
			ds.setPassword(password);
			ds.setMailServerAddress(mailServerAddress);
			ds.setAccountName(accountName);
			ds.setProtocol(protocol);
			ds.setSocketType(socket);
			ds.setPort(portInt);
			ds.setConnectionTimeOut(connTimeOut);
			ds.setReadTimeOut(rdTimeOut);

			DataSourceDAO.addEmailDataSource(connection, ds);

			response.setDwrResponse(true, "Datasource added successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be added", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse updateEmailDataSource(String id, String emailAddress, String password,
			String mailServerAddress, String accountName, String protocol, String socket, String port,
			String connectionTimeOut, String readTimeOut) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			DataSourceDAO.deleteDataConnection(connection, id);
			addEmailDataSource(id, emailAddress, password, mailServerAddress, accountName, protocol, socket, port,
					connectionTimeOut, readTimeOut);

			response.setDwrResponse(true, "Datasource updated successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be updated", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse addHDFSDataSource(String id, String host, int port, String group, String username) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			int dC = DataSourceDAO.getDataConnectionType(connection, id);

			if (dC != -1) {
				response.setDwrResponse(false, "Data connection with specified id already exists", 500);
				return response;
			}

			DataSourceDAO.addDataConnection(connection, id, QueryIOConstants.DATA_CONNECTION_TYPE_HDFS);

			HDFSDataSource ds = new HDFSDataSource();
			ds.setId(id);
			ds.setHost(host);
			ds.setPort(port);
			ds.setGroup(group);
			ds.setUsername(username);

			DataSourceDAO.addHDFSDataSource(connection, ds);

			response.setDwrResponse(true, "Datasource added successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be added", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse updateHDFSDataSource(String id, String host, int port, String group, String username) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			DataSourceDAO.deleteDataConnection(connection, id);
			addHDFSDataSource(id, host, port, group, username);

			response.setDwrResponse(true, "Datasource updated successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be updated", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse addSSHDataSource(String id, String host, int port, String user, String pass, String key) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			int dC = DataSourceDAO.getDataConnectionType(connection, id);

			if (dC != -1) {
				response.setDwrResponse(false, "Data connection with specified id already exists", 500);
				return response;
			}

			DataSourceDAO.addDataConnection(connection, id, QueryIOConstants.DATA_CONNECTION_TYPE_SSH);

			SSHDataSource ds = new SSHDataSource();
			ds.setId(id);
			ds.setHost(host);
			ds.setPort(port);
			ds.setUsername(user);
			ds.setPassword(pass);
			ds.setKey(key);

			DataSourceDAO.addSSHDataSource(connection, ds);

			response.setDwrResponse(true, "Datasource added successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be added", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse updateDBDataSource(String id, String driver, String connUrl, String username,
			String password, String driverJar) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			DataSourceDAO.deleteDataConnection(connection, id);
			DataBaseDataSource ds = new DataBaseDataSource();
			ds.setId(id);
			ds.setDriver(driver);
			ds.setConnectionURL(connUrl);
			ds.setUserName(username);
			ds.setPassword(password);
			ds.setJarFileName(driverJar);

			AppLogger.getLogger().debug(ds.getId() + " : " + ds.getDriver() + " : " + ds.getConnectionURL() + " : "
					+ ds.getUserName() + " : " + ds.getPassword() + " : " + ds.getJarFileName());

			DataSourceDAO.addDataConnection(connection, id, QueryIOConstants.DATA_CONNECTION_TYPE_DATABASE);
			DataSourceDAO.addDataBaseDataSource(connection, ds);

			response.setDwrResponse(true, "Datasource updated successfully", 200);

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be updated", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
			try {
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse updateSSHDataSource(String id, String host, int port, String user, String pass,
			String key) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			DataSourceDAO.deleteDataConnection(connection, id);
			addSSHDataSource(id, host, port, user, pass, key);

			response.setDwrResponse(true, "Datasource updated successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be updated", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse addS3DataSource(String id, String accessKey, String secretAccessKey) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			int dC = DataSourceDAO.getDataConnectionType(connection, id);

			if (dC != -1) {
				response.setDwrResponse(false, "Data connection with specified id already exists", 500);
				return response;
			}

			DataSourceDAO.addDataConnection(connection, id, QueryIOConstants.DATA_CONNECTION_TYPE_S3);

			S3DataSource ds = new S3DataSource();
			ds.setId(id);
			ds.setAccessKey(accessKey);
			ds.setSecretAccessKey(secretAccessKey);

			DataSourceDAO.addS3DataSource(connection, ds);

			response.setDwrResponse(true, "Datasource added successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be added", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static DWRResponse updateS3DataSource(String id, String accessKey, String secretAccessKey) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			DataSourceDAO.deleteDataConnection(connection, id);
			addS3DataSource(id, accessKey, secretAccessKey);

			response.setDwrResponse(true, "Datasource updated successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Datasource could not be updated", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static ArrayList getAllFTPDataSources() {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return DataSourceDAO.getALLFTPDataSources(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllHDFSDataSources() {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return DataSourceDAO.getALLHDFSDataSources(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllHTTPDataSources() {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return DataSourceDAO.getALLHTTPDataSources(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllEmailDataSources() {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return DataSourceDAO.getALLEmailDataSources(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllS3DataSources() {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return DataSourceDAO.getALLS3DataSources(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllDataConnections() {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return DataSourceDAO.getALLDataConnections(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static Object getDataSource(String id) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			int type = DataSourceDAO.getDataConnectionType(connection, id);
			if (type == QueryIOConstants.DATA_CONNECTION_TYPE_FTP) {
				return DataSourceDAO.getFTPDataSource(connection, id);
			} else if (type == QueryIOConstants.DATA_CONNECTION_TYPE_S3) {
				return DataSourceDAO.getS3DataSource(connection, id);
			} else if (type == QueryIOConstants.DATA_CONNECTION_TYPE_HTTP) {
				return DataSourceDAO.getHTTPDataSource(connection, id);
			} else if (type == QueryIOConstants.DATA_CONNECTION_TYPE_EMAIL) {
				return DataSourceDAO.getEmailDataSource(connection, id);
			} else if (type == QueryIOConstants.DATA_CONNECTION_TYPE_HDFS) {
				return DataSourceDAO.getHDFSDataSource(connection, id);
			} else if (type == QueryIOConstants.DATA_CONNECTION_TYPE_SSH) {
				return DataSourceDAO.getSSHDataSource(connection, id);
			} else if (type == QueryIOConstants.DATA_CONNECTION_TYPE_SFTP) {
				return DataSourceDAO.getSFTPDataSource(connection, id);
			} else if (type == QueryIOConstants.DATA_CONNECTION_TYPE_DATABASE) {
				return DataSourceDAO.getDBDataSource(connection, id);
			} else {
				return null;
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static FTPDataSource getFTPDataSource(String id) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return DataSourceDAO.getFTPDataSource(connection, id);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static SFTPDataSource getSFTPDataSource(String id) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return DataSourceDAO.getSFTPDataSource(connection, id);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static HDFSDataSource getHDFSDataSource(String id) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return DataSourceDAO.getHDFSDataSource(connection, id);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static SSHDataSource getSSHDataSource(String id) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return DataSourceDAO.getSSHDataSource(connection, id);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static DataBaseDataSource getDBDataSource(String id) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return DataSourceDAO.getDBDataSource(connection, id);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static HTTPDataSource getHTTPDataSource(String id) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return DataSourceDAO.getHTTPDataSource(connection, id);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static EmailDataSource getEmailDataSource(String id) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return DataSourceDAO.getEmailDataSource(connection, id);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static S3DataSource getS3DataSource(String id) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return DataSourceDAO.getS3DataSource(connection, id);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static DWRResponse deleteDataConnection(ArrayList<String> id) {
		DWRResponse response = new DWRResponse();
		// response.setId(id);
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			for (int i = 0; i < id.size(); i++)
				DataSourceDAO.deleteDataConnection(connection, id.get(i).toString());

			response.setDwrResponse(true, "Entries deleted successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Entries could not be deleted", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}
}
