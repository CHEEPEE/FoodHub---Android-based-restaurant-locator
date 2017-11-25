package com.pointofsalesandroid.androidbasedpos_inventory.Restaurant;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pointofsalesandroid.androidbasedpos_inventory.LogInActivity;
import com.pointofsalesandroid.androidbasedpos_inventory.R;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.StoreProfileInformationMap;
import com.pointofsalesandroid.androidbasedpos_inventory.models.StoreProfileModel;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;

public class InventoryRestaurant extends AppCompatActivity {
FloatingActionButton addProducts;
Toolbar inventoryToolbar;
DatabaseReference mDatabase;
FirebaseAuth mAuth;
TextView menuSingOut;
TextView StoreN;
ArrayList<StoreProfileModel> ArrayStoreProfile = new ArrayList<>();
    private SlidingRootNav slidingRootNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_restaurant);
        addProducts = (FloatingActionButton) findViewById(R.id.addProducts);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        inventoryToolbar = (Toolbar) findViewById(R.id.inventoryToolbar);

        addProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(InventoryRestaurant.this,AddToInventoryRestuarant.class);
                startActivity(i);
            }
        });
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.inventory_drawer_layout)
                .withDragDistance(250)
                .withRootViewScale(1f)
                .withToolbarMenuToggle(inventoryToolbar)
                .withContentClickableWhenMenuOpened(true)
                .inject();
        StoreN = (TextView) findViewById(R.id.textStoreName);
        menuSingOut = (TextView) findViewById(R.id.text_signOut);
        menuSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FirebaseAuth.getInstance().signOut();
               Intent i = new Intent(InventoryRestaurant.this, LogInActivity.class);
               startActivity(i);
               finish();
            }
        });

        //************** get Profile ********************
        mDatabase.child("storeProfiles").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StoreProfileModel storeProfileModel = new StoreProfileModel();
                StoreProfileInformationMap storeProfileInformationMap = dataSnapshot.getValue(StoreProfileInformationMap.class);
                storeProfileModel.setStoreName(storeProfileInformationMap.storeName);
                storeProfileModel.setStoreBannerUrl(storeProfileInformationMap.storeBannerUrl);
                storeProfileModel.setStoreProfileUrl(storeProfileInformationMap.storeProfileUrl);
                storeProfileModel.setStoreAddress(storeProfileInformationMap.storeAddress);
                storeProfileModel.setStoreContact(storeProfileInformationMap.storeContact);
                ArrayStoreProfile.add(storeProfileModel);
                StoreN.setText(ArrayStoreProfile.get(0).getStoreName());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //*************set Text And ImageDraw ********************



    }

}
