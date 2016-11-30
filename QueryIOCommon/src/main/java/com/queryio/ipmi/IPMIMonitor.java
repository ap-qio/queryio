package com.queryio.ipmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.queryio.common.util.AppLogger;

public class IPMIMonitor {
	private static int SENSOR_STATUS = 3;
	private static int SENSOR_VALUE = 4;

	private static int STATUS = 0;
	private static int VALUE = 1;
	private static int UNIT = 2;

	// private static boolean isIPMISupported()
	// {
	//
	// if(!SystemMonitor.isLinux(SystemMonitor.OS_NAME))
	// {
	// return false;
	// }
	//
	// String ipmiSupportConfirmation = "dmidecode --type 38";
	// Process checkIPMISupportProcess = null;
	// BufferedReader reader = null;
	//
	// try
	// {
	// checkIPMISupportProcess =
	// Runtime.getRuntime().exec(ipmiSupportConfirmation);
	// reader = new BufferedReader(new
	// InputStreamReader(checkIPMISupportProcess.getInputStream()));
	// String line = null;
	//
	// while((line = reader.readLine()) != null)
	// {
	// if(line.startsWith("IPMI Device Information"))
	// {
	// return true;
	// }
	// }
	// }
	// catch(IOException e)
	// {
	// AppLogger.getLogger().fatal("dmidecode command could not be executed :" +
	// e.getMessage() + e);
	// }
	// finally
	// {
	// if(reader != null)
	// {
	// try
	// {
	// reader.close();
	// }
	// catch (IOException e)
	// {
	// AppLogger.getLogger().fatal("Could not close BufferedReader :" +
	// e.getMessage() + e);
	// }
	// }
	// }
	// return false;
	// }

	private static BufferedReader executeIPMICommand(String command) {
		Process ipmiMonitoringProcess = null;

		try {
			ipmiMonitoringProcess = Runtime.getRuntime().exec(command);
			return new BufferedReader(new InputStreamReader(ipmiMonitoringProcess.getInputStream()));
		} catch (Exception e) {
			// AppLogger.getLogger().fatal("Could not execute command '" +
			// command + "' :" + e.getMessage() + e);
		}
		return null;
	}

	private static Map<String, String[]> getSensorsStatus(String command) {
		// String lineStart

		Map<String, String[]> sensorMap = null;

		BufferedReader reader = executeIPMICommand(command);

		if (reader == null) {
			return null;
		}

		String line;
		try {
			sensorMap = new HashMap<String, String[]>();
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("Full sensor")) {
					String[] splitLine = line.split("[\\s]*\\|[\\s]*");
					if (!splitLine[SENSOR_STATUS].equalsIgnoreCase("unknown")) {
						String[] values = new String[3];
						values[STATUS] = splitLine[SENSOR_STATUS];

						values[VALUE] = splitLine[SENSOR_VALUE].substring(0, splitLine[SENSOR_VALUE].indexOf(' '));
						values[UNIT] = splitLine[SENSOR_VALUE].substring(splitLine[SENSOR_VALUE].indexOf(' ') + 1);
						sensorMap.put(splitLine[2], values);
					}
				}
			}
			return sensorMap;
		} catch (IOException e) {
			AppLogger.getLogger().fatal("Could not parse command '" + command + "' :" + e.getMessage() + e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					AppLogger.getLogger().fatal("Could not close BufferedReader :" + e.getMessage() + e);
				}
			}
		}
		return null;
	}

	/*
	 * /* 1 01h / "Warn-lo", // "Warning-lo", /* 2 02h / "Crit-lo", //
	 * "Critical-lo", /* 3 04h / "BelowCrit", // "BelowCrit-lo", /* 4 08h /
	 * "Warn-hi", // "Warning-hi", /* 5 10h / "Crit-hi", // "Critical-hi", /* 6
	 * 20h / "AboveCrit", // "AboveCrit-hi", /* 7 40h / "Init ", /*in init
	 * state, no reading
	 */
	/*
	 * 8 80h / "OK* ",
	 */

	public static Map<String, String[]> getTemperatureStatus() {
		// String command = "ipmiutil sensor -g Temp -c";
		// return getSensorsStatus(command);
		return getSensorsStatus("Temp");

	}

	public static Map<String, String[]> getFanStatus() {
		// String command = "ipmiutil sensor -g Fan -c";
		// return getSensorsStatus(command);
		return getSensorsStatus("Fan");
	}

	public static Map<String, String[]> getVoltageStatus() {
		// String command = "ipmiutil sensor -g Voltage -c";
		// return getSensorsStatus(command);
		return getSensorsStatus("Volt");
	}

}