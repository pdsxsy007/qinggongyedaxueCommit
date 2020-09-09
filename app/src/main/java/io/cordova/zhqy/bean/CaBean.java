package io.cordova.zhqy.bean;

public class CaBean {
    private boolean success;

    private String msg;

    private Obj obj;

    private String count;

    private String attributes;

    public void setSuccess(boolean success){
        this.success = success;
    }
    public boolean getSuccess(){
        return this.success;
    }
    public void setMsg(String msg){
        this.msg = msg;
    }
    public String getMsg(){
        return this.msg;
    }
    public void setObj(Obj obj){
        this.obj = obj;
    }
    public Obj getObj(){
        return this.obj;
    }
    public void setCount(String count){
        this.count = count;
    }
    public String getCount(){
        return this.count;
    }
    public void setAttributes(String attributes){
        this.attributes = attributes;
    }
    public String getAttributes(){
        return this.attributes;
    }

    public class Obj {
        private String signTime;

        private String certDn;

        private String plainData;

        public void setSignTime(String signTime){
            this.signTime = signTime;
        }
        public String getSignTime(){
            return this.signTime;
        }
        public void setCertDn(String certDn){
            this.certDn = certDn;
        }
        public String getCertDn(){
            return this.certDn;
        }
        public void setPlainData(String plainData){
            this.plainData = plainData;
        }
        public String getPlainData(){
            return this.plainData;
        }

    }
}
