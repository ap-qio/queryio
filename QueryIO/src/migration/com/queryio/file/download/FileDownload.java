package com.queryio.file.download;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.AccessControlException;

import com.queryio.common.util.AppLogger;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.requestprocessor.GetFileRequest;

public class FileDownload extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("doGet");

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("File download request recevied from host: " + req.getRemoteAddr()
						+ ", user: " + req.getRemoteUser());
			String nameNodeId = req.getParameter("namenode");
			String filePath = req.getParameter("filePath");
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("filePath: " + filePath);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("namenode: " + nameNodeId);
			String fsDefaultName = RemoteManager.getFsDefaultName(nameNodeId);

			ServletContext context = getServletConfig().getServletContext();
			String mimetype = context.getMimeType(filePath);

			if (mimetype == null) {
				mimetype = "application/octet-stream";
			}

			GetFileRequest request = new GetFileRequest(req.getRemoteUser(), new Path(filePath), nameNodeId,
					fsDefaultName, res, mimetype);
			request.process();
		} catch (AccessControlException ex) {
			AppLogger.getLogger().fatal("ACCESS DENIED " + ex.getMessage(), ex);
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			res.setContentType("text/html");
			PrintWriter writer = res.getWriter();
			writer.write("<html><font color=\"red\">Access Denied</font></html>");
			writer.flush();

		} catch (Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			PrintWriter writer = res.getWriter();
			writer.write("<html><font color=\"red\">Internal Server Error</font></html>");
			writer.flush();
		}
	}
}