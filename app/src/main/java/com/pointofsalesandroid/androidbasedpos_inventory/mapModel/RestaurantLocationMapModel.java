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
    public RestaurantLocationMapModel(String getKey, String restoAddress,double lat,double longi){
        this.restauarantAddress = restoAddress;
        this.key = getKey;
        this.locationLatitude = lat;
        this.locationLongitude = longi;
    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("key",key);
        result.put("restauarantAddress", restauarantAddress);
        result.put("locationLatitude",locationLatitude);
        result.put("locationLongitude",locationLongitude);

        return result;
    }
}