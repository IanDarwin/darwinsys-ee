package action;

/**
 * A simple Authenticator api, inspired by the one in Seam2
 */
public interface Authenticator {

	public String login();
	public void logout();

	public String getUserName();
	public void setUserName(String username);

	public String getPassword();
	public void setPassword(String password);

	public boolean isLoggedIn();
	
	public boolean hasRole(String role);
}
