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
 * Block one or more sites by DNS, to combat DOS attacks.
 * Use context initParameter as a plurality (badsite1|crappysite2)
 */
// NOT ANNOTATED - enable in web.xml (principle of least surprise)
public class SiteBlockerFilter implements Filter {
	
	static String[] BAD_DOMAINS = { "your-server.de" };

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String sitelist = filterConfig.getInitParameter("sitelist");
		if (sitelist != null) {
			BAD_DOMAINS = sitelist.split("\\|");
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
		for (String s : BAD_DOMAINS) {
			if (origin.endsWith(s)) {
				// System.out.println("SiteBlockerFilter: rejecting " + origin);
				resp.sendError(404, "Something missing here");
				return;
			}
		}
		// System.out.println("SiteBlockerFilter: accepting " + origin);
		chain.doFilter(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// Empty
	}

}
