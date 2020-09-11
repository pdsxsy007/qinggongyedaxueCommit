function request(sp ,invoke ,myJSONText)
  {
	// 1 android 2 ios
	try {
		window.signet.transmit(sp,invoke,myJSONText);
	} catch(Excep) {
		 window.location.href = "#/signet?" + invoke + ":?" + myJSONText;
	}

}


var signImageBase = "";

function getSignImageBase(){
    return signImageBase;
}

function revertScreenCallBack(signImg){
	/*console.log("1");
	console.log("revertScreenCallBack "+signImg);*/
	signImageBase=signImg;
    /*console.log("THIS : "+signImageBase);*/
}