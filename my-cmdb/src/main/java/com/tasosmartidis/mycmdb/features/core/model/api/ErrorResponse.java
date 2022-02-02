package com.tasosmartidis.mycmdb.features.core.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ErrorResponse {
	private String message;
}
