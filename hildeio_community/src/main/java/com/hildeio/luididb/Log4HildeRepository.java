package com.hildeio.luididb;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hildeio.models.Log4HildeModel;

/***********************************************************************************************
 * Interface Log4HildeRepository
 ***********************************************************************************************/
@Repository
public interface Log4HildeRepository extends CrudRepository<Log4HildeModel, Long> {}