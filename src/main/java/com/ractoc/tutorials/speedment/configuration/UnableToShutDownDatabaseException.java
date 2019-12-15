package com.ractoc.tutorials.speedment.configuration;

import ch.vorburger.exec.ManagedProcessException;

class UnableToShutDownDatabaseException extends RuntimeException {

	private static final long serialVersionUID = -270096547353636378L;

	UnableToShutDownDatabaseException(ManagedProcessException e) {
		super(e);
	}

}
