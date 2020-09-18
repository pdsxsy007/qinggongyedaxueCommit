package io.cordova.zhqy.bean;

import java.util.List;

public class CheckRoleCodeBean {
    private boolean success;

    private String msg;

    private List<Obj> obj ;

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
    public void setObj(List<Obj> obj){
        this.obj = obj;
    }
    public List<Obj> getObj(){
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
        private int configId;

        private int configType;

        private String configName;

        private String configKey;

        private String configValue;

        private String configCreateTime;

        private String configUpdateTime;

        private int configOrder;

        public void setConfigId(int configId){
            this.configId = configId;
        }
        public int getConfigId(){
            return this.configId;
        }
        public void setConfigType(int configType){
            this.configType = configType;
        }
        public int getConfigType(){
            return this.configType;
        }
        public void setConfigName(String configName){
            this.configName = configName;
        }
        public String getConfigName(){
            return this.configName;
        }
        public void setConfigKey(String configKey){
            this.configKey = configKey;
        }
        public String getConfigKey(){
            return this.configKey;
        }
        public void setConfigValue(String configValue){
            this.configValue = configValue;
        }
        public String getConfigValue(){
            return this.configValue;
        }

        public String getConfigCreateTime() {
            return configCreateTime;
        }

        public void setConfigCreateTime(String configCreateTime) {
            this.configCreateTime = configCreateTime;
        }

        public String getConfigUpdateTime() {
            return configUpdateTime;
        }

        public void setConfigUpdateTime(String configUpdateTime) {
            this.configUpdateTime = configUpdateTime;
        }

        public void setConfigOrder(int configOrder){
            this.configOrder = configOrder;
        }
        public int getConfigOrder(){
            return this.configOrder;
        }

    }

}
