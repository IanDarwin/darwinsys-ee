package jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

public class JsfUtil {
	
	public static final String FORCE_REDIRECT = "?faces-redirect=true";

	/** Add a JSF FacesMessage with the given text, not associated with any given UI control.
	 * @param message The text to be displayed via JSF
	 */
	public static void addMessage(String message) {
		addMessage(null, message);
	}

	/** Add a constructed JSF FacesMessage, not associated with any given UI control.
	 * @param message The text to be displayed via JSF
	 */
	public static void addMessage(FacesMessage message) {
		addMessage(null, message);
	}
	
	/** Add a JSF FacesMessage with the given text, associated with the named UI control.
	 * @param controlName The name of the JSF control that caused the message (may be null).
	 * @param message The text to be displayed via JSF
	 */
	public static void addMessage(String controlName, String message) {
		addMessage(controlName, new FacesMessage(message));
	}

	/** Add a constructed JSF FacesMessage, associated with the named UI control.
	 * @param controlName The name of the JSF control that caused the message (may be null).
	 * @param message The text to be displayed via JSF
	 */
	public static void addMessage(String controlName, FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(controlName, message);
	}
}
