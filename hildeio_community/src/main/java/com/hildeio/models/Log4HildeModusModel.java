package com.hildeio.models;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "log4hildeModusModel")
public class Log4HildeModusModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String modusName;
	
	private Boolean modusValue;

}
