package com.ractoc.tutorials.speedment.service;

public class ServiceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    public ServiceException(String msg) {
        super(msg);
    }

    ServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
