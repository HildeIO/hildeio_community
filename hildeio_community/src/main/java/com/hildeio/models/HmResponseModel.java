package com.hildeio.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HmResponseModel {

	private String version;
	
	private String result;
	
	private HmResponseErrorModel error; 
}

