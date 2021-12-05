package com.tasosmartidis.mycmdb.core.model.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TooManyRequestsException extends RuntimeException {
	private static final long serialVersionUID = 7481120970759731814L;

	private String exceptionMessage;
}
