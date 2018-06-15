package com.myweb.vo;

public class SocketMessage {

    private Integer fromUserId;
    private Integer toUserid;
    private String msssage;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Integer fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Integer getToUserid() {
        return toUserid;
    }

    public void setToUserid(Integer toUserid) {
        this.toUserid = toUserid;
    }

    public String getMsssage() {
        return msssage;
    }

    public void setMsssage(String msssage) {
        this.msssage = msssage;
    }
}
