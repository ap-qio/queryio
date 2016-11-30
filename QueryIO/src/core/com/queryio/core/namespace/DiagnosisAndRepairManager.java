package com.queryio.core.namespace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.DiagnosisStatusBean;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.NNDBDiagnosisStatusDAO;
import com.queryio.core.requestprocessor.DBRepairRequest;
import com.queryio.core.requestprocessor.DBVSNamespaceDiagnosisRequest;
import com.queryio.core.requestprocessor.NamespaceVSDBDiagnosisRequest;

public class DiagnosisAndRepairManager {
	static final Map<String, Thread> SYNCTHREADMAP = new HashMap<String, Thread>();

	public static boolean terminateDiagnosticProcess(final String diagnosisId) {
		if (SYNCTHREADMAP.containsKey(diagnosisId)) {
			SYNCTHREADMAP.get(diagnosisId).interrupt();
			return true;
		}
		return false;
	}

	public static boolean terminateRepairProcess(final String diagnosisId) {
		if (SYNCTHREADMAP.containsKey("Repair_" + diagnosisId)) {
			SYNCTHREADMAP.get("Repair_" + diagnosisId).interrupt();
			return true;
		}
		return false;
	}

	public static boolean diagnoseForUser(final String diagnosisId, final String namenodeId, long startIndex,
			long endIndex, String user) {
		boolean isSuccess = false;

		try {

			String dirPath = EnvironmentalConstants.getAppHome() + QueryIOConstants.DIAGNOSIS_REPORTS_DIR;
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String filePath = EnvironmentalConstants.getAppHome() + QueryIOConstants.DIAGNOSIS_REPORTS_DIR
					+ File.separator + diagnosisId + ".dat";

			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}

			final Connection connection = CoreDBManager.getQueryIODBConnection();

			NNDBDiagnosisStatusDAO.addDiagnosisInfo(connection, diagnosisId, namenodeId);

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Logged-in-user: " + user);

			final NamespaceVSDBDiagnosisRequest requestNMVSDB = new NamespaceVSDBDiagnosisRequest(namenodeId, user,
					startIndex, endIndex, filePath);
			final DBVSNamespaceDiagnosisRequest requestDBVSNM = new DBVSNamespaceDiagnosisRequest(namenodeId, user,
					startIndex, endIndex, filePath);

			Thread thread = new Thread() {
				public void run() {
					try {
						NNDBDiagnosisStatusDAO.updateDiagnosisInfo(connection, diagnosisId, null,
								QueryIOConstants.PROCESS_STATUS_DIAGNOSING, null, false);

						requestNMVSDB.process();
						requestDBVSNM.process();

						NNDBDiagnosisStatusDAO.updateDiagnosisInfo(connection, diagnosisId,
								new Timestamp(System.currentTimeMillis()),
								QueryIOConstants.PROCESS_STATUS_DIAGNOSIS_COMPLETE, null, true);
					} catch (InterruptedException e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
						try {
							NNDBDiagnosisStatusDAO.updateDiagnosisInfo(connection, diagnosisId,
									new Timestamp(System.currentTimeMillis()),
									QueryIOConstants.PROCESS_STATUS_DIAGNOSIS_TERMINATED, null, false);
						} catch (Exception e1) {
							AppLogger.getLogger().fatal(e1.getMessage(), e1);
						}
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
						final Writer result = new StringWriter();
						final PrintWriter printWriter = new PrintWriter(result);
						e.printStackTrace(printWriter);
						try {
							NNDBDiagnosisStatusDAO.updateDiagnosisInfo(connection, diagnosisId,
									new Timestamp(System.currentTimeMillis()),
									QueryIOConstants.PROCESS_STATUS_DIAGNOSIS_FAILED, result.toString(), false);
						} catch (Exception e1) {
							AppLogger.getLogger().fatal(e1.getMessage(), e1);
						}
					}

					try {
						connection.close();
					} catch (SQLException e2) {
						AppLogger.getLogger().fatal(e2.getMessage(), e2);
					}
				}
			};

			SYNCTHREADMAP.put(diagnosisId, thread);
			thread.start();

			isSuccess = true;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			isSuccess = false;
		}
		return isSuccess;
	}

	public static boolean diagnose(final String diagnosisId, final String namenodeId, long startIndex, long endIndex,
			String loggedInUser) {

		if (RemoteManager.isNonAdminAndDemo(loggedInUser)) {
			AppLogger.getLogger().fatal(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
			return false;
		}

		return diagnoseForUser(diagnosisId, namenodeId, startIndex, endIndex, loggedInUser);
	}

	public static ArrayList getDiagnosisStatus() {

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NNDBDiagnosisStatusDAO.getDiagnosisStatus(connection);
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

	public static DiagnosisStatusBean getDiagnosisStatusForId(String diagnosisId) {

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NNDBDiagnosisStatusDAO.getDiagnosisStatus(connection, diagnosisId);
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

	public static DWRResponse deleteDiagnosisStatus(String diagnosisId) {
		DWRResponse response = new DWRResponse();
		response.setId(diagnosisId);
		Connection connection = null;
		try {

			if (RemoteManager.isNonAdminAndDemo(null)) {
				response.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return response;
			}

			connection = CoreDBManager.getQueryIODBConnection();
			NNDBDiagnosisStatusDAO.deleteDiagnosisInfo(connection, diagnosisId);

			response.setDwrResponse(true, "Entry deleted successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			response.setDwrResponse(false, "Entry could not be deleted", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return response;
	}

	public static String getError(String diagnosisId) {

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			DiagnosisStatusBean status = NNDBDiagnosisStatusDAO.getDiagnosisStatus(connection, diagnosisId);
			if (status != null) {
				return status.getError();
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

	public static DWRResponse repairForUser(final String diagnosisId, final String namenodeId, String user) {
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setId(diagnosisId);
		try {
			String filePath = EnvironmentalConstants.getAppHome() + QueryIOConstants.DIAGNOSIS_REPORTS_DIR
					+ File.separator + diagnosisId + ".dat";

			final Connection connection = CoreDBManager.getQueryIODBConnection();

			final DBRepairRequest request = new DBRepairRequest(namenodeId, user, filePath);

			Thread thread = new Thread() {
				public void run() {
					try {
						NNDBDiagnosisStatusDAO.updateDiagnosisInfo(connection, diagnosisId, null,
								QueryIOConstants.PROCESS_STATUS_REPAIRING, null, true);

						request.process();

						NNDBDiagnosisStatusDAO.updateDiagnosisInfo(connection, diagnosisId,
								new Timestamp(System.currentTimeMillis()),
								QueryIOConstants.PROCESS_STATUS_REPAIR_COMPLETE, null, false);
					} catch (InterruptedException e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
						try {
							NNDBDiagnosisStatusDAO.updateDiagnosisInfo(connection, diagnosisId,
									new Timestamp(System.currentTimeMillis()),
									QueryIOConstants.PROCESS_STATUS_REPAIR_TERMINATED, e.getMessage(), true);
						} catch (Exception e1) {
							AppLogger.getLogger().fatal(e1.getMessage(), e1);
						}
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
						final Writer result = new StringWriter();
						final PrintWriter printWriter = new PrintWriter(result);
						e.printStackTrace(printWriter);
						try {
							NNDBDiagnosisStatusDAO.updateDiagnosisInfo(connection, diagnosisId,
									new Timestamp(System.currentTimeMillis()),
									QueryIOConstants.PROCESS_STATUS_REPAIR_FAILED, result.toString(), true);
						} catch (Exception e1) {
							AppLogger.getLogger().fatal(e1.getMessage(), e1);
						}
					}

					try {
						connection.close();
					} catch (SQLException e2) {
						AppLogger.getLogger().fatal(e2.getMessage(), e2);
					}
				}
			};

			SYNCTHREADMAP.put("Repair_" + diagnosisId, thread);
			thread.start();

			dwrResponse.setDwrResponse(true, "Repair operation performed successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, "Repair process failed.", 500);
		}
		return dwrResponse;
	}

	public static DWRResponse repair(final String diagnosisId, final String namenodeId) {

		if (!RemoteManager.isAdmin()) {
			DWRResponse dwrResponse = new DWRResponse();
			dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
			return dwrResponse;
		}

		return repairForUser(diagnosisId, namenodeId, RemoteManager.getLoggedInUser());
	}

	public static JSONObject getDiagnosisReport(String diagnosisId, long startIndex, long endIndex) {
		JSONObject object = new JSONObject();
		JSONArray conflicts = new JSONArray();

		String filePath = EnvironmentalConstants.getAppHome() + QueryIOConstants.DIAGNOSIS_REPORTS_DIR + File.separator
				+ diagnosisId + ".dat";

		FileReader fw = null;
		BufferedReader bw = null;
		try {
			fw = new FileReader(new File(filePath));
			bw = new BufferedReader(fw);

			String line = null;
			int index = 0;

			while (index < startIndex && bw.readLine() != null) {
				index++;
			}

			JSONParser parser = new JSONParser();
			while (index < endIndex && (line = bw.readLine()) != null) {
				conflicts.add(parser.parse(line));
				index++;
			}

			while ((line = bw.readLine()) != null) {
				index++;
			}

			object.put("endIndex", index);
			object.put("conflicts", conflicts);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		return object;
	}
}
