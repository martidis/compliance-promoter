package com.tasosmartidis.compliancebar.features.iam;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<UserEntity, Long> {

	UserEntity findUserEntityByUsernameIgnoreCase(String username);

}
