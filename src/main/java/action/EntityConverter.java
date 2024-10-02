package action;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

/**
 * In-memory JSF generic EntityConverter
 * @author "Makky" in a comment on http://good-helper.blogspot.ca/2013/10/generic-entity-converter-in-jsf-2-and.html
 * Warning: will explode memory if used on too many large objects!
 */
@FacesConverter(value = "entityConverter")
public class EntityConverter implements Converter {

	private static Map<Object,String> entities = new HashMap<>();

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object entity) {
		synchronized (entities) {
			if (!entities.containsKey(entity)) {
				String uuid = UUID.randomUUID().toString();
				entities.put(entity, uuid);
				return uuid;
			} else {
				return entities.get(entity);
			}
		}
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String uuid) {
		for (Entry<Object, String> entry : entities.entrySet()) {
			if (entry.getValue().equals(uuid)) {
				return entry.getKey();
			}
		}
		return null;
	}

}