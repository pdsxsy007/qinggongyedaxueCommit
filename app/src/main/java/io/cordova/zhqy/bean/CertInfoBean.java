package io.cordova.zhqy.bean;

public class CertInfoBean {
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
        private String serialNumber;

        private String certDN;

        private String certCN;

        private String userId;

        private String sigAlgName;

        public void setSerialNumber(String serialNumber){
            this.serialNumber = serialNumber;
        }
        public String getSerialNumber(){
            return this.serialNumber;
        }
        public void setCertDN(String certDN){
            this.certDN = certDN;
        }
        public String getCertDN(){
            return this.certDN;
        }
        public void setCertCN(String certCN){
            this.certCN = certCN;
        }
        public String getCertCN(){
            return this.certCN;
        }
        public void setUserId(String userId){
            this.userId = userId;
        }
        public String getUserId(){
            return this.userId;
        }
        public void setSigAlgName(String sigAlgName){
            this.sigAlgName = sigAlgName;
        }
        public String getSigAlgName(){
            return this.sigAlgName;
        }

    }
}
