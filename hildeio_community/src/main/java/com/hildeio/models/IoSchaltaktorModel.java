package com.hildeio.models;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Model-Klasse Schaltaktor")
public class IoSchaltaktorModel extends HmDataModel {

	@Schema(description = "Channel-ID des Schaltaktors", example = "58429")
	private String iseId;
	
	@Schema(description = "Channel-Name des Schaltaktors", example= "Licht_Wohnzimmer")
	private String name;
	
	@SerializedName("interface")
	@Schema(description = "HomeMatic-Gerätetyp", example = "BidCos-RF | HmIP-RF")
	private String interfaceType;

	@Schema(description = "Channel-Adresse des Schaltaktors", example = "000A2D88909C16:1")
	private String address;
	
	@Schema(description = "Datapoint-Name des Schaltaktors (3. Kanal bei HmIP-RF)", example = "STATE")
	private String valueKey;
	
	@Schema(description = "Datentyp des Datapoint-Name", example = "string (bei BidCos-RF) | boolean (bei HmIP-RF)")
	private String type;
	
	@Schema(description = "Schaltaktorstatus", example= "true (= eingeschaltet) | false (= ausgeschaltet)")
	private String value;
	
	@Schema(accessMode = AccessMode.READ_ONLY)
	private String displayName;

	@Schema(accessMode = AccessMode.READ_ONLY)
	private String order;		
}

