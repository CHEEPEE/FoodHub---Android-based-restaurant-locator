package com.pointofsalesandroid.androidbasedpos_inventory.Restaurant;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.GridLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pointofsalesandroid.androidbasedpos_inventory.LogInActivity;
import com.pointofsalesandroid.androidbasedpos_inventory.R;
import com.pointofsalesandroid.androidbasedpos_inventory.Utils;
import com.pointofsalesandroid.androidbasedpos_inventory.adapter.RecycleItemCategoryAdapter;
import com.pointofsalesandroid.androidbasedpos_inventory.adapter.RecycleItemProductAdapter;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.AddItemMapModel;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.CategoryMapModel;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.StoreProfileInformationMap;
import com.pointofsalesandroid.androidbasedpos_inventory.models.CategoryModel;
import com.pointofsalesandroid.androidbasedpos_inventory.models.ProductItemGridModel;
import com.pointofsalesandroid.androidbasedpos_inventory.models.StoreProfileModel;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InventoryRestaurant extends AppCompatActivity {
FloatingActionButton addProducts;
Toolbar inventoryToolbar;
FirebaseAuth mAuth;
TextView menuSingOut;
TextView StoreN,addCategory;
RecyclerView categoryList,itemList;
ArrayList<ProductItemGridModel> arrayItemGrind = new ArrayList<>();
DatabaseReference mDatabase;
Context c;
GridLayoutManager gridLayoutManager;
RecycleItemProductAdapter recycleItemProductAdapter;
ArrayList<StoreProfileModel> ArrayStoreProfile = new ArrayList<>();
ArrayList<CategoryModel> categoryItemList = new ArrayList<>();
    private SlidingRootNav slidingRootNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_restaurant);
        addProducts = (FloatingActionButton) findViewById(R.id.addProducts);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        itemList = (RecyclerView) findViewById(R.id.itemListGrid);

        recycleItemProductAdapter = new RecycleItemProductAdapter(c,arrayItemGrind);
        gridLayoutManager = new GridLayoutManager(c,2);

        itemList.setLayoutManager(gridLayoutManager);
        itemList.setItemAnimator(new DefaultItemAnimator());
        itemList.setAdapter(recycleItemProductAdapter);


        inventoryToolbar = (Toolbar) findViewById(R.id.inventoryToolbar);
        c = InventoryRestaurant.this;
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
        //************* side nav initialize **********
        StoreN = (TextView) findViewById(R.id.textStoreName);
        menuSingOut = (TextView) findViewById(R.id.text_signOut);
        addCategory = (TextView) findViewById(R.id.addCategory);



        //************** Recycler ******************
        final RecycleItemCategoryAdapter recycleItemCategoryAdapter = new RecycleItemCategoryAdapter(InventoryRestaurant.this,categoryItemList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(InventoryRestaurant.this);
        categoryList = (RecyclerView)findViewById(R.id.categoryList);
        categoryList.setItemAnimator(new DefaultItemAnimator());
        categoryList.setLayoutManager(layoutManager);
        categoryList.setAdapter(recycleItemCategoryAdapter);
        recycleItemCategoryAdapter.notifyDataSetChanged();
        //*************** Category Item Listener *************
        mDatabase.child(Utils.storeItemCategory).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryItemList.clear();
               for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                   CategoryModel categoryModel = new CategoryModel();
                   CategoryMapModel categoryMapModel =dataSnapshot1.getValue(CategoryMapModel.class);
                   categoryModel.setKey(categoryMapModel.key);
                   categoryModel.setCategory(categoryMapModel.category);
                   categoryItemList.add(categoryModel);
                   recycleItemCategoryAdapter.notifyDataSetChanged();
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //************************************************************************8
        menuSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(InventoryRestaurant.this, LogInActivity.class);
                startActivity(i);
                finish();
            }
        });

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(c)
                        .content("Add Category")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                               Utils.toster(c, dialog.getInputEditText().getText().toString());
                               String key = mDatabase.push().getKey();
                                CategoryMapModel categoryMapModel = new CategoryMapModel(key,dialog.getInputEditText().getText().toString());
                                Map<String,Object> categoryVal = categoryMapModel.toMap();
                                Map<String,Object> childUpdate = new HashMap<>();
                                childUpdate.put(key,categoryVal);
                                mDatabase.child(Utils.storeItemCategory).child(mAuth.getCurrentUser().getUid()).updateChildren(childUpdate);

                            }
                        })
                        .input("Category Name", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // Do something

                            }
                        }).show();

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

        mDatabase.child(Utils.restaurantItems).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayItemGrind.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ProductItemGridModel productItemGridModel = new ProductItemGridModel();
                    AddItemMapModel addItemMapModel = dataSnapshot1.getValue(AddItemMapModel.class);
                    productItemGridModel.setiName(addItemMapModel.itemName);
                    productItemGridModel.setItemCategory(addItemMapModel.itemCategory);
                    productItemGridModel.setItemBannerUrl(addItemMapModel.itemBannerURL);
                    productItemGridModel.setItemPrice(addItemMapModel.itemPrice);
                    arrayItemGrind.add(productItemGridModel);
                    recycleItemProductAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //*************set Text And ImageDraw ********************



    }

}
