package com.hildeio.models;

import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "log4hilde")
public class Log4HildeModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private Timestamp logtime;

	private String klasse;

	private String methode;
	
	private String logpoint;
	
	@Enumerated(EnumType.STRING)
	private Kategorie kategorie;
	
	@Column(length = 50)
	private String eventId;
	
	@Column(length = 80000)
	private String meldung;
	
	private String exception;
	
	private String stackTrace;
		
	@Column(length = 80000)
	private String objekte;

	public enum Kategorie {
	    ERROR, INFO;
	}
	
}
