package com.pointofsalesandroid.androidbasedpos_inventory.models;

/**
 * Created by Keji's Lab on 09/01/2018.
 */

public class VariantItemModel {
    String key;
    String variantName;
    String variantDes;
    String variantPrice;


    public String getKey(){
        return key;
    }

    public String getVariantName(){
        return variantName;
    }
    public String getVariantDes(){
        return variantDes;
    }
    public String getVariantPrice(){
        return variantPrice;
    }
    public void setKey(String key){
        this.key = key;
    }
    public void setVariantName(String name){
        this.variantName = name;
    }
    public void setVariantDes(String Des){
        this.variantDes = Des;
    }
    public void setVariantPrice(String price){
        this.variantPrice = price;
    }


}