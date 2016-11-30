package com.queryio.core.requestprocessor;

import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.authorize.AuthorizationException;

import com.queryio.common.util.AppLogger;

public class FSOperationUtil {

	public static void createDirectoryRecursively(Configuration conf, String nameNodeId, String fsDefaultName,
			String destinationPath, String user, String group) throws Exception {
		GetFileStatusRequest statusRequest = new GetFileStatusRequest(user, new Path(destinationPath), nameNodeId,
				fsDefaultName);

		try {
			statusRequest.process();
		} catch (Exception e) {
			if (e instanceof AuthorizationException)
				throw e;
		}
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger()
					.debug("user : " + user + " . group :" + group + " . pathFound : " + statusRequest.isSuccessFul()
							+ " .Status : " + statusRequest.getStatus() + " . Path : " + destinationPath);
		if (statusRequest.getStatus() == null) {

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Destination path \"" + destinationPath + "\" does not exist");

			String[] tokens = destinationPath.split("/");
			for (int i = 0; i < tokens.length; i++) {
				String[] tempTokens = Arrays.copyOfRange(tokens, 0, i);

				String tempPath = "";
				for (int j = 0; j < tempTokens.length; j++) {
					tempPath = tempPath + tempTokens[j] + "/";
				}

				if (tempPath.equals("")) {
					tempPath = "/";
				}

				Path path = new Path(tempPath);

				GetFileStatusRequest subStatusRequest = new GetFileStatusRequest(user, path, nameNodeId, fsDefaultName);
				try {
					subStatusRequest.process();
				} catch (Exception e) {

				}

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("New Destination path \"" + path + "\" does not exist" + " . Status :"
							+ subStatusRequest.getStatus());

				if (subStatusRequest.getStatus() == null) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Creating directory \"" + path + "\"");
					MKDIRRequest mdRequest = new MKDIRRequest(nameNodeId, fsDefaultName, user, group, path, conf);
					mdRequest.process();
				}
			}
		}
	}

}
