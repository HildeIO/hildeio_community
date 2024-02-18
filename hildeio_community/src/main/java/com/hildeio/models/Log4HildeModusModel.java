package com.hildeio.models;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

/***********************************************************************************************
 * Model Log4HildeModusModel
 ***********************************************************************************************/
@Setter
@Getter
@Entity
@Table(name = "log4hildeModusModel")
public class Log4HildeModusModel {
	
	/***********************************************************************************************
	 * eindeutige Id  in der Tabelle [luidi_db].[log4hilde_modus_model]
	 ***********************************************************************************************/
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/***********************************************************************************************
	 * Variablenname
	 ***********************************************************************************************/
	private String modusName;
	
	/***********************************************************************************************
	 * Variablenwert
	 ***********************************************************************************************/
	private Boolean modusValue;
}