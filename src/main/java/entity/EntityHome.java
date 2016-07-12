package entity;

import java.io.Serializable;

import javax.annotation.PreDestroy;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

/**
 * Implements Gateway, an Adam Bien pattern whose purpose is to expose
 * an Entity (and its relations) to the Client/Web tier, rather like a Seam2 "Home Object"
 * Contains methods to manipulate one entity. Typical usage:
 * <pre>
 * // A Stateful EJB
 * //@Stateful @Named @ConversationScoped // Commented for JavaDoc
 * public class CustomerHome extends EntityHome&lt;Customer, Long&gt; {
 *
 *  // Annotate as Override
 *  public Customer newInstance() {
 * 		return new Customer(); // doing any initialization/customization
 *  }
 * }
 * </pre>
 * @author Ian Darwin
 * @param <T> The type of the JPA Entity we want to manipulate.
 * @param <PK> The type of the JPA Entity's primary key
 */
public abstract class EntityHome<T extends Object, PK extends Object> implements Serializable {

	private static final long serialVersionUID = 4599034282117375142L;

	@PersistenceContext(type=PersistenceContextType.EXTENDED) protected EntityManager em;

	private static final String FORCE_REDIRECT = "?faces-redirect=true";
	
	@Inject Conversation conv;

	protected T instance = newInstance();
	protected Class<? extends T> entityClass;
	protected PK id;
	protected Class<?> pkClass;
	
	@SuppressWarnings("unchecked")
	protected EntityHome() {
		System.out.println("EntityHome.EntityHome()");
		entityClass = (Class<? extends T>) instance.getClass();
	}

	protected PK getId() {
		return id;
	}
	protected void setId(PK id) {
		this.id = id;
	}
	
	public abstract T newInstance();
	
	public void create() {
		// Nothing to do, instance is pre-created
	}

	public T getInstance() {
		return instance;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void wire() {
		System.out.println("Wire(): " + id);
		if (conv.isTransient()) {
			conv.begin();
		}
		if (id == null) {
			instance = newInstance();
			return;
		}
		instance = (T) em.find(entityClass, id);
		if (instance == null) {
			throw new IllegalArgumentException("Entity not found by id! " + id);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void wire(PK id) {
		System.out.println("EntityHome.wire(" + id + ")");
		setId(id);
		wire();
	}

	/** The C of CRUD - create a new T in the database
	 * @param entity - the object to be saved
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String persist(T entity) {
		System.out.println("MemberHome.save()");
		em.persist(entity);
		conv.end();
		return getListPage() + FORCE_REDIRECT;
	}

	/** The R of CRUD - Download a T by primary key
	 * @param id The primary key of the entity to find
	 * @return The found entity
	 */
	public T find(long id2) {		
		return (T) em.find(entityClass, id2);
	}
	
	/** The U of CRUD - update an Entity
	 * @param entity The entity to update
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String update(T entity) {
		System.out.println("MemberHome.update()");
		em.merge(instance);
		return getListPage() + FORCE_REDIRECT;
	}

	/** Update the current Entity */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String update() {
		return update(getInstance());
	}
	
	/** The D of CRUD - delete an Entity. Use with care!
	 * @param entity The entity to delete
	 */
	public String remove(T entity) {
		em.remove(entity);
		conv.end();
		return getListPage() + FORCE_REDIRECT;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String remove() {
		em.remove(instance);
		conv.end();
		return getListPage() + FORCE_REDIRECT;
	}

	/** Close an editing operation: just end conversation, return List page.
	 * @return The List Page
	 */
	public String cancel() {
		conv.end();
		return getListPage() + FORCE_REDIRECT;
	}
	
	/** Like Cancel but for e.g., View page, no conv end.
	 * @return The List Page
	 */
	public String done() {
		return getListPage() + FORCE_REDIRECT;
	}

	@PreDestroy
	public void bfn() {
		//conv.end();
		System.out.println("MemberHome.bfn()");
	}
	/** Used in some places to get the list page to go to after editing;
	 * should normally be overridden.
	 * @return The name of the list page for this Entity.
	 */
	public String getListPage() {
		return "/";
	}
}
