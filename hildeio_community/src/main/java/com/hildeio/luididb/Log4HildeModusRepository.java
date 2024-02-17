package com.hildeio.luididb;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hildeio.models.Log4HildeModusModel;

/***********************************************************************************************
 * 
 * Interface Log4HildeModusRepository
 *    
 ***********************************************************************************************/
@Repository
public interface Log4HildeModusRepository extends CrudRepository<Log4HildeModusModel, Long> {
	
	/***********************************************************************************************
	 * 
	 * Ermittlung ob checkDebugMode in Tabelle [luidi_db].[log4hilde_modus_model] gesetzt ist.
	 * 
	 * @param debugMode 'exceptionPush' | 'debug'
	 * @return Datensatz gefunden | nicht gefunden
	 *  
	 ***********************************************************************************************/	
	Optional<Log4HildeModusModel> findByModusName(String debugMode);
}