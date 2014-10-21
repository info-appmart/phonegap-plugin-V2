# Appmartアプリ内課金: PhoneGap plugin V2 (CordovaPlugin継承)

![last-version](http://img.shields.io/badge/last%20version-1.1-green.svg "last version:1.1") 

![license apache 2.0](http://img.shields.io/badge/license-apache%202.0-brightgreen.svg "licence apache 2.0")


---

## 目次

```
1- 導入手順

	- [appmart-inbilling-as-project]をインポート
	- プラグインファイル作成
	- [config.xml]変更
	- 実装

```

## 導入手順


#### [appmart-inbilling-as-project]をインポート

[https://github.com/info-appmart/appmart-inbilling-as-project](https://github.com/info-appmart/appmart-inbilling-as-project)　をダウンロードし、workspaceでデプロイしてください。

> 導入手順は [こちら](https://github.com/info-appmart/appmart-inbilling-as-project#appmart%E3%82%A2%E3%83%97%E3%83%AA%E5%86%85%E8%AA%B2%E9%87%91-android-project-library)



#### プラグインファイル作成

本プロジェクトの「AppmartPlugin.java」クラスを[src]下に追加してください（パッケージは何でもよい）。

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
```

> パッケージ名だけを反映してくさい。


#### [config.xml]変更

[project-root/res/xml/config]下のconfig.xmlに下記コードを追記

```xml
<!-- appmart plugin -->
    <feature name="AppmartPlugin">
       <param name="android-package" value="com.example.appmart_phonegap.AppmartPlugin" />
	</feature>
```
> パッケージ名を合わせてください


#### 実装

pluginの導入は完了になりました。jsで実装しましょう：

##### 成功時のcallbackを用意

```js
// success callback
var successCallback = function(successJson) { 
		//決済IDを取得
		var settlementId = successJson["transactionId"];
		alert("決済が確定されました。決済ID: " + settlementId);     			
	};
```

##### 失敗時のcallbackを用意

```js
// error callback
var errorCallback = function(errorJson) {
	var code 	= errorJson["error_code"];	        	
	alert("エラーが発生しました。 " + code); 
};
```

##### pluginの呼び出し

```js
// 決済処理
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

> Cordova(phoneGap)のexecメッソードでプラグインを呼びだす。

##### HTML

```html
<button onclick="do_settlement(this, 'your-service_id')" value="settlement">Settlement</button>
```

##### HTML/JS サンプル

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
     			//決済IDを取得
     			var settlementId = successJson["transactionId"];
     			alert("決済が確定されました。決済ID: " + settlementId);     			
	       	};
	        
	        // error callback
	        var errorCallback = function(errorJson) {
	        	var code 	= errorJson["error_code"];	        	
	        	alert("エラーが発生しました。 " + code); 
	        };
	        
	        
	        // 決済処理
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
