package com.tasosmartidis.compliancebar.features.core.repository;

import com.tasosmartidis.compliancebar.features.core.model.entity.ComplianceShotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplianceShotsRepository extends JpaRepository<ComplianceShotEntity, String> {

	@Override
	@NonNull
	ComplianceShotEntity save(@NonNull ComplianceShotEntity configurationItem);

	@Override
	List<ComplianceShotEntity> findAll();

	ComplianceShotEntity findComplianceShotEntityById(@NonNull String id);

	long countComplianceShotEntityByTitle(@NonNull String title);


}
