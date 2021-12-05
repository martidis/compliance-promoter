package com.tasosmartidis.mycmdb.core.repository;

import com.tasosmartidis.mycmdb.core.model.entity.ConfigurationItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "ciRepo")
public interface ConfigurationItemsRepository extends JpaRepository<ConfigurationItemEntity, Long> {

	@Override
	@NonNull
	ConfigurationItemEntity save(@NonNull ConfigurationItemEntity configurationItem);

	@Query("select ci from CONFIGURATION_ITEMS ci where ci.availabilityRating >= :minAvailability and " +
			"ci.integrityRating >= :minIntegrity and ci.confidentialityRating >= :minConfidentiality")
	List<ConfigurationItemEntity> findConfigurationEntities(Integer minAvailability, Integer minIntegrity,
															Integer minConfidentiality);


	// While not causing a problem functionally, the next two methods cause an exception
	// in class JpaQueryLookupStrategy.resolveQuery because they are not annotated with
	// explicit query here or named query in the entity. From the name convention and due to the simplicity
	// of the query neither query nor named query are required. If you have free time look deeper into thi
	ConfigurationItemEntity findConfigurationItemEntityById(@NonNull String id);

	void deleteById(@NonNull String id);

}
