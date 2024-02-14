package com.hildeio.models;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/***********************************************************************************************
 * 
 * Model Log4HildePushNotificationModel
 *    
 ***********************************************************************************************/
@Setter
@Getter
public class Log4HildePushNotificationModel {

	
	/***********************************************************************************************
	 * returnCode der FCM PushNotification
	 ***********************************************************************************************/
	private int returnCode;

	
	/***********************************************************************************************
	 * messageId der FCM PushNotification
	 ***********************************************************************************************/
	private String messageId;

	
	/***********************************************************************************************
	 * enthaltene Objekt in der FCM PushNotification
	 ***********************************************************************************************/
	private JSONObject objekte;

	
	/***********************************************************************************************
	 * Exception in der FCM PushNotification
	 ***********************************************************************************************/
	private Exception exception;
}
