package com.hildeio.models;

import lombok.Getter;
import lombok.Setter;

/***********************************************************************************************
 * 
 * Model HmResponseErrorModel
 *    
 ***********************************************************************************************/
@Getter
@Setter
public class HmResponseErrorModel {

	
	/***********************************************************************************************
	 * JSON-RPC Fehler von der HomeMatic CCU
	 ***********************************************************************************************/
	private String name;

	
	/***********************************************************************************************
	 * JSON-RPC Fehlernummer von der HomeMatic CCU
	 ***********************************************************************************************/
	private String code;
	
	
	/***********************************************************************************************
	 * Fehlertext von der HomeMatic CCU
	 ***********************************************************************************************/
	private String message; 
}
