package com.tasosmartidis.mycmdb.iam;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientsRepository extends CrudRepository<ClientEntity, Long> {


	@Query("SELECT c FROM clients c WHERE c.username = :username")
	ClientEntity getClientsByUsername(@Param("username") String username);

}
