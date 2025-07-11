package com.metacube.serviceCatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.metacube.serviceCatalog"})
public class ServiceCatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceCatalogApplication.class, args);
	}

}
