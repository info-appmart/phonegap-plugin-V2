# Appmart�A�v�����ۋ�: PhoneGap plugin V2 (CordovaPlugin�p��)

![last-version](http://img.shields.io/badge/last%20version-1.1-green.svg "last version:1.1") 

![license apache 2.0](http://img.shields.io/badge/license-apache%202.0-brightgreen.svg "licence apache 2.0")


---

## �ڎ�

```
1- �����菇

	- [appmart-inbilling-as-project]���C���|�[�g
	- �v���O�C���t�@�C���쐬
	- [config.xml]�ύX
	- ����

```

## �����菇


#### [appmart-inbilling-as-project]���C���|�[�g

[https://github.com/info-appmart/appmart-inbilling-as-project](https://github.com/info-appmart/appmart-inbilling-as-project)�@���_�E�����[�h���Aworkspace�Ńf�v���C���Ă��������B

> �����菇�� [������](https://github.com/info-appmart/appmart-inbilling-as-project#appmart%E3%82%A2%E3%83%97%E3%83%AA%E5%86%85%E8%AA%B2%E9%87%91-android-project-library)



#### �v���O�C���t�@�C���쐬

�{�v���W�F�N�g�́uAppmartPlugin.java�v�N���X��[src]���ɒǉ����Ă��������i�p�b�P�[�W�͉��ł��悢�j�B

```java
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
	
	// Action��
	public static final String SETTLEMENT_METHOD = "do_settlement";
	public static final String CONF_SETTLEMENT_METHOD = "conf_settlement";
	
	// parameters
	public static final String DEV_ID 		= "devId";
	public static final String LICENCEN_KEY = "licenceKey";
	public static final String PUBLIC_KEY 	= "publicKey";
	public static final String APP_ID 		= "appId";
	public static final String SERVICE_ID 	= "serviceId";
	
	// json�@parameters
	public static final String TRANSACTION_ID 	= "transactionId";
	public static final String ERROR_CODE 		= "error_code";	
	public static final int VALIDATION_ERROR 	= -99 ;
	
	//���̑�
	public Context mContext;
	public AppmartInBilling plugin;
	public AppmartResultInterface callback;
		
	
    /*
     * PhoneGap����Ăяo�����method 
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
            	
    	// �R���e�L�X�g�擾
    	mContext = cordova.getActivity().getApplicationContext();
    	
    	if (action.equals(SETTLEMENT_METHOD)) {	//����
    		
    		JSONObject obj = new JSONObject(args.getString(0));
    		
    		// ���Ϗ����擾
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
     * ���ώ��smethod
     */
    private void doSettlement(final CallbackContext callbackContext,String devId,String licenceKey,String publicKey,String appId,String serviceId) {
    	
    	
    	// �ʃX���b�h�Ō��ς����s���邽�߁Acallback��p��
    	callback = new AppmartResultInterface(){
			
			//����ID
			String transactionId;

			/*
			 * ���ς����s
			 */
			@Override
			public void settlementError(int errorCode) {				
				try {
					//�G���[����json��
					JSONObject message = new JSONObject();						
					message.put(ERROR_CODE, errorCode);
					callbackContext.error(message);
				}catch(Exception e){
					Log.e("AppmartPlugin","��O���������܂����B" + e.getMessage());
					e.printStackTrace();
				}
			}
			
			
			/*
			 * ���ϓo�^��
			 */
			@Override
			public void settlementWaitValidation(String transactionId) {
				try {					
					this.transactionId = transactionId;				
					plugin.confirmSettlement();					
				} catch (Exception e) {
					Log.e("AppmartPlugin","��O���������܂����B" + e.getMessage());
					e.printStackTrace();
				}				
			}


			/*
			 * ���ς��m���
			 */
			@Override
			public void settlementValidated(boolean result) {
				try {					
					//�����܂Ƃ߂āAcallback
					JSONObject message = new JSONObject();	
					if(result){									
						message.put(TRANSACTION_ID, transactionId);					
						callbackContext.success(message);	
					}else{					
						message.put(ERROR_CODE, VALIDATION_ERROR);
						callbackContext.error(message);
					}				
				} catch (JSONException e) {
					Log.e("AppmartPlugin","json�G���[���������܂����B" + e.getMessage());
				} catch(Exception e ){
					Log.e("AppmartPlugin","��O���������܂����B" + e.getMessage());
				}
			}
			
		};		    		
		
		// ���ϊJ�n
		plugin = AppmartInBilling.getInstance(mContext); 		
		plugin.setParameters(devId, licenceKey, publicKey, appId);
		plugin.doSettlement(serviceId, callback);
		
	}
    
}
```

> �p�b�P�[�W�������𔽉f���Ă������B


#### [config.xml]�ύX

[project-root/res/xml/config]����config.xml�ɉ��L�R�[�h��ǋL

```xml
<!-- appmart plugin -->
    <feature name="AppmartPlugin">
       <param name="android-package" value="com.example.appmart_phonegap.AppmartPlugin" />
	</feature>
```
> �p�b�P�[�W�������킹�Ă�������


#### ����

plugin�̓����͊����ɂȂ�܂����Bjs�Ŏ������܂��傤�F

##### ��������callback��p��

```js
// success callback
var successCallback = function(successJson) { 
		//����ID���擾
		var settlementId = successJson["transactionId"];
		alert("���ς��m�肳��܂����B����ID: " + settlementId);     			
	};
```

##### ���s����callback��p��

```js
// error callback
var errorCallback = function(errorJson) {
	var code 	= errorJson["error_code"];	        	
	alert("�G���[���������܂����B " + code); 
};
```

##### plugin�̌Ăяo��

```js
// ���Ϗ���
function do_settlement(obj, itemId){
	        	
	//appmart plugin
	cordova.exec(
        successCallback, errorCallback, 
        'AppmartPlugin', 'do_settlement', 
        [{                 
            "devId": "your-developer-id",
            "licenceKey": "your-licence-key",
            "publicKey": "your-public-key",
            "appId": "your-app-id",
            "serviceId": service_id
        }]
	); 	            	
}        
```

> Cordova(phoneGap)��exec���b�\�[�h�Ńv���O�C�����Ăт����B

##### HTML

```html
<button onclick="do_settlement(this, 'your-service_id')" value="settlement">Settlement</button>
```

##### HTML/JS �T���v��

```html
<!DOCTYPE html>

<html>
    <head>
        <meta charset="utf-8" />
        <meta name="format-detection" content="telephone=no" />
        <meta name="msapplication-tap-highlight" content="no" />
                
        <script type="text/javascript" src="cordova.js"></script>
		<script type="text/javascript" src="cordova_plugins.js"></script>
		<script type="text/javascript" src="js/index.js"></script>
        
        
        <script type="text/javascript">
       		
	       	// success callback
	        var successCallback = function(successJson) { 
     			//����ID���擾
     			var settlementId = successJson["transactionId"];
     			alert("���ς��m�肳��܂����B����ID: " + settlementId);     			
	       	};
	        
	        // error callback
	        var errorCallback = function(errorJson) {
	        	var code 	= errorJson["error_code"];	        	
	        	alert("�G���[���������܂����B " + code); 
	        };
	        
	        
	        // ���Ϗ���
	        function do_settlement(obj, service_id){
	        	        	
	        	//appmart plugin
	        	cordova.exec(
		            successCallback, errorCallback, 
		            'AppmartPlugin', 'do_settlement', 
		            [{                 
		                "devId": "your-developer-id",
		                "licenceKey": "your-licence-key",
		                "publicKey": "your-public-key",
		                "appId": "your-app-id",
		                "serviceId": service_id
		            }]
	        	); 	            	
	        }         
        </script>
      
    </head>
    
    
    <body>    
    	<button onclick="do_settlement(this, 'your-service_id')" value="settlement">Settlement</button>
    </body>
</html>

```
