package com.ractoc.tutorials.speedment.service;

public class NoSuchEntryException extends ServiceException {
	private static final long serialVersionUID = 1L;

	public NoSuchEntryException(String msg) {
		super(msg);
	}
}
