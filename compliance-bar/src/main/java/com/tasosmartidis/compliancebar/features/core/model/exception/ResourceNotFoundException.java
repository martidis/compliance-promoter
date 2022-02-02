package com.tasosmartidis.compliancebar.features.core.model.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -3366799951452857065L;

	public ResourceNotFoundException(String message) {
		super(message);
	}

}
