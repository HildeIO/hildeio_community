package com.hildeio.models;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.Getter;
import lombok.Setter;

/***********************************************************************************************
 * Model IoSchaltaktorModel
 ***********************************************************************************************/
@Getter
@Setter
@Schema(description = "Model-Klasse Schaltaktor")
public class IoSchaltaktorModel extends HmDataModel {
	
	/***********************************************************************************************
	 * Channel-ID des Schaltaktors
	 ***********************************************************************************************/
	@Schema(description = "Channel-ID des Schaltaktors", example = "58429")
	private String iseId;
	
	/***********************************************************************************************
	 * Channel-Name des Schaltaktors
	 ***********************************************************************************************/
	@Schema(description = "Channel-Name des Schaltaktors", example= "Licht_Wohnzimmer")
	private String name;
	
	/***********************************************************************************************
	 * HomeMatic-Geraetetyp
	 ***********************************************************************************************/
	@SerializedName("interface")
	@Schema(description = "HomeMatic-Gerätetyp", example = "BidCos-RF | HmIP-RF")
	private String interfaceType;
	
	/***********************************************************************************************
	 * Channel-Adresse des Schaltaktors
	 ***********************************************************************************************/
	@Schema(description = "Channel-Adresse des Schaltaktors", example = "000A2D88909C16:1")
	private String address;

	/***********************************************************************************************
	 * Datapoint-Name des Schaltaktors (3. Kanal bei HmIP-RF)
	 ***********************************************************************************************/
	@Schema(description = "Datapoint-Name des Schaltaktors (3. Kanal bei HmIP-RF)", example = "STATE")
	private String valueKey;
	
	/***********************************************************************************************
	 * Datentyp des Datapoint-Name
	 ***********************************************************************************************/
	@Schema(description = "Datentyp des Datapoint-Name", example = "string (bei BidCos-RF) | boolean (bei HmIP-RF)")
	private String type;
	
	/***********************************************************************************************
	 * Schaltaktorstatus
	 ***********************************************************************************************/
	@Schema(description = "Schaltaktorstatus", example= "true (= eingeschaltet) | false (= ausgeschaltet)")
	private String value;
	
	/***********************************************************************************************
	 * Anzeigename
	 ***********************************************************************************************/
	@Schema(accessMode = AccessMode.READ_ONLY)
	private String displayName;
	
	/***********************************************************************************************
	 * Anzeigereihenfolge
	 ***********************************************************************************************/
	@Schema(accessMode = AccessMode.READ_ONLY)
	private String order;		
}