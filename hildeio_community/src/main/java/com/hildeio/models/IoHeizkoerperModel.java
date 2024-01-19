package com.hildeio.models;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Model-Klasse Heizkörpers")
public class IoHeizkoerperModel extends HmDataModel {

	
	@Schema(description = "Channel-ID des 1. Kanals des Heizkörpers", example = "58429")
	private String iseId;
	
	@Schema(description = "Channel-Name des 1. Kanals des Heizkörpers", example= "Heizkörperthermostat_Arbeitszimmer:1")
	private String name;
	
	@Schema(description = "Raumtemperatur", example = "19.5")
	private String temperaturIst;
	
	@SerializedName("value")
	@Schema(description = "Gewünschte Solltemperatur", example = "22.5")
	private String temperaturSoll;
	
	@Schema(description = "Manueller Modus oder Wochenplan", example = "manuell | wochenplan")
	private String betriebsart;

	@SerializedName("interface")
	@Schema(description = "HomeMatic-Gerätetyp", example = "BidCos-RF | HmIP-RF")
	private String interfaceType;

	@Schema(description = "Channel-Adresse des 1. Kanals des Heizkörpers", example = "000A2D88909C16:1")
	private String address;
	
	@Schema(description = "Datapoint-Name des 1. Kanals des Heizkörpers", example = "SET_POINT_TEMPERATURE")
	private String valueKey;
	
	@Schema(description = "Datentyp des Datapoint-Name", example = "double")
	private String type;
	
	@Schema(description = "Aufrufer dieses Requests", example = "MOBILE | HM")
	private String changedBy;	

	@Schema(accessMode = AccessMode.READ_ONLY)
	private String displayName;
}
