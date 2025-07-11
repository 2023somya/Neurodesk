package com.metacube.serviceCatalog.exception;


// to handle service layer error more genuinely
public class ServiceCatalogException extends RuntimeException{
	public ServiceCatalogException (String message) {
		super(message);
	}

}
