package com.tasosmartidis.mycmdb.core.model.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NonExistentConfigurationItemException extends RuntimeException {
	private static final long serialVersionUID = -6737409218955861404L;

	private String exceptionMessage;

}
