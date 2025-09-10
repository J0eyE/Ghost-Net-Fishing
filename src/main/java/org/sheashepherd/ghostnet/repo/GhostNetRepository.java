package org.sheashepherd.ghostnet.repo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.*;
import java.util.List;
import java.util.function.Function;

import org.sheashepherd.ghostnet.model.GhostNet;
import org.sheashepherd.ghostnet.model.GhostNetStatus;
import org.sheashepherd.ghostnet.model.Person;

@ApplicationScoped
public class GhostNetRepository {

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

	// ---------- WICHTIG: Seite in 2 Schritten laden ----------
	public List<GhostNet> page(int page, int pageSize) {
		return withEm(em -> em
				.createQuery("select g from GhostNet g " + "left join fetch g.salvor "
						+ "left join fetch g.recoveredBy " + "order by g.id desc", GhostNet.class)
				.setFirstResult(page * pageSize).setMaxResults(pageSize).getResultList());
	}

	public long count() {
		return withEm(em -> em.createQuery("select count(g.id) from GhostNet g", Long.class).getSingleResult());
	}

	public GhostNet createReport(double lat, double lon, String size, String reporterName, String reporterPhone) {
		return inTx(em -> {
			GhostNet g = new GhostNet();
			g.setStatus(GhostNetStatus.REPORTED);
			g.setLat(lat);
			g.setLon(lon);
			g.setSize(size);
			g.setReporterName(reporterName);
			g.setReporterPhone(reporterPhone);
			em.persist(g);
			return g;
		});
	}

	public GhostNet reassign(Long id, Person salvor) {
		return inTx(em -> {
			GhostNet g = em.find(GhostNet.class, id, LockModeType.PESSIMISTIC_WRITE);
			if (g == null)
				throw new IllegalArgumentException("GhostNet " + id + " nicht gefunden.");
			g.setSalvor(salvor);
			g.setStatus(GhostNetStatus.CLAIMED);
			return g;
		});
	}

	public GhostNet markMissing(Long id, String missingName, String missingPhone) {
		return inTx(em -> {
			GhostNet g = em.find(GhostNet.class, id, LockModeType.PESSIMISTIC_WRITE);
			if (g == null)
				throw new IllegalArgumentException("GhostNet " + id + " nicht gefunden.");
			g.setMissingReporterName(missingName);
			g.setMissingReporterPhone(missingPhone);
			g.setStatus(GhostNetStatus.MISSING);
			return g;
		});
	}

	public GhostNet markRecovered(Long id, Person recoveredBy) {
		return inTx(em -> {
			GhostNet g = em.find(GhostNet.class, id, LockModeType.PESSIMISTIC_WRITE);
			if (g == null)
				throw new IllegalArgumentException("GhostNet " + id + " nicht gefunden.");
			g.setRecoveredBy(recoveredBy);
			g.setStatus(GhostNetStatus.RECOVERED);
			return g;
		});
	}
}
