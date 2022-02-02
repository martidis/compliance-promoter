package com.tasosmartidis.compliancebar.features.core.model.api;

import com.tasosmartidis.compliancebar.features.core.model.view.Viewable;
import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Data
public class ConfigurationItemsSelectionForShotRequest implements Serializable, Viewable {
	private static final long serialVersionUID = 6803857194395027332L;

	// we want to post table contents from thymeleaf template. we have to use list cos the mechanism to do it
	// (at least the one I found out) uses indexes so while all items are unique and set would be more appropriate,
	// we have to use list in the request object. in the rest of the applications sets are used so at one point we turn
	// the list into a set and maybe the repeated object creation would affect garbage collection and thus pauses and
	// thus performance. for now we dont worry about it
	private List<ConfigurationItem> applicableConfigurationItems;
	private String complianceShotRequestId;

}
