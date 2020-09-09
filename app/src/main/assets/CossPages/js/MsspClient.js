// JavaScript Document

function request(sp ,invoke ,myJSONText)
  {
	// 1 android 2 ios
	try {
		window.coss.cossrequest(sp,invoke,myJSONText);
	} catch(Excep) {
		 window.location.href = "#/coss?" + invoke + ":?" + myJSONText;
	}

}

function activeUser(){

	 var pin1="";
	 var pin2="";
	 if(document.getElementById("userpin1")&&document.getElementById("userpin2")){
		 pin1=document.getElementById("userpin1").value;
		 pin2=document.getElementById("userpin2").value;

		 if(checkIpt(pin1)==false||checkIpt(pin2)==false){
			 request('coss','alertWarnig','请输入数字或字母口令');
			 return;
		 }
		 if(pin1!=pin2){
			 request('coss','alertWarnig','口令输入不一致');
			 return;
		 }
	 }

	 request('coss','activeUser',pin1);
	
}

function oldUserCheck(el){
    el.setAttribute('disabled', '');
    setTimeout(function(){
        el.removeAttribute('disabled');
    }, 1500)
	var selectType;
	var IDtype = document.getElementById("selectView").value;
	var paramObj=new Object();
	paramObj.name=document.getElementById("name").value;
	paramObj.type = IDtype;
	paramObj.idCardNumber=document.getElementById("paperid").value;
	request('coss','userActiveDevice',JSON.stringify(paramObj));

}


function getIDType(IDType) {
//	var IDType = '{"idcardType":[{"type":"SF","description":"身份证"},{"type":"HZ","description":"护照"}]}';
	var sel= document.getElementById("selectView");
	if(IDType == "" || IDType == 'undefined' || IDType == null){
		sel.options.add(new Option("身份证","SF"));
		idTypeObjs[0] = '{"type":"SF","description":"身份证"}';
	}else{
		var str = IDType.substring(15,IDType.length-2);
		idTypeObjs = str.split("},");
		for(var i=0;i<idTypeObjs.length-1;i++){
			idTypeObjs[i] +="}"
		}
		for(var i=0;i<idTypeObjs.length;i++){
			var idTypeObj=JSON.parse(idTypeObjs[i]);
			sel.options.add(new Option(idTypeObj.description,idTypeObj.type));
		}
	}
}


/**
 * 取消按钮
 */
function signSettingBack(){
	request('coss','signSettingBack','');
}

