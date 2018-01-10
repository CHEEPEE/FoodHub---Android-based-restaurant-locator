package com.pointofsalesandroid.androidbasedpos_inventory.Restaurant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.pointofsalesandroid.androidbasedpos_inventory.R;

public class AddItemVariant extends AppCompatActivity {
    String itemKey;
    RecyclerView variantList,recentUsedVariantNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_variant);
        itemKey = getIntent().getExtras().getString("key");
        System.out.println(itemKey);
        variantList = (RecyclerView) findViewById(R.id.variantList);
        recentUsedVariantNameList = (RecyclerView) findViewById(R.id.recentUsedVariantName);



    }
}
