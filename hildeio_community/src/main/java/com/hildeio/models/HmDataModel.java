package com.hildeio.models;

import com.google.cloud.firestore.annotation.Exclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/***********************************************************************************************
 * 
 * Model HmDataModel
 *    
 ***********************************************************************************************/
@Getter
@Setter
public class HmDataModel {


	/***********************************************************************************************
	 * Session-ID aus der HomeMatic CCU
	 ***********************************************************************************************/
	@Exclude
	@Schema(description = "Session-ID aus der HomeMatic CCU", example = "5B1yXc80E3")
	private String _session_id_;
}
