package action;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Id;

/**
 * The Missing Generic JPA Entity Converter for JSF2
 * Note: Assumes that the given entity has a pk of type Long
 * @author Originally based on incomplete code at
 * http://good-helper.blogspot.ca/2013/10/generic-entity-converter-in-jsf-2-and.html
 */
@FacesConverter("entityConverter")
public class EntityConverter implements Converter, Serializable {

	private static final long serialVersionUID = 6596576590941959208L;
	@Inject
	private EntityManager em;
	
	public EntityConverter() {
		// Required empty constructor
	}

	/**
	 * "Convert the specified string value, which is associated with the specified UIComponent,
	 * into a model data object that is appropriate for being stored
	 * during the Apply Request Values phase of the request 
	 * processing lifecycle." - EE JavaDoc
	 * <br>
	 * IOW, JSF calls us with a string like "package.Entity:123"
	 * (which we generated in getAsString, below).
	 * @param fc The FacesContext
	 * @param component The UI component needing the converted result
	 * @param clazzAndId The clazzname:pkId string
	 * @return The re-created object
	 */
	@Override
	public Object getAsObject(
		FacesContext fc, 
		UIComponent component,
		String clazzAndId) {

		System.out.println("EntityConverter.getAsObject(): " + clazzAndId);

		if (clazzAndId == null) {
			return null;
		}
		try {
			String[] split = clazzAndId.split(":");
			return em.find(Class.forName(split[0]), Long.valueOf(split[1]));
		} catch (NumberFormatException | ClassNotFoundException e) {
			String mesg = "getAsObject() failed for " + clazzAndId + "(" + e + ")";
			System.out.println(mesg);
			throw new ConverterException(mesg, e);
		}
	}

	/**
	 * Convert the specified model object value, which is 
	 * associated with the specified UIComponent, into a String 
	 * that is suitable for being included in the response 
	 * generated during the Render Response phase of the 
	 * request processing lifeycle." -- EE JavaDoc
	 * IOW, JSF calls us with an object, we want to represent it
	 * as a string like "package.Entity:123"
	 * @param fc The FacesContext
	 * @param component The UI component needing the converted result
	 * @param object The object to be represented as aclazzname:pkId string
	 * @return The clazzname:pkId string
	 */
	@Override
	public String getAsString(
		FacesContext fc, 
		UIComponent component,
		Object object) {
		System.out.println("EntityConverter.getAsString(): " + object);
		if (object == null) {
			return "";	// Required by specification
		}
		try {
			Class<? extends Object> clazz = object.getClass();
			// Look for field annotation
			for (Field f : clazz.getDeclaredFields()) {
				if (f.isAnnotationPresent(Id.class)) {
					f.setAccessible(true);
					Long id = (Long) f.get(object);
					return clazz.getCanonicalName() + ":" + id.toString();
				}
			}
			// Meh. Look for method annotation.
			for (Method m : clazz.getDeclaredMethods()) {
				if (m.isAnnotationPresent(Id.class)) {
					m.setAccessible(true);
					Long id = (Long) m.invoke(object, new Object[0]);
					return clazz.getCanonicalName() + ":" + id.toString();
				}
			}
			// Sigh. No joy.
			final String mesg = "EntityConverter.getAsString(): no Id on " + clazz;
			System.err.println(mesg);
			throw new ConverterException(mesg);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			String mesg = "getAsString failed to convert " + object + " (" + e + ")";
			System.err.println(mesg);
			throw new ConverterException(mesg, e);
		}
	}
}
