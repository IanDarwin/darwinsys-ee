package entity;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;

public class EntityHomeTest {

	@Test
    @Disabled // ParameterizedType stuff is crap
	public void testGetClassStuff() {

		class Demo {
		};

		class Home extends EntityHome<Demo,Long> {
			public Home() {
				ParameterizedType t = 
						(ParameterizedType) EntityHome.class.getGenericSuperclass(); //OtherClass<Entity>
					entityClass = (Class<? extends Demo>) t.getActualTypeArguments()[0]; // Class<Entity>
					System.out.println("entityClass = " + entityClass.getName());
					pkClass = (Class<?>) t.getActualTypeArguments()[1]; // Class<PK>
					System.out.println("pkClass = " + pkClass.getName());

			}
			public Demo newInstance() {
				return new Demo();
			}
		};

		Home home = new Home();
		System.out.println(home);
		// If we get here, the EntityHome constructor didn't throw any exceptions
	}
}
