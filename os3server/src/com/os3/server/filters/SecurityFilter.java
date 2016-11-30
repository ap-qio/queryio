package com.os3.server.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

public class SecurityFilter implements Filter {

	static Logger logger = Logger.getLogger(SecurityFilter.class);

	public SecurityFilter() {
		// DO nothing
	}

	public void destroy() {
		// DO nothing

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// FIXME handle acls
		// TODO - Add handling for authenticating clients and ACLs here,
		// allowing all for now.

		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

}
