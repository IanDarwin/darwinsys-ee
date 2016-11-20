package jsf;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class JsfUtil {
	
	public static final String FORCE_REDIRECT = "?faces-redirect=true";

	/** Add a JSF FacesMessage with the given text, not associated with any given UI control.
	 * @param mesg The text to be displayed via JSF
	 */
	public static void addMessage(String mesg) {
		addMessage(null, mesg);
	}
	
	/** Add a JSF FacesMessage with the given text, associated with the given UI control.
	 * @param mesg The text to be displayed via JSF
	 */
	public static void addMessage(String controlName, String mesg) {
		FacesContext.getCurrentInstance().addMessage(controlName,
			new FacesMessage(mesg));
	}
}
