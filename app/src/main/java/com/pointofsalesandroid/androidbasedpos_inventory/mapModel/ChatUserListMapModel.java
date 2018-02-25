package com.pointofsalesandroid.androidbasedpos_inventory.mapModel;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class ChatUserListMapModel {
   public String pushkey;
   public String userID;
   public String displayName;
   public String timeStamp;
   public String userPhoto;

    public ChatUserListMapModel(){

    }
    public ChatUserListMapModel(String getPushKey, String setUserID, String setDisplayName, String setTimeStamp, String photo){
        this.pushkey = getPushKey;
        this.userID = setUserID;
        this.displayName = setDisplayName;
        this.timeStamp = setTimeStamp;
        this.userPhoto = photo;
    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("pushkey",pushkey);
        result.put("userID",userID);
        result.put("displayName",displayName);
        result.put("timeStamp",timeStamp);
        result.put("userPhoto",userPhoto);
        return result;
    }
}
