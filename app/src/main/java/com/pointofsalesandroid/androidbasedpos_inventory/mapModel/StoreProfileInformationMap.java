package com.pointofsalesandroid.androidbasedpos_inventory.mapModel;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Keji's Lab on 17/11/2017.
 */

public class StoreProfileInformationMap {
    public String storeProfileUrl;
    public String storeBannerUrl;
    public String storeName;
    public String storeAddress;
    public String storeContact;
    public String storeType;

    public StoreProfileInformationMap(){

    }

    public StoreProfileInformationMap(String StoreProfile,
                                      String StoreBanner,
                                      String name,
                                      String address,
                                      String contact,String type)
    {
        this.storeProfileUrl = StoreProfile;
        this.storeBannerUrl = StoreBanner;
        this.storeName = name;
        this.storeAddress = address;
        this.storeContact = contact;
        this.storeType = type;

    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("storeProfileUrl",storeProfileUrl);
        result.put("storeBannerUrl",storeBannerUrl);
        result.put("storeName",storeName);
        result.put("storeAddress",storeAddress);
        result.put("storeContact",storeContact);
        result.put("storeType",storeType);


        return result;
    }

}
