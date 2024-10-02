package auth;

import jakarta.ejb.Stateful;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import model.Person;

/**
 * A simple JSF-based Authenticator, inspired by the one in Seam2
 * and some code posted for public use at
 * http://docs.oracle.com/javaee/6/tutorial/doc/glxce.html
 */
@Stateful
@SessionScoped
@Named
public class AuthenticatorCMS implements Authenticator<Person> {
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

	/**
	 * The equally-important logout method
	 */
	public String logout() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) 
				context.getExternalContext().getRequest();
		try {
			request.logout();
			loggedIn = false;
		} catch (ServletException e) {
			context.addMessage(null, new FacesMessage("Logout failed."));
		}
		return "/";
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
	public Person getLoggedInUser() {
		final Person person = new Person();
		person.setLoginName(username);
		return person;
	}

	@Override
	public boolean hasRole(String role) {
		return false;
	}
}
