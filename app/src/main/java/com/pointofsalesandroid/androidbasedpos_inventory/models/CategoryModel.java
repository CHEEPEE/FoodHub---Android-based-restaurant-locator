package com.pointofsalesandroid.androidbasedpos_inventory.models;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class CategoryModel {
    String key;
    String category;

    public String getKey(){
        return key;
    }
    public String getCategory(){
        return category;
    }
    public void setKey(String itemKey){
        this.key = itemKey;
    }
    public void setCategory(String itemCategory){
        this.category = itemCategory;
    }
}
