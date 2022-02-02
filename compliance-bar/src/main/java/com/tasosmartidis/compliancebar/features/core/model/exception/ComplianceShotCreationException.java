package com.tasosmartidis.compliancebar.features.core.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ComplianceShotCreationException extends RuntimeException {
	private static final long serialVersionUID = -1470952696838462536L;

	public ComplianceShotCreationException(String message) {
		super(message);
	}
}
