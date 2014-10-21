package com.example.appmart_phonegap;


import jp.app_mart.AppmartInBilling;
import jp.app_mart.AppmartResultInterface;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class AppmartPlugin extends CordovaPlugin {
	
	// Action名
	public static final String SETTLEMENT_METHOD = "do_settlement";
	public static final String CONF_SETTLEMENT_METHOD = "conf_settlement";
	
	// parameters
	public static final String DEV_ID 		= "devId";
	public static final String LICENCEN_KEY = "licenceKey";
	public static final String PUBLIC_KEY 	= "publicKey";
	public static final String APP_ID 		= "appId";
	public static final String SERVICE_ID 	= "serviceId";
	
	// json　parameters
	public static final String TRANSACTION_ID 	= "transactionId";
	public static final String ERROR_CODE 		= "error_code";	
	public static final int VALIDATION_ERROR 	= -99 ;
	
	//その他
	public Context mContext;
	public AppmartInBilling plugin;
	public AppmartResultInterface callback;
		
	
    /*
     * PhoneGapから呼び出されるmethod 
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
            	
    	// コンテキスト取得
    	mContext = cordova.getActivity().getApplicationContext();
    	
    	if (action.equals(SETTLEMENT_METHOD)) {	//決済
    		
    		JSONObject obj = new JSONObject(args.getString(0));
    		
    		// 決済情報を取得
    		String devId		= obj.getString(DEV_ID);
    		String licenceKey	= obj.getString(LICENCEN_KEY);
    		String publicKey	= obj.getString(PUBLIC_KEY);
    		String appId		= obj.getString(APP_ID);
    		String serviceId 	= obj.getString(SERVICE_ID);
    		
            this.doSettlement(callbackContext,devId,licenceKey,publicKey,appId,serviceId);
            return true;        
    	}
    	
        return false;
    }

    
    /*
     * 決済実行method
     */
    private void doSettlement(final CallbackContext callbackContext,String devId,String licenceKey,String publicKey,String appId,String serviceId) {
    	
    	
    	// 別スレッドで決済を実行するため、callbackを用意
    	callback = new AppmartResultInterface(){
			
			//決済ID
			String transactionId;

			/*
			 * 決済が失敗
			 */
			@Override
			public void settlementError(int errorCode) {				
				try {
					//エラー情報をjsonに
					JSONObject message = new JSONObject();						
					message.put(ERROR_CODE, errorCode);
					callbackContext.error(message);
				}catch(Exception e){
					Log.e("AppmartPlugin","例外が発生しました。" + e.getMessage());
					e.printStackTrace();
				}
			}
			
			
			/*
			 * 決済登録後
			 */
			@Override
			public void settlementWaitValidation(String transactionId) {
				try {					
					this.transactionId = transactionId;				
					plugin.confirmSettlement();					
				} catch (Exception e) {
					Log.e("AppmartPlugin","例外が発生しました。" + e.getMessage());
					e.printStackTrace();
				}				
			}


			/*
			 * 決済が確定後
			 */
			@Override
			public void settlementValidated(boolean result) {
				try {					
					//情報をまとめて、callback
					JSONObject message = new JSONObject();	
					if(result){									
						message.put(TRANSACTION_ID, transactionId);					
						callbackContext.success(message);	
					}else{					
						message.put(ERROR_CODE, VALIDATION_ERROR);
						callbackContext.error(message);
					}				
				} catch (JSONException e) {
					Log.e("AppmartPlugin","jsonエラーが発生しました。" + e.getMessage());
				} catch(Exception e ){
					Log.e("AppmartPlugin","例外が発生しました。" + e.getMessage());
				}
			}
			
		};		    		
		
		// 決済開始
		plugin = AppmartInBilling.getInstance(mContext); 		
		plugin.setParameters(devId, licenceKey, publicKey, appId);
		plugin.doSettlement(serviceId, callback);
		
	}
    
}