package com.tasosmartidis.compliancebar.features.core.repository;

import com.tasosmartidis.compliancebar.features.core.model.entity.ComplianceShotConfigurationItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplianceShotConfigurationItemRepository extends JpaRepository<ComplianceShotConfigurationItemEntity, Long> {

	@Override
	@NonNull
	ComplianceShotConfigurationItemEntity save(@NonNull ComplianceShotConfigurationItemEntity complianceShotConfigurationItemEntity);



}
