package entity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class EntityQuery {

	public static Class<?> getEntityClass(Object inquirer) {
		// We need to find the actual entity type. Since there are likely to be
		// proxies implemented by subclassing in some EE environments,
		// look for the "genericSuperclass()" in super-super... classes,
		// stopping only if we find one with types OR get to Object.
		System.out.println("EntityHome.EntityHome()");
		Class<?> mc = inquirer.getClass();
		Type clazzType = null;
		do {
			clazzType = mc.getGenericSuperclass();
			mc = mc.getSuperclass();
		} while (!(clazzType instanceof ParameterizedType) && clazzType != Object.class);
		
		if (clazzType == Object.class) {
			String mesg = String.format("Sorry, %s not instanceof ParameterizedType", inquirer.getClass());
			throw new ExceptionInInitializerError(mesg);
		}
		final Type type = ((ParameterizedType)clazzType).getActualTypeArguments()[0];
		System.out.println("TYPE = " + type);
		return (Class<?>)type; // first = Class for 'T'
	}
}
