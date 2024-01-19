package com.hildeio.homematic;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hildeio.Log4Hilde;
import com.hildeio.models.HmDataModel;
import com.hildeio.models.HmLoginRequestParamsModel;
import com.hildeio.models.HmLogoutRequestParamsModel;
import com.hildeio.models.HmRequestModel;
import com.hildeio.models.HmResponseModel;

public class HmEventManager {

	private Log4Hilde log4Hilde;
	
	/* *********************************************************************************************
	*
	* HmControllerInterface
	* 
	* ********************************************************************************************/			
	public HmEventManager(Log4Hilde log4Hilde) {
		
		this.log4Hilde = log4Hilde;
	}
	
	
	/* *********************************************************************************************
	*
	* doLogin
	* 
	* ********************************************************************************************/		
	private String doLogin(String eventId) {
		
		try {
			
			HmConfiguration config = new HmConfiguration();
			
			HmLoginRequestParamsModel hmSessionRequestParamsModel = new HmLoginRequestParamsModel();
			hmSessionRequestParamsModel.setUsername(config.getUsername());
			hmSessionRequestParamsModel.setPassword(config.getPassword());
			
			this.log4Hilde.doInfoLog(
					"01-HmLoginRequestParamsModel", 
					new ObjectMapper().writeValueAsString(hmSessionRequestParamsModel), 
					new JSONObject(),
					eventId
					);
			
			
			HmRequestModel hmRequestModel = new HmRequestModel();
			hmRequestModel.setMethod(config.getRfcLogin());
			hmRequestModel.setParams(hmSessionRequestParamsModel);
		
			this.log4Hilde.doInfoLog(
					"02-hmRequestModel", 
					new ObjectMapper().writeValueAsString(hmRequestModel), 
					new JSONObject(),
					eventId
					);
			
			
			HmResponseModel hmResponseModel = HmJsonRpcClientAsync.doRequest(hmRequestModel, this.log4Hilde, eventId);
			
			this.log4Hilde.doInfoLog(
					"03-hmResponseModel", 
					new ObjectMapper().writeValueAsString(hmResponseModel), 
					new JSONObject(),
					eventId
					);
			
			if(hmResponseModel.getResult() == null) {
				
				this.log4Hilde.doInfoLog(
						"04-kein-Login", 
						"_session_id_ ist null", 
						new JSONObject(),
						eventId
						);
				
				return null;
			} 
			
			return hmResponseModel.getResult();
			
		} catch(Exception exception) {
			
			this.log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
			return null;
		}		
	}	
	
	
	/* *********************************************************************************************
	*
	* save
	* 
	* ********************************************************************************************/		
	public void save(String hmRfcMethod, HmDataModel hmDataModel, String eventId) {
		
		String _session_id_ = null;
		
		try {
			
			_session_id_ = this.doLogin(eventId);		
			if(_session_id_ != null) {
				
				this.log4Hilde.doInfoLog(
						"01-_session_id", 
						_session_id_, 
						new JSONObject(),
						eventId
						);
				
			} else {
				
				this.log4Hilde.doInfoLog(
						"02-keine-_session_id", 
						"_session_id_ ist null", 
						new JSONObject(),
						eventId
						);
				
				return;			
			}
			
			hmDataModel.set_session_id_(_session_id_);
			
			HmRequestModel hmRequestModel = new HmRequestModel();
			hmRequestModel.setMethod(hmRfcMethod);
			hmRequestModel.setParams(hmDataModel);
			
			this.log4Hilde.doInfoLog(
					"03-hmRequestModel", 
					new ObjectMapper().writeValueAsString(hmRequestModel), 
					new JSONObject(),
					eventId
					);
			
			HmResponseModel hmResponseModel = HmJsonRpcClientAsync.doRequest(hmRequestModel, this.log4Hilde, eventId);
			
			this.log4Hilde.doInfoLog(
					"04-hmResponseModel", 
					new ObjectMapper().writeValueAsString(hmResponseModel), 
					new JSONObject(),
					eventId
					);
						
			return;
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
			return;
			
		} finally {
			
			try {
				
				if(_session_id_ != null) {
					
					this.log4Hilde.doInfoLog(
							"05-Logout", 
							"_session_id: " + _session_id_, 
							new JSONObject(),
							eventId
							);
					
					this.doLogout(_session_id_, eventId);
					
				} else {
					
					this.log4Hilde.doInfoLog(
							"06-kein-Logout", 
							"_session_id_ ist null", 
							new JSONObject(),
							eventId
							);
				}
			
			} catch(Exception exception) {
				
				log4Hilde.doErrorLog("EX-02", exception, new JSONObject(), eventId);			
			}			
		}						
	}
	

	/* *********************************************************************************************
	*
	* doLogout
	* 
	* ********************************************************************************************/		
	private void doLogout(String _session_id_, String eventId) {
		
		try {
			
			if(_session_id_ == null) {
				
				this.log4Hilde.doInfoLog(
						"00-kein-Logout", 
						"_session_id_ ist null", 
						new JSONObject(),
						eventId
						);
				
				return;
			}
				
			HmLogoutRequestParamsModel hmLogoutRequestParamsModel = new HmLogoutRequestParamsModel();
			hmLogoutRequestParamsModel.set_session_id_(_session_id_);
			
			this.log4Hilde.doInfoLog(
					"01-hmLogoutRequestParamsModel", 
					new ObjectMapper().writeValueAsString(hmLogoutRequestParamsModel), 
					new JSONObject(),
					eventId
					);

			
			HmRequestModel hmRequestModel = new HmRequestModel();
			hmRequestModel.setMethod("Session.logout");
			hmRequestModel.setParams(hmLogoutRequestParamsModel);
		
			this.log4Hilde.doInfoLog(
					"02-hmRequestModel", 
					new ObjectMapper().writeValueAsString(hmRequestModel), 
					new JSONObject(),
					eventId
					);
			
			HmResponseModel hmResponseModel = HmJsonRpcClientAsync.doRequest(hmRequestModel, this.log4Hilde, eventId);
			
			this.log4Hilde.doInfoLog(
					"03-hmResponseModel", 
					new ObjectMapper().writeValueAsString(hmResponseModel), 
					new JSONObject(),
					eventId
					);						
			
			if(hmResponseModel.getResult() == null) {
				
				this.log4Hilde.doInfoLog(
						"04-kein-Logout", 
						"_session_id_ ist null", 
						new JSONObject(),
						eventId
						);
			} 
			
		} catch(Exception exception) {
			
			this.log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
		}				
	}
}
