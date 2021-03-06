package com.darwinsys.servlet;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.darwinsys.graphics.JigglyTextImageWriter;
import com.darwinsys.security.PassPhrase;

/**
 * Also known to some as "CAPTCHA", this servlet will
 * generate one of those annoying but necessary images that the user has to read
 * and verify to confirm that she's a human being not a spambot. The complication is that we can't
 * generate the image back to the middle of a JSP, so we create
 * it in a temp file, and write the &lt;IMG&gt; tag back to the user
 * <p>
 * Because of this, there must be a <em>writable</em> Temporary
 * Directory named by the *context* init parameter LOCAL_TMP_KEY (defaults
 * to LOCAL_TMP_DIR) inside the web app directory; this is slightly unusual
 * from a security point of view but would be quite hard to subvert
 * since the servlet does not accept any parameters from the user
 * that are used in creating the file.
 * <p>
 * Typical usage of this servlet (in, e.g, contact.jsp):
 * <pre>&lt;jsp:include page="/servlet/HumanResponseServlet"&gt;</pre>
 * <p>
 * Typical code in response servlet, e.g., ContactServlet:
 * <pre>
 * final String actualChallenge = request.getParameter("challenge");
 * final String expectedChallenge =
 *     (String)request.getSession().getAttribute(HumanResponseServlet.SESSION_KEY_RESPONSE);
 * if (actualChallenge == null) {
 *     out.println("You must provide a value for the challenge string");
 * 	   giveTryAgainLink(out);
 *     return;
 * }
 * if (!actualChallenge.equals(expectedChallenge)) {
 *     out.println("Sorry, you didn't pass the anti-Turing test :-)");
 *     giveTryAgainLink(out);
 *     return;
 * }
 * </pre>
 */
public class HumanResponseServlet extends HttpServlet {

	public static final String SESSION_KEY_RESPONSE = "c.d.s.RESPONSE_STRING";
	public static final String SESSION_KEY_TIMESTAMP = "c.d.s.RESPONSE_TIME";
	/** An arbitrary key name used to find the value of the tmp folder in the
	 * virtual fileystem.
	 */
	public final static String LOCAL_TMP_KEY = "c.d.s.LOCAL_TMP_PATH";
	/** The default path in the virtual filesystem for temp files.
	 * Might not want it in the public /tmp folder for security reasons?
	 */
	public final static String DFLT_LOCAL_TMP_DIR = "/hrtmp";
	File dirForTmpFiles;
	private static final long serialVersionUID = -101972891L;
	private static final int NUM_CHARS = 7;
	static final int H = 100;
	static final int W = 400;
	String tmpDir;

	private JigglyTextImageWriter jiggler;

	@Override
	public void init(ServletConfig cfg) throws ServletException {
		super.init(cfg);
		System.out.println("HumanResponseServlet.init()");
		jiggler = new JigglyTextImageWriter(new Font("SansSerif", Font.BOLD, 24), W, H); // XXX initparams
		tmpDir = getInitParameter(LOCAL_TMP_KEY);
		if (tmpDir == null) {
			tmpDir = DFLT_LOCAL_TMP_DIR;
		}
		final String realPath = cfg.getServletContext().getRealPath(tmpDir);
		if (realPath == null) {
			throw new ExceptionInInitializerError("getRealPath failed for " + tmpDir);
		}
		dirForTmpFiles = new File(realPath);
		dirForTmpFiles.mkdirs();	// Ignore return, it probably exists
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		final HttpSession session = request.getSession();

		// create the random string
		final String challenge = PassPhrase.getNext(NUM_CHARS);

		// save it in the session
		session.setAttribute(SESSION_KEY_RESPONSE, challenge);

		// And the timestamp
		session.setAttribute(SESSION_KEY_TIMESTAMP, System.currentTimeMillis());

		
        final File tempFile = File.createTempFile("challenge", ".jpg", dirForTmpFiles);

		// Generate the image
		OutputStream os = null;
		try {
			os = new FileOutputStream(tempFile);

			jiggler.write(challenge, os);
		} finally {
			if (os != null) {
				os.close();
			}
		}

		// If that didn't throw an exception, print an IMG tag
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.printf("<img src='%s/%s' width='%d' height='%d' alt='image to read for human verification'>%n",
				tmpDir,
				tempFile.getName(), W, H);
		out.flush();
	}

	/**
	 * Return true iff the user entered the correct string.
	 * Designed to be called from the target servlet,
	 * just to encapsulate the logic for this all in one place.
	 * @param session The HttpSession
	 * @param input The user's input, to match against what's saved in 'session'
	 * @return True if the user input matches what's in the session.
	 */
	public boolean isValidString(HttpSession session, String input) {
		return input.equals(session.getAttribute(SESSION_KEY_RESPONSE));
	}
}
