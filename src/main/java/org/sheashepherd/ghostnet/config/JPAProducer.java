package org.sheashepherd.ghostnet.config;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

@ApplicationScoped
public class JPAProducer {

	private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ghostnetPU");

	@Produces
	@RequestScoped
	public EntityManager produceEntityManager() {
		return emf.createEntityManager();
	}

	public void closeEntityManager(@Disposes EntityManager em) {
		if (em != null && em.isOpen()) {
			em.close();
		}
	}

	@PreDestroy
	public void shutdown() {
		if (emf != null && emf.isOpen()) {
			emf.close();
		}

		try {
			Enumeration<Driver> drivers = DriverManager.getDrivers();
			while (drivers.hasMoreElements()) {
				Driver d = drivers.nextElement();
				try {
					DriverManager.deregisterDriver(d);
				} catch (SQLException ignore) {
				}
			}
		} catch (Exception ignore) {
		}
	}
}
