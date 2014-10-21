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

本プロジェクトの「AppmartPlugin.java」クラスを[src]下に追加してください（パッケージ名は何でもよい）。


#### [config.xml]変更

[project-root/res/xml/config]下のconfig.xmlに下記コードを追記

```xml
<!-- appmart plugin -->
    <feature name="AppmartPlugin">
       <param name="android-package" value="com.example.appmart_phonegap.AppmartPlugin" />
	</feature>
```
> パッケージ名を合わせてください。


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
