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

�{�v���W�F�N�g�́uAppmartPlugin.java�v�N���X��[src]���ɒǉ����Ă��������i�p�b�P�[�W���͉��ł��悢�j�B


#### [config.xml]�ύX

[project-root/res/xml/config]����config.xml�ɉ��L�R�[�h��ǋL

```xml
<!-- appmart plugin -->
    <feature name="AppmartPlugin">
       <param name="android-package" value="com.example.appmart_phonegap.AppmartPlugin" />
	</feature>
```
> �p�b�P�[�W�������킹�Ă��������B


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
