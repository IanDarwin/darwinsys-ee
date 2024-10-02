package auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * Delegating HTTPServletRequest that just overrides a few methods such as getRemoteUser(), since
 * we are handling our own login...
 */
public class LoginFilterHttpServletRequest extends HttpServletRequestWrapper {

	private String userName;

	public LoginFilterHttpServletRequest(HttpServletRequest request, String userName) {
		super(request);
		this.userName = userName;
	}

	@Override
	public String getRemoteUser() {
		return this.userName;
	}
}
