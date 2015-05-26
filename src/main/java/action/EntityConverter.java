package action;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Id;

/**
 * The Missing Generic Entity Converter for JSF2
 * Note: Assumes that the given entity has a pk of type Long
 * @author Originally based on code at
 * http://good-helper.blogspot.ca/2013/10/generic-entity-converter-in-jsf-2-and.html
 */
@FacesConverter("entityConverter")
public class EntityConverter implements Converter, Serializable {

	private static final long serialVersionUID = 6596576590941959208L;
	@Inject
	private EntityManager em;
	
	public EntityConverter() {
		System.out.println("GenericEntityConverterLong.GenericEntityConverterLong()");
	}

	/**
	 * "Convert the specified string value, which is associated with the specified UIComponent,
	 * into a model data object that is appropriate for being stored
	 * during the Apply Request Values phase of the request 
	 * processing lifecycle." - EE JavaDoc
	 * <br/>
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

		System.out.println("GenericEntityConverterLong.getAsObject(): " + clazzAndId);

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
		System.out.println("GenericEntityConverterLong.getAsString(): " + object);
		if (object == null) {
			return "";	// Required by specification
		}
		try {
			Class<? extends Object> clazz = object.getClass();
			for (Field f : clazz.getDeclaredFields()) {
				if (f.isAnnotationPresent(Id.class)) {
					f.setAccessible(true);
					Long id = (Long) f.get(object);
					return clazz.getCanonicalName() + ":" + id.toString();
				}
			}
			System.err.println("GenericEntityConverterLong.getAsString(): no Id on " + clazz);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			String mesg = "getAsString failed to convert " + object + " (" + e + ")";
			System.out.println(mesg);
			throw new ConverterException(mesg, e);
		}
		return null;
	}
}
