package com.pointofsalesandroid.androidbasedpos_inventory.models;

/**
 * Created by Keji's Lab on 27/11/2017.
 */

public class ProductItemGridModel {
    private String iName;
    private String itemCategory;
    private String itemPrice;
    private String itemBannerUrl;

    public String getiName(){
        return iName;
    }
    public String getItemCategory(){
        return itemCategory;
    }
    public String getItemPrice(){
        return itemPrice;
    }
    public String getItemBannerUrl(){
        return itemBannerUrl;
    }
    public void setiName(String name){
        this.iName = name;
    }
    public void setItemCategory(String category){
        this.itemCategory = category;
    }
    public void setItemBannerUrl(String bUrl){
        this.itemBannerUrl = bUrl;
    }
    public void setItemPrice(String price){
        this.itemPrice = price;
    }

}
