package entity;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

/**
 * Implements Gateway, an Adam Bien pattern whose purpose is to expose
 * an Entity (and its relations) to the Client/Web tier; also
 * patterned loosely after the Seam2 Entity Framework.
 * Contains * methods to manipulate one entity. Typical usage:
 * <pre>
 * @Named(name="customerHome")
 * public class CustomerHome extends EntityHome<Customer, Long> {
 *
 *  @Override
 *  public Customer newInstance() {
 * 		return new Customer(); // doing any initialization/customization
 *  }
 * }
 * </pre>
 * @author Ian Darwin
 * @param <T> The type of the JPA Entity we want to manipulate.
 * @param <PK> The type of the JPA Entity's primary key
 */
public abstract class EntityHome<T extends Object, PK extends Object> 
	implements Serializable {

	private static final long serialVersionUID = -1L;

	@PersistenceContext(type=PersistenceContextType.EXTENDED)
	protected EntityManager entityManager;

	@Inject Conversation conv;

	protected T instance = newInstance();
	protected Class<? extends T> entityClass;
	protected PK pk;
	protected Class<?> pkClass;
	
	protected EntityHome() {
		System.out.println("EntityHome.EntityHome()");
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
        System.out.println("Wire(): " + pk);
        if (pk == null) {
            instance = newInstance();
            return;
        }
        instance = (T) entityManager.find(entityClass, pk);
        if (instance == null) {
            throw new IllegalArgumentException("Entity not found by id! " + pk);
        }
    }
    public void wire(PK id) {
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
		return (T) entityManager.find(entityClass, pk);
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
