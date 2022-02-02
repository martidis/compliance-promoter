package com.tasosmartidis.compliancebar.features.core.repository;

import com.tasosmartidis.compliancebar.features.core.model.entity.ConfigurationItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigurationItemsRepository extends JpaRepository<ConfigurationItemEntity, String> {


	ConfigurationItemEntity findConfigurationItemEntityById(@NonNull String id);

	@Query("SELECT ci.systemOwner, count(ci.systemOwner) FROM CONFIGURATION_ITEMS ci GROUP BY ci.systemOwner")
	List<String> getUniqueSystemOwners();


}
