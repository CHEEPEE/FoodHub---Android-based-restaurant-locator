package com.pointofsalesandroid.androidbasedpos_inventory.mapModel;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class AddItemMapModel {
    public String itemName;
    public String itemCode;
    public String itemPrice;
    public String itemCategory;
    public String itemBannerURL;
    public String itemKey;
    public Boolean itemPublic;
    public AddItemMapModel (String name,String code,String price,String category,String bannerUrl,String key,Boolean itemPublic){
        this.itemName = name;
        this.itemCode = code;
        this.itemPrice = price;
        this.itemCategory = category;
        this.itemBannerURL = bannerUrl;
        this.itemKey = key;
        this.itemPublic = itemPublic;

    }
    public AddItemMapModel(){

    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("itemName",itemName);
        result.put("itemCode",itemCode);
        result.put("itemPrice",itemPrice);
        result.put("itemCategory",itemCategory);
        result.put("itemBannerURL",itemBannerURL);
        result.put("itemKey",itemKey);
        result.put("itemPublic",itemPublic);


        return result;
    }
}
