package com.tasosmartidis.compliancebar.features.core.model.view;

import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class ConfigurationItemsView implements Viewable {
	private Set<ConfigurationItem> configurationItems;
}
