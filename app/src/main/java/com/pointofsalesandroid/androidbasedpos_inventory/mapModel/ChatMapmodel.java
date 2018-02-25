package com.pointofsalesandroid.androidbasedpos_inventory.mapModel;

/**
 * Created by Keji's Lab on 22/01/2018.
 */

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class ChatMapmodel {

    public String msgkey;
    public String message;
    public String timestamp;
    public String username;
    public String userID;

    public ChatMapmodel(){

    }
    public ChatMapmodel(String key, String msg, String time, String username, String uid){
        this.msgkey = key;
        this.message = msg;
        this.timestamp = time;
        this.username = username;
        this.userID = uid;
    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("msgkey",msgkey);
        result.put("message",message);
        result.put("timestamp",timestamp);
        result.put("username",username);
        result.put("userID",userID);
        return result;
    }
}
