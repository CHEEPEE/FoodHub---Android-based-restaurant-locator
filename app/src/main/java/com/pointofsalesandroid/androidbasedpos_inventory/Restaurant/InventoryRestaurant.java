package com.pointofsalesandroid.androidbasedpos_inventory.Restaurant;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.pointofsalesandroid.androidbasedpos_inventory.R;

public class InventoryRestaurant extends AppCompatActivity {
FloatingActionButton addProducts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_restaurant);
        addProducts = (FloatingActionButton) findViewById(R.id.addProducts);
        addProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(InventoryRestaurant.this,AddToInventoryRestuarant.class);
                startActivity(i);
            }
        });
    }
}
