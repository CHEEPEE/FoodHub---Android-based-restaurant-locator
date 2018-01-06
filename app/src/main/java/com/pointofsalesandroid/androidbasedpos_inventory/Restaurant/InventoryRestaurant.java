package com.pointofsalesandroid.androidbasedpos_inventory.Restaurant;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputType;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.pointofsalesandroid.androidbasedpos_inventory.LogInActivity;
import com.pointofsalesandroid.androidbasedpos_inventory.R;
import com.pointofsalesandroid.androidbasedpos_inventory.UpdateProfile;
import com.pointofsalesandroid.androidbasedpos_inventory.Utils;
import com.pointofsalesandroid.androidbasedpos_inventory.adapter.GlideApp;
import com.pointofsalesandroid.androidbasedpos_inventory.adapter.RecycleItemCategoryAdapter;
import com.pointofsalesandroid.androidbasedpos_inventory.adapter.RecycleItemProductAdapter;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.AddItemMapModel;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.RestaurantLocationMapModel;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.StoreProfileInformationMap;
import com.pointofsalesandroid.androidbasedpos_inventory.models.CategoryModel;
import com.pointofsalesandroid.androidbasedpos_inventory.models.ProductItemGridModel;
import com.pointofsalesandroid.androidbasedpos_inventory.models.StoreProfileModel;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class InventoryRestaurant extends AppCompatActivity {
FloatingActionButton addProducts;
Toolbar inventoryToolbar;
FirebaseAuth mAuth;
TextView menuSingOut,accountSettings;
TextView StoreN,addCategory;
RecyclerView categoryList,itemList;
ArrayList<ProductItemGridModel> arrayItemGrind = new ArrayList<>();
DatabaseReference mDatabase;
Context c;
ImageView ic_edit,ic_delete;
CircleImageView storeIcon;
public ConstraintLayout constraintLayout;
String itemCategoryString = "itemCategory";
GridLayoutManager gridLayoutManager;
RecycleItemProductAdapter recycleItemProductAdapter;
int itemPos;
ArrayList<StoreProfileModel> ArrayStoreProfile = new ArrayList<>();
ArrayList<CategoryModel> categoryItemList = new ArrayList<>();
RecycleItemCategoryAdapter recycleItemCategoryAdapter;
private SlidingRootNav slidingRootNav;
Context context;
ImageView drawerImgBackground;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_restaurant);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        addProducts = (FloatingActionButton) findViewById(R.id.addProducts);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        constraintLayout = (ConstraintLayout) findViewById(R.id.constrainLayout);
        itemList = (RecyclerView) findViewById(R.id.itemListGrid);
        context = InventoryRestaurant.this;




        inventoryToolbar = (Toolbar) findViewById(R.id.inventoryToolbar);
        setSupportActionBar(inventoryToolbar);
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

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //code for portrait mode
            gridLayoutManager = new GridLayoutManager(c,2);
        }
        else{
            //code for landscape mode
            gridLayoutManager = new GridLayoutManager(c,3);
        }
        recycleItemProductAdapter = new RecycleItemProductAdapter(c,arrayItemGrind);
        storeIcon = (CircleImageView) findViewById(R.id.storeIcon);

        itemList.setLayoutManager(gridLayoutManager);
        itemList.setItemAnimator(new DefaultItemAnimator());
        itemList.setAdapter(recycleItemProductAdapter);

        //************* side nav initialize **********
        StoreN = (TextView) findViewById(R.id.textStoreName);
        menuSingOut = (TextView) findViewById(R.id.text_signOut);
        addCategory = (TextView) findViewById(R.id.addCategory);
        accountSettings = (TextView) findViewById(R.id.accountSettings);
        drawerImgBackground = (ImageView) findViewById(R.id.drawerImgBackground);




        //*******************Screen Orientation***********************



        //************** Recycler ******************
        recycleItemCategoryAdapter = new RecycleItemCategoryAdapter(InventoryRestaurant.this,categoryItemList);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL);
        categoryList = (RecyclerView)findViewById(R.id.rv_categoryItems);
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

        categoryList();

        //*************** Category Item Listener *************

      recycleItemCategoryAdapter.setOnItemClickListener(new RecycleItemCategoryAdapter.OnItemClickLitener() {
          @Override
          public void onItemClick(View view, int position) {
              setitemListCategory(itemCategoryString,categoryItemList.get(position).getKey());
              slidingRootNav.closeMenu();
          }
      });

      recycleItemCategoryAdapter.setonItemLongClickListener(new RecycleItemCategoryAdapter.OnItemLongClickListener() {
          @Override
          public void onItemLongClick(View view, int posistion) {
             showDialog(categoryItemList.get(posistion).getCategory(),categoryItemList.get(posistion).getKey());


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
                               if (!dialog.getInputEditText().getText().toString().trim().equals("")){
                                   String key = mDatabase.push().getKey();
                                   RestaurantLocationMapModel categoryMapModel = new RestaurantLocationMapModel(key,dialog.getInputEditText().getText().toString());
                                   Map<String,Object> categoryVal = categoryMapModel.toMap();
                                   Map<String,Object> childUpdate = new HashMap<>();
                                   childUpdate.put(key,categoryVal);
                                   mDatabase.child(Utils.storeItemCategory).child(mAuth.getCurrentUser().getUid()).updateChildren(childUpdate);
                               }else {
                                   Utils.toster(c,"Error Empty Text");
                               }

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
                GlideApp.with(c).load(ArrayStoreProfile.get(0).getStoreProfileUrl()).override(200,200).into(storeIcon);
                GlideApp.with(c).load(ArrayStoreProfile.get(0).getStoreBannerUrl()).override(200,200).into(drawerImgBackground);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        accountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(context, UpdateProfile.class);
                startActivity(i);
                finish();
            }
        });

        recycleItemProductAdapter.setOnItemClickListener(new RecycleItemProductAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position, String Text) {
               if (Text.equals("edit")){
                   editProductItem(position);


               }
               if (Text.equals("delete")){
                   Snackbar.make(constraintLayout,"Are You Sure you Want to Delete This Item?",Snackbar.LENGTH_SHORT)
                           .setAction("Proceed",new deleteProductItem()).show();
                   itemPos = position;
               }
            }
        });
    }

    private void setitemListCategory(String child,String category){
        arrayItemGrind.clear();
        recycleItemProductAdapter.notifyDataSetChanged();
        if (category.equals("All")){
            arrayItemGrind.clear();
            mDatabase.child(Utils.restaurantItems).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        ProductItemGridModel productItemGridModel = new ProductItemGridModel();
                        AddItemMapModel addItemMapModel = dataSnapshot1.getValue(AddItemMapModel.class);
                        productItemGridModel.setiName(addItemMapModel.itemName);
                        productItemGridModel.setItemCategory(addItemMapModel.itemCategory);
                        productItemGridModel.setItemBannerUrl(addItemMapModel.itemBannerURL);
                        productItemGridModel.setItemPrice(addItemMapModel.itemPrice);
                        productItemGridModel.setItemKey(addItemMapModel.itemKey);
                        arrayItemGrind.add(productItemGridModel);
                        recycleItemProductAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {

            mDatabase.child(Utils.restaurantItems).child(mAuth.getCurrentUser().getUid()).orderByChild(child).equalTo(category).addValueEventListener(new ValueEventListener() {
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
                        productItemGridModel.setItemKey(addItemMapModel.itemKey);
                        arrayItemGrind.add(productItemGridModel);
                        recycleItemProductAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        //*****************8 set ItemList Categroy  ***************************8


    }
    private void setitemListCategoryAll(){

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
                    productItemGridModel.setItemKey(addItemMapModel.itemKey);
                    arrayItemGrind.add(productItemGridModel);
                    recycleItemProductAdapter.notifyDataSetChanged();
                }
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

    private void editProductItem(int position){
        ProductItemGridModel productItemGridModel = arrayItemGrind.get(position);
        Intent i = new Intent(InventoryRestaurant.this,UpdateProductActiivty.class);
        i.putExtra(Utils.productItems.itemKey,productItemGridModel.getItemKey());
        startActivity(i);
        finish();

    }

    //*************** custom Dialog  ***************
    public void showDialog(String msg, final String key){
        final Dialog dialog = new Dialog(InventoryRestaurant.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_edit_delete_category);
        final EditText category_field = (EditText) dialog.findViewById(R.id.category_field);
        Button save = (Button) dialog.findViewById(R.id.btn_save);
        Button delete = (Button) dialog.findViewById(R.id.btn_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(Utils.storeItemCategory).child(mAuth.getCurrentUser().getUid()).child(key).removeValue();
                recycleItemCategoryAdapter.notifyDataSetChanged();

                mDatabase.child(Utils.restaurantItems).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            if (dataSnapshot1.child("itemCategory").getValue(String.class).equals(key)){
                                dataSnapshot1.getRef().removeValue();
                                FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot1.child("itemBannerURL").getValue(String.class).toString()).delete();
                                Utils.toster(c,dataSnapshot1.child("itemCategory").getValue().toString());
                                recycleItemCategoryAdapter.notifyDataSetChanged();
                                setitemListCategoryAll();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                dialog.dismiss();
                categoryList();

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mDatabase.child(Utils.storeItemCategory).child(mAuth.getCurrentUser().getUid()).child(key).updateChildren()
                if (!category_field.getText().toString().trim().equals("")){
                    RestaurantLocationMapModel categoryMapModel = new RestaurantLocationMapModel(key,category_field.getText().toString());
                    Map<String,Object> categoryVal = categoryMapModel.toMap();
                    Map<String,Object> childUpdate = new HashMap<>();
                    childUpdate.put(key,categoryVal);
                    mDatabase.child(Utils.storeItemCategory).child(mAuth.getCurrentUser().getUid()).updateChildren(childUpdate);
                }else {
                    Utils.toster(c,"Error Empty Text");
                }
                dialog.dismiss();
            }
        });

        category_field.setText(msg);

        dialog.show();

    }
    private void categoryList(){
        mDatabase.child(Utils.storeItemCategory).child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        categoryItemList.clear();
                        CategoryModel categoryModel1 = new CategoryModel();
                        categoryModel1.setKey("All");
                        categoryModel1.setCategory("All");
                        categoryItemList.add(categoryModel1);
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            CategoryModel categoryModel = new CategoryModel();
                            RestaurantLocationMapModel categoryMapModel =dataSnapshot1.getValue(RestaurantLocationMapModel.class);
                            categoryModel.setKey(categoryMapModel.key);
                            categoryModel.setCategory(categoryMapModel.restauarantAddress);
                            categoryItemList.add(categoryModel);
                            recycleItemCategoryAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
