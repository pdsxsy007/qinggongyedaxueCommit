package io.cordova.zhqy.bean;

import java.util.List;

public class MessageBean {
    private boolean success;

    private String msg;

    private List<Obj> obj ;

    private int count;

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
    public void setCount(int count){
        this.count = count;
    }
    public int getCount(){
        return this.count;
    }
    public void setAttributes(String attributes){
        this.attributes = attributes;
    }
    public String getAttributes(){
        return this.attributes;
    }

    public class Obj {
        private int backlogId;

        private String senderName;

        private String messageTitle;

        private String messageContent;

        private String messageMobileUrl;

        private int messageType;

        private String messageIdentification;

        private String appOpenid;

        private String messageAppName;

        private int noticeType;

        private String nodeId;

        private String remarks;

        private String messageSendTime;

        private String messageUpdateTime;

        private String backlogDetailId;

        private int backlogDetailState;

        private String toMessageType;

        private String memberNickname;

        public void setBacklogId(int backlogId){
            this.backlogId = backlogId;
        }
        public int getBacklogId(){
            return this.backlogId;
        }
        public void setSenderName(String senderName){
            this.senderName = senderName;
        }
        public String getSenderName(){
            return this.senderName;
        }
        public void setMessageTitle(String messageTitle){
            this.messageTitle = messageTitle;
        }
        public String getMessageTitle(){
            return this.messageTitle;
        }
        public void setMessageContent(String messageContent){
            this.messageContent = messageContent;
        }
        public String getMessageContent(){
            return this.messageContent;
        }

        public String getMessageMobileUrl() {
            return messageMobileUrl;
        }

        public void setMessageMobileUrl(String messageMobileUrl) {
            this.messageMobileUrl = messageMobileUrl;
        }

        public void setMessageType(int messageType){
            this.messageType = messageType;
        }
        public int getMessageType(){
            return this.messageType;
        }
        public void setMessageIdentification(String messageIdentification){
            this.messageIdentification = messageIdentification;
        }
        public String getMessageIdentification(){
            return this.messageIdentification;
        }
        public void setAppOpenid(String appOpenid){
            this.appOpenid = appOpenid;
        }
        public String getAppOpenid(){
            return this.appOpenid;
        }
        public void setMessageAppName(String messageAppName){
            this.messageAppName = messageAppName;
        }
        public String getMessageAppName(){
            return this.messageAppName;
        }
        public void setNoticeType(int noticeType){
            this.noticeType = noticeType;
        }
        public int getNoticeType(){
            return this.noticeType;
        }
        public void setNodeId(String nodeId){
            this.nodeId = nodeId;
        }
        public String getNodeId(){
            return this.nodeId;
        }
        public void setRemarks(String remarks){
            this.remarks = remarks;
        }
        public String getRemarks(){
            return this.remarks;
        }
        public void setMessageSendTime(String messageSendTime){
            this.messageSendTime = messageSendTime;
        }
        public String getMessageSendTime(){
            return this.messageSendTime;
        }
        public void setMessageUpdateTime(String messageUpdateTime){
            this.messageUpdateTime = messageUpdateTime;
        }
        public String getMessageUpdateTime(){
            return this.messageUpdateTime;
        }
        public void setBacklogDetailState(int backlogDetailState){
            this.backlogDetailState = backlogDetailState;
        }
        public int getBacklogDetailState(){
            return this.backlogDetailState;
        }
        public void setToMessageType(String toMessageType){
            this.toMessageType = toMessageType;
        }
        public String getToMessageType(){
            return this.toMessageType;
        }

        public String getBacklogDetailId() {
            return backlogDetailId;
        }

        public void setBacklogDetailId(String backlogDetailId) {
            this.backlogDetailId = backlogDetailId;
        }

        public String getMemberNickname() {
            return memberNickname;
        }

        public void setMemberNickname(String memberNickname) {
            this.memberNickname = memberNickname;
        }
    }
}
