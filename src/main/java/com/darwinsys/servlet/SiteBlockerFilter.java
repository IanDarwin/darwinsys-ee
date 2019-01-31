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
 * TODO use context initParameter as a plurality (commas? vbars?)
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
		String origin = req.getRemoteHost();
		if (origin.endsWith(BAD_DOMAIN)) {
			// System.out.println("SiteBlockerFilter: rejecting " + origin);
			resp.sendError(404, "Something missing here");
			return;
		}
		// System.out.println("SiteBlockerFilter: accepting " + origin);
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
