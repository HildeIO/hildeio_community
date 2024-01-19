package com.hildeio.luididb;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hildeio.models.Log4HildeModel;


@Repository
public interface Log4HildeRepository extends CrudRepository<Log4HildeModel, Long> {}