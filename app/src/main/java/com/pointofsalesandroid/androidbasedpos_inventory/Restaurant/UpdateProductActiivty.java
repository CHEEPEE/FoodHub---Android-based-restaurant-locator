package com.pointofsalesandroid.androidbasedpos_inventory.Restaurant;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pointofsalesandroid.androidbasedpos_inventory.R;
import com.pointofsalesandroid.androidbasedpos_inventory.Utils;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.AddItemMapModel;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.CategoryMapModel;
import com.pointofsalesandroid.androidbasedpos_inventory.models.CategoryModel;
import com.pointofsalesandroid.androidbasedpos_inventory.models.ProductItemGridModel;

import java.util.ArrayList;

public class UpdateProductActiivty extends AppCompatActivity {
EditText fitemName,fitemCategory,fitemBanner,fitemPrice,fitemCode;
DatabaseReference mDatabase;
String itemKey;
FirebaseAuth mAuth;
Spinner spinnerCategory;
ArrayList<String> categoryItemList = new ArrayList<>();
ArrayList<String> categoryItemListKey = new ArrayList<>();
ArrayAdapter<String> adapter;
String imgBannerURL;
Context c;
String Category = "default";
ImageView bannerImage;
    String value = null;
ArrayList<ProductItemGridModel> arrayProductItem = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product_actiivty);
        c = UpdateProductActiivty.this;
        fitemName = (EditText)findViewById(R.id.fItemName);
        fitemPrice = (EditText)findViewById(R.id.fitemPrice);
        fitemCode = (EditText)findViewById(R.id.fMenuCode);
        spinnerCategory = (Spinner) findViewById(R.id.dropDownCategory);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Bundle extra = getIntent().getExtras();
        itemKey = extra.getString(Utils.productItems.itemKey);
        bannerImage = (ImageView) findViewById(R.id.product_banner);
        mDatabase.child(Utils.restaurantItems).child(mAuth.getCurrentUser().getUid()).child(itemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProductItemGridModel productItemGridModel = new ProductItemGridModel();
                AddItemMapModel addItemMapModel = dataSnapshot.getValue(AddItemMapModel.class);
                fitemName.setText(addItemMapModel.itemName);
                fitemPrice.setText(addItemMapModel.itemPrice);
                fitemCode.setText(addItemMapModel.itemCode);
                setImage(addItemMapModel.itemBannerURL,bannerImage);
                setSpinner(addItemMapModel.itemCategory);
                Category = addItemMapModel.itemCategory;


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryItemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        mDatabase.child(Utils.storeItemCategory).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryItemList.clear();
                categoryItemList.add("Choose Category");
                categoryItemListKey.add("null");
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    categoryItemList.add(dataSnapshot1.child("category").getValue(String.class).toString());
                    categoryItemListKey.add(dataSnapshot1.child("key").getValue(String.class).toString());
                    setSpinner(getCategoryFromKey(dataSnapshot1.child("key").getValue(String.class).toString()));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private boolean validateForm(EditText itemName,EditText menuCode,EditText ItemPrice) {
        boolean valid = true;

        String name = itemName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            itemName.setError("Required.");
            valid = false;
        } else {
            itemName.setError(null);
        }

        String address = menuCode.getText().toString();
        if (TextUtils.isEmpty(address)) {
            menuCode.setError("Required.");
            valid = false;
        } else {
            menuCode.setError(null);
        }

        String contact = ItemPrice.getText().toString();
        if (TextUtils.isEmpty(contact)) {
            ItemPrice.setError("Required.");
            valid = false;
        } else {
            ItemPrice.setError(null);
        }
        if (Category.equals("default")){
            valid = false;
            Utils.toster(c,"Please Select Category");
        }if (imgBannerURL==null){
            valid = false;
            Utils.toster(c,"Please Select Product Image");
        }
        return valid;
    }

    private void setImage(String uri, ImageView imageView){
        // floatClearImage.setVisibility(View.VISIBLE);
        // Picasso.with(CreatePostActivity.this).load(uri).resize(300,600).into(imageToUpload);
        imgBannerURL = uri;
        Glide.with(c).load(uri).into(imageView);
        imageView.setColorFilter(getResources().getColor(R.color.transparent));
        imageView.setPadding(0,0,0,0);


    }
    private void setSpinner(String value)
    {
        int pos = adapter.getPosition(value);
        spinnerCategory.setSelection(pos+1);
    }

    private String getCategoryFromKey(final String key){

        mDatabase.child(Utils.storeItemCategory).child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            CategoryMapModel categoryMapModel=dataSnapshot1.getValue(CategoryMapModel.class);
                            try {
                                if (key.equals(categoryMapModel.key)) {
                                    value = categoryMapModel.category;
                                    Utils.toster(c,categoryMapModel.category);
                                }

                            }catch (Exception error){
                                Log.d("Error",error.toString());
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return value;

    }


}
