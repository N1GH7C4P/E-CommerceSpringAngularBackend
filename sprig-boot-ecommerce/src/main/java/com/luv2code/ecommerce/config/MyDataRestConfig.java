package com.luv2code.ecommerce.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.luv2code.ecommerce.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer{
		@Value("${allowed.origins}")
		private String[] allowedOrigins;
		EntityManager entityManager;

		@Autowired
		public MyDataRestConfig(EntityManager em) {
			entityManager = em;
		}

		@Override
		public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {

			disableHttpMethods(config, Product.class);
			disableHttpMethods(config, ProductCategory.class);
			disableHttpMethods(config, Country.class);
			disableHttpMethods(config, State.class);
			disableHttpMethods(config, Order.class);
			exposeIds(config);
			cors.addMapping(config.getBasePath() + "/**").allowedOrigins(allowedOrigins);
		}

	private void disableHttpMethods(RepositoryRestConfiguration config, Class affectedClass)  {

		HttpMethod[] theUnsupportedActions = {HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PATCH};

		config.getExposureConfiguration()
			.forDomainType(affectedClass)
			.withItemExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions))
			.withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions));
	}

	private void exposeIds(RepositoryRestConfiguration config) {
			Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
			List<Class> entityClasses = new ArrayList<>();
			for(EntityType t : entities) {
				entityClasses.add(t.getJavaType());
			}
			Class[] domainTypes = entityClasses.toArray(new Class[0]);
			config.exposeIdsFor(domainTypes);
		}
}
