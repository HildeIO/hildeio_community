package com.hildeio.models;

import lombok.*;

@Getter
@Setter
public class IoWochenplanModel {

	private String iseId;	
	
	private String wochentag;
	
	private String von;

	private String bis;
	
	private String temperaturSoll;
}
