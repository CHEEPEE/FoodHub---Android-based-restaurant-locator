package com.pointofsalesandroid.androidbasedpos_inventory;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Inventory extends AppCompatActivity {
FloatingActionButton addProducts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        addProducts = (FloatingActionButton) findViewById(R.id.addProducts);
        addProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(Inventory.this,AddToInventory.class);
                startActivity(i);
            }
        });
    }
}
