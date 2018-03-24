package com.pointofsalesandroid.androidbasedpos_inventory.models;

/**
 * Created by Keji's Lab on 22/01/2018.
 */

public class ChatDataModel {

    String msgkey;
    String message;
    String timestamp;
    String username;
    String userID;
    String userImg;

    public String getUserImg(){
        return userImg;
    }
    public String getMsgkey(){
        return  msgkey;
    }
    public String getMessage(){
        return message;
    }

    public String getTimestamp(){
        return timestamp;
    }
    public String getUsername(){
        return username;
    }
    public String getUserID(){
        return userID;
    }

    public void setMsgkey(String key){
        this.msgkey = key;
    }
    public void setMessage(String msg){
        this.message = msg;
    }
    public void setTimestamp(String time){
        this.timestamp = time;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setUserID(String uid){
        this.userID = uid;
    }
    public void setUserImg(String url){
        this.userImg = url;

    }
}

