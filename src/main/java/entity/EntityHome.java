package entity;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Patterned loosely after the Seam2 Entity Framework; contains
 * methods to manipulate one entity. Typical usage:
 * <pre>
 * @Named(name="frameworkHome")
 * public class FrameworkHome extends EntityHome<Framework> {
 *
 *  @Override
 *  public Framework newInstance() {
 * 		return new Framework(); // doing any initialization/customization
 *  }
 * }
 * </pre>
 * @author Ian Darwin
 * @param <T> The type of the JPA Entity we want to manipulate.
 */
public abstract class EntityHome<T extends Object, PK extends Object> 
	implements Serializable {

	private static final long serialVersionUID = -1L;

	@PersistenceContext(type=PersistenceContextType.EXTENDED)
	protected EntityManager entityManager;

	@Inject Conversation conv;

	protected T instance = newInstance();
	protected PK pk;
	
	protected EntityHome() {
	}

	public abstract T newInstance();

	protected void setId(PK pk) {
		this.pk = pk;
	}
	protected PK getId() {
		return pk;
	}

	public T getInstance() {
		return instance;
	}

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void wire() {
        if (conv.isTransient()) {
            conv.begin();
        }
        System.out.println("Wire(): " + id);
        if (id == null) {
            instance = new Person();
            return;
        }
        instance = em.find(Person.class, id);
        if (instance == null) {
            throw new IllegalArgumentException("Person not found by id! " + id);
        }
    }
    public void wire(Long id) {
        System.out.println("PersonHome.wire(" + id + ")");
        setId(id);
        wire();
    }

	
	/** The C of CRUD - create a new T in the database */
	public void persist(T entity) {
		entityManager.persist(entity);
	}

	/** The R of CRUD - Download a T by primary key */
	public T find(long id) {		
		return entityManager.find(entityClass, id);
	}
	
	/** The U of CRUD - update an Entity */
	public void update(T entity) {
		// Nothing to do here - if the Entity is persistent, changes to
		// it will be persisted by the EntityManager automagically.
	}
	
	/** The D of CRUD - delete an Entity. Use with care! */
	public void delete(T entity) {
		entityManager.remove(entity);
	}
}
