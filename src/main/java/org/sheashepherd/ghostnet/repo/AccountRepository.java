package org.sheashepherd.ghostnet.repo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.sheashepherd.ghostnet.model.Account;

@ApplicationScoped
public class AccountRepository {

	@Inject
	EntityManager em;

	public Account findByLastName(String lastName) {
		if (lastName == null)
			return null;
		return em.createQuery("""
				  SELECT a FROM Account a
				   WHERE LOWER(a.lastName) = LOWER(:ln)
				""", Account.class).setParameter("ln", lastName.trim()).getResultStream().findFirst().orElse(null);
	}

	public boolean existsLastName(String lastName) {
		if (lastName == null)
			return false;
		Long c = em.createQuery("""
				  SELECT COUNT(a) FROM Account a
				   WHERE LOWER(a.lastName) = LOWER(:ln)
				""", Long.class).setParameter("ln", lastName.trim()).getSingleResult();
		return c != null && c > 0;
	}

	public Account save(Account a) {
		EntityTransaction tx = em.getTransaction();
		boolean newTx = !tx.isActive();
		try {
			if (newTx)
				tx.begin();
			if (a.getId() == null)
				em.persist(a);
			else
				a = em.merge(a);
			em.flush();
			if (newTx)
				tx.commit();
			return a;
		} catch (RuntimeException e) {
			if (newTx && tx.isActive())
				tx.rollback();
			throw e;
		}
	}

	// optional zum Testen
	public long countAll() {
		return em.createQuery("SELECT COUNT(a) FROM Account a", Long.class).getSingleResult();
	}
}
