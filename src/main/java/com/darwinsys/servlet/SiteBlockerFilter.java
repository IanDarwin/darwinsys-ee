/**
 * 
 */
package com.darwinsys.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Block one or more sites by DNS; this is to combat DOS attacks.
 * Initial version is hard-coded PoC; will use context initParameter, plurality
 */
// NOT ANNOTATED - enable in web.xml (principle of least surprise)
public class SiteBlockerFilter implements Filter {
	
	static String BAD_DOMAIN = "your-server.de";

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String sitelist = filterConfig.getInitParameter("sitelist");
		if (sitelist != null) {
			BAD_DOMAIN = sitelist;
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		// Common Java Server DoS attempt: create many bogus sessions. Reject.
		if (req.getRequestedSessionId() != null && !req.isRequestedSessionIdValid()) {
			resp.sendError(400, "Session Expired");
			return;
		}
		String origin = req.getRemoteHost();
		if (origin.endsWith(BAD_DOMAIN)) {
			resp.sendError(404, "Something missing here");
			return;
		}
		chain.doFilter(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
