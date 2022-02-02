package com.tasosmartidis.compliancebar.features.core.model.view;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class ProgressDetails {
	private int cancelled;
	private int inProgress;
	private int completed;
}
