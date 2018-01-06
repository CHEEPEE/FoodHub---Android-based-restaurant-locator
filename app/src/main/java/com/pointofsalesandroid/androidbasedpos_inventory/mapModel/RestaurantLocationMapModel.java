package com.pointofsalesandroid.androidbasedpos_inventory.mapModel;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Keji's Lab on 07/01/2018.
 */

public class RestaurantLocationMapModel {
    public String key;
    public String restauarantAddress;
    public double locationLatitude;
    public double locationLongitude;

    public RestaurantLocationMapModel(){

    }
    public RestaurantLocationMapModel(String getKey, String restoAddress){
        this.restauarantAddress = restoAddress;
        this.key = getKey;
    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("key",key);
        result.put("restauarantAddress", restauarantAddress);

        return result;
    }
}