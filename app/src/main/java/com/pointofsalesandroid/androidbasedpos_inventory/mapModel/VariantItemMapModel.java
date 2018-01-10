package com.pointofsalesandroid.androidbasedpos_inventory.mapModel;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class VariantItemMapModel {
    public String itemKey;
    public String itemVariantName;
    public String itemVariantDescription;
    public String itemVariantPrice;

    public VariantItemMapModel(String key, String name, String des, String price){
      this.itemKey = key;
      this.itemVariantName = name;
      this.itemVariantDescription = des;
      this.itemVariantPrice = price;

    }
    public VariantItemMapModel(){

    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("itemKey",itemKey);
        result.put("itemVariantName",itemVariantName);
        result.put("itemVariantDescription",itemVariantDescription);
        result.put("itemVariantPrice",itemVariantPrice);

        return result;
        }
    }
