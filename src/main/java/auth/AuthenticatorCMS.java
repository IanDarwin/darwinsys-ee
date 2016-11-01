package auth;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * A simple JSF-based Authenticator, inspired by the one in Seam2
 * and some code posted for public use at
 * http://docs.oracle.com/javaee/6/tutorial/doc/glxce.html
 */
@Stateful
@SessionScoped
@Named
public class AuthenticatorCMS implements Authenticator {
	private String username;
	private String password;
	private boolean loggedIn;

	/**
	 * The all-important login method!
	 */
	public String login() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) 
				context.getExternalContext().getRequest();
		try {
			request.login(this.username, this.password);
			loggedIn = true;
			return "index";
		} catch (ServletException e) {
			context.addMessage(null, new FacesMessage("Login failed."));
			loggedIn = false;
			return "login_error";
		}
	}

	public void logout() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) 
				context.getExternalContext().getRequest();
		try {
			request.logout();
			loggedIn = false;
		} catch (ServletException e) {
			context.addMessage(null, new FacesMessage("Logout failed."));
		}
	}

	public String getUserName() {
		return this.username;
	}

	public void setUserName(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isLoggedIn() {
		return this.loggedIn;
	}

	@Override
	public boolean hasRole(String role) {
		// TODO Auto-generated method stub
		return false;
	}
}
