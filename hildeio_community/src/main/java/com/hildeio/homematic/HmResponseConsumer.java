package com.hildeio.homematic;

import java.io.IOException;
import java.nio.CharBuffer;

import org.apache.http.HttpResponse;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hildeio.Log4Hilde;
import com.hildeio.models.HmResponseModel;

/***********************************************************************************************
 * 
 * Klasse HmResponseConsumer
 *    
 ***********************************************************************************************/
public class HmResponseConsumer extends AsyncCharConsumer<Boolean> {
	  
	
	/***********************************************************************************************
	 * KONSTANTE fuer Response von der HomeMatic CCU.
	 ***********************************************************************************************/	
	private HmResponseModel hmResponseModel;
	
	
	/***********************************************************************************************
	 * Logging-Instanz 
	 ***********************************************************************************************/	
	private Log4Hilde log4Hilde;
	
	
	/***********************************************************************************************
	 * Aktuelle WorkflowId 
	 ***********************************************************************************************/	
	private String eventId;
	
	
	/***********************************************************************************************
	 * 
	 * Konstruktor
	 * 
	 * @param eventId Aktuelle WorkflowId.
	 * @param log4Hilde Aktuelle Logging-Instanz.
	 *    
	 ***********************************************************************************************/	
	public HmResponseConsumer(Log4Hilde log4Hilde, String eventId) {		
		
		this.log4Hilde = log4Hilde;
		this.eventId = eventId;
	}
	
	
	/***********************************************************************************************
	 * 
	 * Zuruecksetzen von response.
	 * 
	 * @param response Response von der HomeMatic CCU.
	 *    
	 ***********************************************************************************************/	
    @Override
    protected void onResponseReceived(final HttpResponse response) {}

    
	/***********************************************************************************************
	 * 
	 * Response wird verarbeitet.
	 * 
	 * @param charBuffer Response-Nachricht von der HomeMatic CCU.
	 * @param ioControl Wird nicht weiter benoetigt.
	 *    
	 ***********************************************************************************************/	
    @Override
    protected void onCharReceived(final CharBuffer charBuffer, final IOControl ioControl)
        throws IOException {

		try {
    	
			this.log4Hilde.doInfoLog(
					"01-charBuffer", 
					String.valueOf(charBuffer), 
					new JSONObject(),
					this.eventId
					);		    					
			
	    	JSONObject response = new JSONObject(String.valueOf(charBuffer));
	    	byte[] responseData = response.toString().getBytes();
	
	    	ObjectMapper mapper = new ObjectMapper();
	    	hmResponseModel = mapper.readValue(responseData, HmResponseModel.class);
	    	
			log4Hilde.doInfoLog(
					"02-hmResponseModel", 
					new ObjectMapper().writeValueAsString(hmResponseModel), 
					new JSONObject(),
					this.eventId
					);		    					
	    	
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), this.eventId);			
		}		    	
    }


	/***********************************************************************************************
	 *
	 * Keine zusaetzliche Aenderung erforderlich.
	 * 
	 ***********************************************************************************************/	
    @Override
    protected void releaseResources() {}


	/***********************************************************************************************
	 *
	 * Keine zusaetzliche Aenderung erforderlich.
	 * 
	 ***********************************************************************************************/	
    @Override
    protected Boolean buildResult(final HttpContext context) {    	
      return Boolean.TRUE;
    }
    
    
	/***********************************************************************************************
	 *
	 * Bereitstellung des Response-Models. 
	 * 
	 * @return hmResponseModel Wird von HmJsonRpcClientAsync aufgerufen. 
	 * 
	 ***********************************************************************************************/	
    public HmResponseModel getHmResponseModel() {
    	return hmResponseModel;
    }
}	
