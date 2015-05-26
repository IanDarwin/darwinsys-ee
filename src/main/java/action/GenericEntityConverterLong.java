package action;

import java.lang.reflect.Field;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Id;

/**
 * The Missing Generic Entity Converter for JSF2
 * @author Adapted from code at
 * http://good-helper.blogspot.ca/2013/10/generic-entity-converter-in-jsf-2-and.html
 */
@FacesConverter("entityConverterLong")
public class GenericEntityConverterLong implements Converter {

	@Inject
	private EntityManager em;

	public Object getAsObject(
		FacesContext fc, 
		UIComponent component,
		String objectNameber) {
		try {
			String[] split = objectNameber.split(":");
			return em.find(Class.forName(split[0]), Long.valueOf(split[1]));
		} catch (NumberFormatException | ClassNotFoundException e) {
			System.err.println("getAsObject() failed for " + objectNameber + "(" + e + ")");
			return null;
		}
	}

	public String getAsString(
		FacesContext fc, 
		UIComponent component,
		Object object) {
		
		try {
			Class<? extends Object> clazz = object.getClass();
			for (Field f : clazz.getDeclaredFields()) {
				if (f.isAnnotationPresent(Id.class)) {
					f.setAccessible(true);
					Long id = (Long) f.get(object);
					return clazz.getCanonicalName() + ":" + id.toString();
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			System.err.println("getAsString failed to convert " + object + " (" + e + ")");
		}
		return null;
	}
}