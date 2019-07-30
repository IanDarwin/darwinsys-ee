package jsf;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Many JSF pages have a lot of overhead but don't need to change often.
 * Try caching them.
 * @author Ian Darwin
 */
public class JsfCacheFilter implements Filter {
	ServletContext servletContext;
	String ourExtension;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("JsfCacheFilter installed as " + filterConfig.getFilterName());
		servletContext = filterConfig.getServletContext();
		ourExtension = filterConfig.getInitParameter("EXTENSION");
		if (ourExtension == null) {
			throw new ServletException("JsfCacheFilter needs EXTENSION init param");
		}
		if (ourExtension.charAt(0) != '.') {
			System.err.println("\"JsfCacheFilter EXTENSION should begin with '.', correcting it");
			ourExtension = '.' + ourExtension;
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("JsfCacheFilter.doFilter()");
		HttpServletRequest req = (HttpServletRequest) request;
		final String basePath = req.getPathInfo();
		final String path = req.getPathTranslated();
		if (path != null && path.endsWith(ourExtension)) {
			System.out.println("JsfCacheFilter: got ext");
			String fileName = path.replaceFirst(ourExtension + "$", ".html");
			if (new File(fileName).exists() ) {
				System.out.println("JsfCacheFilter: wrapping");
				HttpServletRequestWrapper wrap = 
						new HttpServletRequestWrapper(req) {
					public String getPathInfo() {
						return basePath.replaceFirst(ourExtension + "$", ".html");
					}
				};
				request = wrap;
			} else {
				System.out.println("JsfCacheFilter: not wrapping");
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// empty
	}

}
