//验证输入框
var panduanArray = new Array(false, false, false);

var isName = false;
var isIDCard = false;
var isIDDate = false;

var ischange = false;
var selectText = false;

function checkIpt(val){
    //var reg =  /^(?=.*?[a-z)(?=.*>[A-Z])(?=.*?[0-9])[a-zA_Z0-9]{6,12}$/
	var reg = /^[0-9a-zA-Z]{6,12}$/
    if (!reg.test(val)) {
        return false;
    }
}

//验证码
function sdkPanduanCode(n, lengths) {
	if(((String($(".sdk_input"+n).val()).indexOf(" ")!=-1))||((String($(".sdk_input"+n).val()).indexOf("　")!=-1))){
		$(".sdk_ts"+n).hide();
		$(".zm_sdk_ts"+n).hide();
		$(".kg_sdk_ts"+n).show();
	}else{
		$(".kg_sdk_ts"+n).hide();
		if($(".sdk_input" + n).val().length == lengths) {
			$(".sdk_ts" + n).hide();
			$(".zm_sdk_ts" + n).hide();
			panduanArray[n] = true;
		} else if($(".sdk_input" + n).val().length == 0) {
			$(".sdk_ts" + n).hide();
			$(".zm_sdk_ts" + n).hide();
			panduanArray[n] = false;
		} else {
			if(isNaN($(".sdk_input" + n).val())) {
				$(".zm_sdk_ts" + n).show();
				$(".sdk_ts" + n).hide();
			} else {
				$(".sdk_ts" + n).show();
				$(".zm_sdk_ts" + n).hide();
			}
			panduanArray[n] = false;
		}

		if(n == 3) {
			if(panduanArray[n]) {
				$(".btn-primary").attr("disabled", false);
			} else {
				$(".btn-primary").attr("disabled", true);
			}
		} else if(n == 4) {
			if(panduanArray[n] && (String($(".sdk_input" + n).val()).charAt(0) == 1)) {
				$(".btn-primary").attr("disabled", false);
			} else {
				$(".btn-primary").attr("disabled", true);
			}
		} else {
			if( panduanArray[1] && panduanArray[2]) {
				$(".btn-primary").attr("disabled", false);
			} else {
				$(".btn-primary").attr("disabled", true);
			}
		}
	}
}

//口令
function sdkPanduan(n, lengths) {
	if(((String($(".sdk_input"+n).val()).indexOf(" ")!=-1))||((String($(".sdk_input"+n).val()).indexOf("　")!=-1))){
		$(".sdk_ts"+n).hide();
		$(".zm_sdk_ts"+n).hide();
		$(".kg_sdk_ts"+n).show();
	}else{
		$(".kg_sdk_ts"+n).hide();	
		if(String($(".sdk_input" + n).val()).length > (lengths-1) && checkIpt($(".sdk_input" + n).val()) != false) {
			$(".sdk_ts" + n).hide();
			$(".zm_sdk_ts" + n).hide();
			panduanArray[n] = true;
		} else if(String($(".sdk_input" + n).val()).length < lengths){
		    $(".sdk_ts" + n).show();
            $(".zm_sdk_ts" + n).hide();
            panduanArray[n] = false;
		}else if($(".sdk_input" + n).val().length > (lengths-1) && checkIpt($(".sdk_input" + n).val()) == false){
            $(".sdk_ts" + n).hide();
            $(".zm_sdk_ts" + n).show();
            panduanArray[n] = false;
        }else if($(".sdk_input" + n).val().length == 0) {
			$(".sdk_ts" + n).hide();
			$(".zm_sdk_ts" + n).hide();
			panduanArray[n] = false;
		} else {
			if(checkIpt($(".sdk_input" + n).val()) == false) {
				$(".zm_sdk_ts" + n).show();
				$(".sdk_ts" + n).hide();
			} else {
				$(".sdk_ts" + n).show();
				$(".zm_sdk_ts" + n).hide();
			}
			panduanArray[n] = false;
		}
	
		if(n == 3) {
			if(panduanArray[n]) {
				$(".btn-primary").attr("disabled", false);
			} else {
				$(".btn-primary").attr("disabled", true);
			}
		} else if(n == 4) {
			if(panduanArray[n] && (String($(".sdk_input" + n).val()).charAt(0) == 1)) {
				$(".btn-primary").attr("disabled", false);
			} else {
				$(".btn-primary").attr("disabled", true);
			}
		} else {
			if(panduanArray[1] && panduanArray[2]) {
				$(".btn-primary").attr("disabled", false);
			} else {
				$(".btn-primary").attr("disabled", true);
			}
		}
	}
}


