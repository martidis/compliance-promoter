package com.tasosmartidis.compliancebar.features.core.model.exception;

import lombok.Getter;

@Getter
public class ApplicationInitializationException extends RuntimeException {
	private static final long serialVersionUID = -1470952696838462536L;

	public ApplicationInitializationException(String message) {
		super(message);
	}
}
