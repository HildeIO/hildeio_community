package com.hildeio.models;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Log4HildePushNotificationModel {

	private int returnCode;
	
	private String messageId;
	
	private JSONObject objekte;
	
	private Exception exception;
}
