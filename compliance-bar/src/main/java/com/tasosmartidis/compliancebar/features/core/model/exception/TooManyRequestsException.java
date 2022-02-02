package com.tasosmartidis.compliancebar.features.core.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TooManyRequestsException extends RuntimeException {
	private static final long serialVersionUID = 7481120970759731814L;

	private String exceptionMessage;

	private long tryAgainInMilliseconds;
}
