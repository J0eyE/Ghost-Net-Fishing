package org.sheashepherd.ghostnet.config;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class JpaResources {

	private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ghostnetPU");

	@Produces
	@ApplicationScoped
	public EntityManagerFactory produceEmf() {
		return emf;
	}

	@PreDestroy
	void close() {
		if (emf != null && emf.isOpen())
			emf.close();
	}
}
