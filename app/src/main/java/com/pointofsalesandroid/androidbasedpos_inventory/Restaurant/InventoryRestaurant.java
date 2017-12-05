package com.pointofsalesandroid.androidbasedpos_inventory.Restaurant;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
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
ImageView ic_edit,ic_delete;
public ConstraintLayout constraintLayout;
String itemCategoryString = "itemCategory";
GridLayoutManager gridLayoutManager;
RecycleItemProductAdapter recycleItemProductAdapter;
int itemPos;
ArrayList<StoreProfileModel> ArrayStoreProfile = new ArrayList<>();
ArrayList<CategoryModel> categoryItemList = new ArrayList<>();
private SlidingRootNav slidingRootNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_restaurant);
        mDatabase = FirebaseDatabase.getInstance().getReference();
       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase.keepSynced(true);
        addProducts = (FloatingActionButton) findViewById(R.id.addProducts);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        constraintLayout = (ConstraintLayout) findViewById(R.id.constrainLayout);

        itemList = (RecyclerView) findViewById(R.id.itemListGrid);



        inventoryToolbar = (Toolbar) findViewById(R.id.inventoryToolbar);
        c = InventoryRestaurant.this;
        addProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(InventoryRestaurant.this,AddToInventoryRestuarant.class);
                startActivity(i);
                finish();
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
        recycleItemProductAdapter = new RecycleItemProductAdapter(c,arrayItemGrind);
        gridLayoutManager = new GridLayoutManager(c,2);

        itemList.setLayoutManager(gridLayoutManager);
        itemList.setItemAnimator(new DefaultItemAnimator());
        itemList.setAdapter(recycleItemProductAdapter);

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


        //************** Category Filter Off ************
        CategoryModel catMode = new CategoryModel();
        catMode.setCategory("All");
        catMode.setKey("null");
        categoryItemList.add(catMode);
        //***********************************************

        mDatabase.child(Utils.storeItemCategory).child(mAuth.getCurrentUser().getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        CategoryModel categoryModel = new CategoryModel();
                        CategoryMapModel categoryMapModel =dataSnapshot.getValue(CategoryMapModel.class);
                        categoryModel.setKey(categoryMapModel.key);
                        categoryModel.setCategory(categoryMapModel.category);
                        categoryItemList.add(categoryModel);
                        recycleItemCategoryAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        //*************** Category Item Listener *************

      recycleItemCategoryAdapter.setOnItemClickListener(new RecycleItemCategoryAdapter.OnItemClickLitener() {
          @Override
          public void onItemClick(View view, int position) {
              setitemListCategory(itemCategoryString,categoryItemList.get(position).getCategory());
              slidingRootNav.closeMenu();
          }
      });

        setitemListCategoryAll();

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

        recycleItemProductAdapter.setOnItemClickListener(new RecycleItemProductAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position, String Text) {
               if (Text.equals("edit")){
                   editProductItem();

               }
               if (Text.equals("delete")){
                   Snackbar.make(constraintLayout,"Are You Sure you Want to Delete This Item?",Snackbar.LENGTH_SHORT)
                           .setAction("Proceed",new deleteProductItem()).show();
                   itemPos = position;
               }
            }
        });





        //*************set Text And ImageDraw ********************

        //************* Imageview SetOnclick *********************


    }

    private void setitemListCategory(String child,String category){
        arrayItemGrind.clear();
        recycleItemProductAdapter.notifyDataSetChanged();
        if (category.equals("All")){
            setitemListCategoryAll();
        }
        mDatabase.child(Utils.restaurantItems).child(mAuth.getCurrentUser().getUid()).orderByChild(child).equalTo(category).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ProductItemGridModel productItemGridModel = new ProductItemGridModel();
                AddItemMapModel addItemMapModel = dataSnapshot.getValue(AddItemMapModel.class);
                productItemGridModel.setiName(addItemMapModel.itemName);
                productItemGridModel.setItemCategory(addItemMapModel.itemCategory);
                productItemGridModel.setItemBannerUrl(addItemMapModel.itemBannerURL);
                productItemGridModel.setItemPrice(addItemMapModel.itemPrice);
                productItemGridModel.setItemKey(addItemMapModel.itemKey);
                arrayItemGrind.add(productItemGridModel);
                recycleItemProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //*****************8 set ItemList Categroy  ***************************8


    }
    private void setitemListCategoryAll(){
        mDatabase.child(Utils.restaurantItems).child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ProductItemGridModel productItemGridModel = new ProductItemGridModel();
                AddItemMapModel addItemMapModel = dataSnapshot.getValue(AddItemMapModel.class);
                productItemGridModel.setiName(addItemMapModel.itemName);
                productItemGridModel.setItemCategory(addItemMapModel.itemCategory);
                productItemGridModel.setItemBannerUrl(addItemMapModel.itemBannerURL);
                productItemGridModel.setItemPrice(addItemMapModel.itemPrice);
                productItemGridModel.setItemKey(addItemMapModel.itemKey);
                arrayItemGrind.add(productItemGridModel);
                recycleItemProductAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class deleteProductItem implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            Utils.toster(c,"Delete This Toast");

            Utils.toster(c,arrayItemGrind.get(itemPos).getItemKey());
            FirebaseStorage.getInstance().getReferenceFromUrl(arrayItemGrind.get(itemPos).getItemBannerUrl()).delete();
            FirebaseDatabase.getInstance().getReference()
                    .child(Utils.restaurantItems).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(arrayItemGrind.get(itemPos).getItemKey()).removeValue();
            arrayItemGrind.remove(itemPos);
            recycleItemProductAdapter.notifyDataSetChanged();
        }

    }

    private void editProductItem(){
        Utils.toster(c,"Edit this Product Item");
    }



}
