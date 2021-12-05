package com.tasosmartidis.mycmdb.core.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ErrorResponse {
	private String message;
}
