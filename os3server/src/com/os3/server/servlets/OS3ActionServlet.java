package com.os3.server.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.os3.server.actions.BaseAction;
import com.os3.server.common.OS3Constants;

public class OS3ActionServlet extends HttpServlet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 811232623584912580L;
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(OS3ActionServlet.class);
	
	/** The service counter. */
	private AtomicLong serviceCounter = new AtomicLong();
	
	/** The shutting down. */
	private volatile boolean shuttingDown;
	
	/** The action objects. */
	private Map<String, BaseAction> actionObjects = new HashMap<String, BaseAction>();
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		loadMapping(config.getServletContext().getRealPath(OS3Constants.MAPPING_FILE));
	}
	
	/**
	 * Creates the action object.
	 *
	 * @param actionClassName the action class name
	 * @return the base action
	 */
	private BaseAction createActionObject(String actionClassName) {
		try {
			return BaseAction.class.cast(Class.forName(actionClassName).newInstance());
		} catch (InstantiationException e) {
			logger.error("Action class object could not be created: " + actionClassName, e);
		} catch (IllegalAccessException e) {
			logger.error("Action class object could not be created: " + actionClassName, e);
		} catch (ClassNotFoundException e) {
			logger.error("Action class not defined: " + actionClassName, e);
		} catch (ClassCastException e) {
			logger.error("Action class not defined for operation: " + actionClassName, e);
		}
		return null;
	}
	
	/**
	 * Load mapping.
	 *
	 * @param fileName the file name
	 * @throws ServletException the servlet exception
	 */
	private void loadMapping(String fileName) throws ServletException {
		File file = new File(fileName);
		if (file.exists()) {
			Properties properties = new Properties();
			InputStream is = null;
			try {
				is = new BufferedInputStream(new FileInputStream(file));
				properties.load(is);
			} catch (IOException ioe) {
				logger.fatal("Could not load actions mapping file " + fileName, ioe);
				throw new Error(ioe);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						logger.warn("Failed to close input stream.", e);
					}
				}
			}
			//Properties extend from Hashtable, which has all its operations as synchronized, so copy it to HashMap.
			Enumeration<Object> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				BaseAction action = createActionObject(properties.getProperty(key));
				if (action != null) {
					this.actionObjects.put(key, action);
					if(logger.isInfoEnabled()) logger.info("Action object created for action " + key + ", action class name: " + properties.getProperty(key));
				} else {
					logger.error("Could not create action for " + key + ", action class name: " + properties.getProperty(key));
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processRequest(req, resp);
	}
	
	

	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}
	

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}

	
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}
	

	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}
	
	/**
	 * Process request.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
//		logger.error("Headers");
//		Enumeration<String> headerEnum = request.getHeaderNames();
//        if(headerEnum != null) {
//            while(headerEnum.hasMoreElements()) {
//                String headerName = headerEnum.nextElement();
//                //Skip host header
//                logger.error(headerName + " : " + request.getHeader(headerName));
//            }
//        }
//        logger.error("-------");
        
//        if (logger.isDebugEnabled()) {
//			logger.debug("Received client request, server shutting down: " + isShuttingDown() +  " async supported: " + request.isAsyncSupported() + " use thread pool for async: " + 
//					Settings.getBoolean("USE_THREAD_POOL") + " URI: " + request.getRequestURI());
//		}
//		logger.error("ActionSevrelt request inputstream length " + request.getInputStream().available());
		if (!isShuttingDown()) {
//			if (request.isAsyncSupported()) {
//				final AsyncContext asyncCtx = request.startAsync(request, response);
//				final Runnable command = new AsyncRequestProcessor(asyncCtx, this, this.actionObjects);
//				if (Settings.getBoolean("USE_THREAD_POOL")) {
//					Executor executor = (Executor)request.getServletContext().getAttribute(OS3Constants.ASYNC_REQUEST_EXECUTOR);
//					executor.execute(command);
//				} else{
//					asyncCtx.start(command);
//				}
//			} else {
				AsyncRequestProcessor.executeRequest(null, this, this.actionObjects, request, response);
//			}
		} else {
			logger.error("Server is shutting down hence cannot handle this request, we should not have received this request on this server.");
			response.sendError(HttpServletResponse.SC_TEMPORARY_REDIRECT, "Server is shutting down hence cannot handle this request, please retry again.");
			response.flushBuffer();
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	
	public void destroy() {
		/*
		 * Check to see whether there are still service methods running, and if
		 * there are, tell them to stop.
		 */
		if (numServices() > 0) {
		    if(logger.isDebugEnabled()) logger.debug("The number of services:" + numServices());
		    setShuttingDown(true);
		}
		long timeout = OS3Constants.DESTROY_TIMEOUT;
		/* Wait for the service methods to stop. */
		while (numServices() > 0 && timeout > 0) {
			try {
				Thread.sleep(5000);
				timeout -= 5000;
	            if(logger.isInfoEnabled()) logger.info("The number of services:" + numServices() + "  Time Out: "+ timeout);
				
			} catch (InterruptedException e) {
				if(logger.isInfoEnabled()) logger.info("Ignore - Thread interrupted while waiting for the service methods to stop.");
			}
		}
		if(timeout <= 0) {
			logger.error("Server is stopping but all requests may not be processed yet, the number of active requests is " + this.serviceCounter.get());
		}
		if(logger.isDebugEnabled()) logger.info("Destroying Request Thread");
		super.destroy();
	}

	/**
	 * Sets the shutting down.
	 *
	 * @param flag the new shutting down
	 */
	protected void setShuttingDown(boolean flag) {
		this.shuttingDown = flag;
	}

	/**
	 * Checks if is shutting down.
	 *
	 * @return true, if is shutting down
	 */
	protected boolean isShuttingDown() {
		return this.shuttingDown;
	}

	/**
	 * Entering service method.
	 */
	protected void enteringServiceMethod() {
		final long value = this.serviceCounter.incrementAndGet();
		if (logger.isDebugEnabled()) {
			logger.debug("service method started, active request count: " + value);
		}
	}

	/**
	 * Leaving service method.
	 */
	protected void leavingServiceMethod() {
		final long value = this.serviceCounter.decrementAndGet();
		if (logger.isDebugEnabled()) {
			logger.debug("service method finished, active request count: " + value);
		}
	}

	/**
	 * Num services.
	 *
	 * @return the long
	 */
	protected long numServices() {
		return this.serviceCounter.get();
	}
}
