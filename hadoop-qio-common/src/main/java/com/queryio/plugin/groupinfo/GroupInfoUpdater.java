package com.queryio.plugin.groupinfo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.util.SecurityHandler;

public class GroupInfoUpdater extends HttpServlet {
	
	private final Log LOG = LogFactory
			.getLog(GroupInfoUpdater.class);
	
	private static final long serialVersionUID = 1L;

	public GroupInfoUpdater() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}
	
	protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException{
		LOG.info("User group information update request received");
		
		String groupInfo = request.getHeader(QueryIOConstants.GROUP_INFO_REQUEST_HEADER_KEY);
		
		if(groupInfo==null){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, QueryIOConstants.GROUP_INFO_REQUEST_HEADER_KEY + " header not received");
			return;
		}
		
		try{
			GroupInfoContainer.setGroupInfo(SecurityHandler.decryptData(groupInfo));
		} catch (Exception e){
			LOG.fatal(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Group Information could not be updated");
			return;
		}
		
		LOG.debug("Group Information updated");
	}
}
