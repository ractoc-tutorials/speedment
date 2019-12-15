package com.ractoc.tutorials.speedment.service;

public class DuplicateEntryException extends ServiceException {
	private static final long serialVersionUID = 1L;

	public DuplicateEntryException(String msg) {
		super(msg);
	}
}
