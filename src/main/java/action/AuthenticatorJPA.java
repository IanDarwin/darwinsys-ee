package action;

import java.util.Date;
import java.util.Set;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.Person;
import model.PersonRole;

/**
 * A simple JSF-based Authenticator, inspired by the one in Seam2
 */
@Stateful
@SessionScoped
@Named("authenticator")
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AuthenticatorJPA implements Authenticator {

	private static final int TIMEOUT_MINUTES = 240;
	private static final int SEC_PER_MIN = 60;

	@PersistenceContext EntityManager em;

	private Person person;

	private String userName;
	private String password;
	private boolean loggedIn;
	private Set<PersonRole> roles;

	/**
	 * The all-important login method!
	 */
	public String login() {
		try {
			person = (Person) em.createQuery(
				"from Person p where p.loginName = ?1 and p.passPhrase = ?2"/* , Person.class*/).
				setParameter(1, userName).
				setParameter(2, password).
				getSingleResult();
			// getSingleResult() will throw an exception if not found.
			// So we're almost good to go; save the logged-in user
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) 
					context.getExternalContext().getRequest();
			request.getSession().setAttribute("loggedInUser", person);
			
			person.setLastLogin(new Date());

			// Extend session timeout
			FacesContext facesContext = FacesContext.getCurrentInstance();
			if (facesContext != null) {
				ExternalContext externalContext = facesContext.getExternalContext();
				if (externalContext != null) {
					HttpSession httpSession = ((HttpSession) externalContext.getSession(true));
					if (httpSession != null) {
						httpSession.setMaxInactiveInterval(TIMEOUT_MINUTES * SEC_PER_MIN);
					}
				}
			}
			
			roles = person.getRoles();
			
			loggedIn = true;
			return "index";
		} catch (Exception e) {
			System.err.println("Login failed: " + e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Login failed."));
			loggedIn = false;
			return "login_error";
		}
	}

	@Remove
	public void logout() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) 
				context.getExternalContext().getRequest();
		try {
			// No need to remove loggedInUser from the session,
			// we're about to destroy the entire Session:
			request.getSession().invalidate();
			loggedIn = false;
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage("Logout failed."));
		}
	}
	
	public boolean isInRole(String roleName) {
		for (PersonRole role : roles) {
			if (role.getName().equals(roleName)) {
				return true;
			}
		}
		return false;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Person getLoggedInUser() {
		return person;
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
