package jsf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * Many JSF pages have a lot of overhead but don't need to change often.
 * Try caching them.
 * @author Ian Darwin
 */
public class JsfCacheFilter implements Filter {
	private static final String INIT_PARAMKEY_EXTEN = "EXTENSION";
	ServletContext servletContext;
	String ourExtension;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		servletContext = filterConfig.getServletContext();
		ourExtension = filterConfig.getInitParameter(INIT_PARAMKEY_EXTEN);
		if (ourExtension == null) {
			throw new ServletException("JsfCacheFilter needs " + INIT_PARAMKEY_EXTEN + " init param");
		}
		if (ourExtension.charAt(0) != '.') {
			System.err.println("\"JsfCacheFilter \" + INIT_PARAMKEY_EXTEN + \" value should begin with '.', correcting it");
			ourExtension = '.' + ourExtension;
		}
		System.out.printf("JsfCacheFilter installed as %s with ext = %s\n", filterConfig.getFilterName(), ourExtension);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest req = (HttpServletRequest) request;
		final HttpServletResponse resp = (HttpServletResponse) response;
		final String basePath = "/".equals(req.getRequestURI()) ? "/index" + ourExtension : req.getRequestURI();
		final String fullPath = servletContext.getRealPath(basePath);
		System.out.printf("JsfCacheFilter.doFilter(): base %s -> real %s\n", basePath, fullPath);
		// Don't apply to root or to non-JSF requests
		if (fullPath != null && fullPath.endsWith(ourExtension)) {
			System.out.printf("JsfCacheFilter: %s matched ext %s\n", fullPath, ourExtension);
			String fileName = fullPath.replaceFirst(ourExtension + "$", ".html");
			if (new File(fileName).exists() ) {
				System.out.println("JsfCacheFilter: wrapping Request");
				HttpServletRequestWrapper wrap = 
						new HttpServletRequestWrapper(req) {
					public String getPathInfo() {
						// First arg to replaceFirst is a regex; $ means end of string.
						return basePath.replaceFirst(ourExtension + "$", ".html");
					}
				};
				request = wrap;
			} else {
				System.out.println("JsfCacheFilter: wrapping Response to create output html");
				// Do a tee-like trick with the output stream to create html...
				final OutputStream os = new FileOutputStream(fileName);
				HttpServletResponseWrapper wrappedResp =
					new HttpServletResponseWrapper(resp) {
						public ServletOutputStream getOutputStream() throws IOException {
							return new TeeOutputStream(resp.getOutputStream(), os);
						}
				};
				response = wrappedResp;
			}
		}
		// Last but not least: pass control down the line.
		chain.doFilter(request, response);
		
		// Really last, close the output file(s).
	}

	@Override
	public void destroy() {
		// empty
	}

	class TeeOutputStream extends ServletOutputStream {
		final OutputStream o1, o2;
		TeeOutputStream(OutputStream o1, OutputStream o2) {
			System.out.println("TeeOutputStream()");
			this.o1 = o1;
			this.o2 = o2;
		}

		@Override
		public void write(int b) throws IOException {
			o1.write(b);
			o2.write(b);
		}
		@Override
		public void close() throws IOException {
			o1.close();
			o2.close();
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {
			System.err.println("TeeOutputStream: Ignoring setWriteListener");
		}
	}
}
