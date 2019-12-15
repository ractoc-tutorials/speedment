package com.ractoc.tutorials.speedment.configuration;

import ch.vorburger.exec.ManagedProcessException;

class UnableToInitializeDatabaseException extends RuntimeException {

	private static final long serialVersionUID = -270096547353636378L;

	UnableToInitializeDatabaseException(ManagedProcessException e) {
		super(e);
	}

}
