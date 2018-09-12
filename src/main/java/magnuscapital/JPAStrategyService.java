/**
 * @file JPAStrategyService.java
 * @Author from PersonDemo project provided by Harry
 * @date July 25, 2018
 * @brief Service class for Strategies
 *  
 */

package magnuscapital;

import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import exceptions.ConflictException;
import exceptions.NotFoundByNumberException;
import exceptions.NotFoundException;

public class JPAStrategyService {
	private final static Logger LOGGER = Logger.getLogger(JPAStrategyService.class.getName());
	private FileHandler fh;
	private EntityManagerFactory emf;
	private String getAllQuery;
	private String getCountQuery;
	private String getMaxQuery;
	private static String GET_BY_NUMBER = "select i from Strategy i where i.number = ?1";

	public JPAStrategyService() {
		
	}
	
	public JPAStrategyService(EntityManagerFactory emf) {
		this.emf = emf;
		getAllQuery = "select x from Strategy x";
		getCountQuery = "select count(x) from Strategy x";
		getMaxQuery = "select max(x.id) from Strategy x";
		
		try {
			fh = new FileHandler("JPAStrategyService.log");
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		LOGGER.setUseParentHandlers(false);
		LOGGER.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
		LOGGER.info("strategy service instantiated");
	}

	/**
	 * Helper to reject operations on entities that don't yet exist.
	 */
	protected Strategy findOrFail(EntityManager em, Strategy entity) {
		Object ID = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
		Strategy result = em.find(Strategy.class, ID);
		if (result == null) {
			LOGGER.info("Strategy with ID " + entity.getId() + " NOT FOUND.");
			throw new NotFoundException(Strategy.class, ID);
		}
		return result;
	}

	/**
	 * Returns the total number of objects.
	 */
	public long getCount() {
		EntityManager em = emf.createEntityManager();
		long result = -1;
		try {
			em.getTransaction().begin();
			result = em.createQuery(getCountQuery, Long.class).getSingleResult();
			em.getTransaction().commit();
		} catch (PersistenceException ex) {
			LOGGER.log(Level.SEVERE, "unable to get total count", ex);
			ex.printStackTrace();
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
		} finally {
			em.close();
		}
		return result;
	}
	
	/**
	 * Returns the maximum id assigned to object
	 */
	public int getMaxID() {
		EntityManager em =  emf.createEntityManager();
		int result = -1;
		try {
			em.getTransaction().begin();
			result = em.createQuery(getMaxQuery,Integer.class).getSingleResult();
		} catch (PersistenceException ex) {
			LOGGER.log(Level.SEVERE, "unable to find maxID", ex);
			ex.printStackTrace();
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
		} finally {
			em.close();
		}
		return result;
	}
	
	/**
	 * Returns a list of all entities of our managed type.
	 */
	@SuppressWarnings("unchecked")
	public List<Strategy> getAll() {
		EntityManager em = emf.createEntityManager();
		List<Strategy> result = null;
		try {
			em.getTransaction().begin();
			result = em.createQuery(getAllQuery).getResultList();
			em.getTransaction().commit();
		} catch (PersistenceException ex) {
			LOGGER.log(Level.SEVERE, "unable to get list of strategies", ex);
			ex.printStackTrace();
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
		} finally {
			em.close();
		}
		return result;
	}

	/**
	 * Returns the entity with the given ID, or null if not found.
	 */
	public Strategy getByID(int ID) throws NotFoundException {
		EntityManager em = emf.createEntityManager();
		Strategy result = null;
		try {
			em.getTransaction().begin();
			result = em.find(Strategy.class, ID);
			em.getTransaction().commit();
		} catch (PersistenceException ex) {
			LOGGER.log(Level.WARNING, "unable to get strategy by id", ex);
			ex.printStackTrace();
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
		} finally {
			em.close();
		}
		if (result == null)
			throw new NotFoundException(Strategy.class, ID);
		return result;
	}

	/**
	 * Adds the given object to the database, with a generated ID that is
	 * guaranteed to be unique.
	 */
	public Strategy add(Strategy newObject) {
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(newObject);
			em.getTransaction().commit();
		} catch (PersistenceException ex) {
			LOGGER.log(Level.WARNING, "unable to add strategy id = " + newObject.getId(), ex);
			ex.printStackTrace();
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			throw new PersistenceException();
		} finally {
			em.close();
		}
		return newObject;
	}

	/**
	 * Merges the given object into the database.
	 */
	public Strategy update(Strategy modifiedObject) throws NotFoundException {
		EntityManager em = emf.createEntityManager();
		Strategy result = null;
		try {
			em.getTransaction().begin();
			findOrFail(em, modifiedObject);
			result = em.merge(modifiedObject);
			em.getTransaction().commit();
		} catch (PersistenceException ex) {
			LOGGER.log(Level.WARNING, "unable to update strategy by id", ex);
			ex.printStackTrace();
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
		} finally {
			em.close();
		}
		return result;
	}

	/**
	 * Removes the given object from the database.
	 */
	public boolean remove(Strategy oldObject) throws NotFoundException {
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			em.remove(findOrFail(em, oldObject));
			em.getTransaction().commit();
			return true;
		} catch (PersistenceException ex) {
			LOGGER.log(Level.WARNING, "unable to remove strategy by strategy", ex);
			ex.printStackTrace();
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			return false;
		} finally {
			em.close();
		}
	}

	/**
	 * Removes the object with the given ID from the database.
	 */
	public boolean removeByID(int ID) throws NotFoundException {
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			Strategy doomed = em.find(Strategy.class, ID);
			if (doomed == null) {
				LOGGER.info("can't remove by Id, id = " + ID);
				throw new NotFoundException(Strategy.class, ID);
			}
			em.remove(doomed);
			em.getTransaction().commit();
			return true;
		} catch (PersistenceException ex) {
			LOGGER.log(Level.WARNING, "unable to remove strategy by id", ex);
			ex.printStackTrace();
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			return false;
		} finally {
			em.close();
		}
	}


}
