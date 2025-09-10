package org.sheashepherd.ghostnet.repo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.*;
import java.util.List;
import java.util.function.Function;

import org.sheashepherd.ghostnet.model.Person;

@ApplicationScoped
public class PersonRepository {

	@Inject
	EntityManagerFactory emf;

	private <T> T inTx(Function<EntityManager, T> work) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			T result = work.apply(em);
			tx.commit();
			return result;
		} catch (RuntimeException e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	private <T> T withEm(Function<EntityManager, T> work) {
		EntityManager em = emf.createEntityManager();
		try {
			return work.apply(em);
		} finally {
			em.close();
		}
	}

	public Person findOrCreate(String name, String phone) {
		String n = (name == null || name.isBlank()) ? null : name.trim();
		String p = (phone == null || phone.isBlank()) ? null : phone.trim();
		if (n == null && p == null)
			return null;

		List<Person> existing = withEm(
				em -> em.createQuery(
						"select p from Person p "
								+ "where p.name = :n and ((p.phone is null and :ph is null) or p.phone = :ph)",
						Person.class).setParameter("n", n).setParameter("ph", p).setMaxResults(1).getResultList());

		if (!existing.isEmpty())
			return existing.get(0);

		return inTx(em -> {
			Person per = new Person();
			per.setName(n);
			per.setPhone(p);
			em.persist(per);
			return per;
		});
	}
}
