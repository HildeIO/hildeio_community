package com.hildeio.luididb;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hildeio.models.Log4HildeModusModel;

@Repository
public interface Log4HildeModusRepository extends CrudRepository<Log4HildeModusModel, Long> {
	
	Optional<Log4HildeModusModel> findByModusName(String debugMode);
}