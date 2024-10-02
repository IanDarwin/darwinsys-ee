package com.darwinsys.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * Servlet Filter to do compression.
 * @author Main class by Stephen Neal(?), hacked on by Ian Darwin.
 * GzipResponseWrapper class by Ian Darwin.
 */
public class CompressingFilter implements Filter {

	/*
	 * @see jakarta.servlet.Filter#init(jakarta.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		// nothing to do.
	}

	/**
	 * If the request is of type HTTP *and* the user's browser will
	 * accept GZIP encoding, do it; otherwise just pass the request on.
	 * @param req The ServletRequest
	 * @param resp The ServletResponse
	 * @param chain The FilterChain
	 * @throws IOException on error
	 * @throws ServletException on error
	 */
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
	throws IOException, ServletException {
		System.out.println("CompressingFilter.doFilter()");
		if (req instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) req;
			System.out.println("CompressingFilter.doFilter(): " + request.getRequestURI());
			HttpServletResponse response = (HttpServletResponse) resp;
			// XXX should maybe use getHeaders() and iterate?
			String acceptableEncodings = request.getHeader("accept-encoding");
			if (acceptableEncodings != null
					&& acceptableEncodings.indexOf("gzip") != -1) {

				System.out.println("CompressingFilter.doFilter(): doing compression!");

				// Create a delegate for the Response object; all methods
				// are directly delegated except getOutputStream.
				// This wrapper class is defined below.
				GZipResponseWrapper wrappedResponse = new GZipResponseWrapper(
						response);
				try {
					chain.doFilter(req, wrappedResponse);
				} finally {
					wrappedResponse.flush();
				}
				return;
			}
		}
		System.out.println("CompressingFilter.doFilter(): bottom");
		chain.doFilter(req, resp);
	}

	public void destroy() {
		// nothing to do.
	}

	/**
	 * Inner Class is a ServletResponse that does compression
	 * @author Ian Darwin
	 */
	static class GZipResponseWrapper extends HttpServletResponseWrapper {

		/**
		 * @param response The ServletResponse
		 * @throws IOException If anything fails to read or write
		 */
		public GZipResponseWrapper(HttpServletResponse response) throws IOException {
			super(response);
			createOutputStream();
		}

		/**
		 * @return The OutputStream
		 * @throws IOException If anything fails to read or write
		 */
		private ServletOutputStream createOutputStream() throws IOException {
			servletOutputStream = super.getOutputStream();
			GZIPOutputStream zippedOutputStream = new GZIPOutputStream(servletOutputStream);
			myServletOutputStream = new MyServletOutputStream(
							zippedOutputStream);
			return myServletOutputStream;
		}
		
		private PrintWriter writer = null;
		
		private OutputStream stream = null;

		/** Inner inner class that is just needed because
		 * getOutputStream has to return a ServletOutputStream.
		 * @author Ian Darwin
		 */
		static class MyServletOutputStream extends ServletOutputStream {
			private OutputStream os;

			MyServletOutputStream(GZIPOutputStream os) {
				super();
				this.os = os;
			}

			/** Delegate the write() to the GzipOutputStream
			 * @param val The int value to write
			 */
			public void write(int val) throws IOException {
				os.write(val);
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setWriteListener(WriteListener writeListener) {
				System.err.println("WARN: WriteListener not supported");
			}
		}

		/**
		 * The original output stream that we are wrapping;
		 * needs to be a field so we can flush() it.
		 */
		ServletOutputStream servletOutputStream;

		/** The gzipped output stream */
		MyServletOutputStream myServletOutputStream;
		
		/** getOutputStream() override that gives you the GzipOutputStream.
		 * XXX Assumes you only call this once!!
		 * @see jakarta.servlet.ServletResponse#getOutputStream()
		 * @return The output stream
		 */
		public ServletOutputStream getOutputStream() throws IOException {
			if (writer != null)
	            throw new IllegalStateException("getWriter() was already called for this response");

	        if (stream == null)
	            stream = createOutputStream();
	        
			return myServletOutputStream;
		}
		
		@Override
		public PrintWriter getWriter() throws IOException {
			if (stream != null) {
				throw new IllegalStateException("getOutputStream was already called");
			}
			if (writer == null) {
				writer = new PrintWriter(getOutputStream());
			}
			return writer;
		}

		/** Added method so we can be sure the GZipOutputStream
		 * gets flushed.
		 * @throws IOException
		 */
		public void flush() throws IOException {
			myServletOutputStream.flush();
			servletOutputStream.flush();
		}

	}
}
