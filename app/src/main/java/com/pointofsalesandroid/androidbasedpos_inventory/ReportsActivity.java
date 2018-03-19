package com.pointofsalesandroid.androidbasedpos_inventory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pointofsalesandroid.androidbasedpos_inventory.adapter.ReportRestaurantMenusAdapter;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.AddItemMapModel;
import com.pointofsalesandroid.androidbasedpos_inventory.models.ProductItemGridModel;

import java.util.ArrayList;

public class ReportsActivity extends AppCompatActivity {
    RecyclerView restaurantMenuList;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    ArrayList<ProductItemGridModel> productItemGridModelArrayList = new ArrayList<>();
    ReportRestaurantMenusAdapter reportRestaurantMenusAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        reportRestaurantMenusAdapter = new ReportRestaurantMenusAdapter(ReportsActivity.this,productItemGridModelArrayList);
        restaurantMenuList = (RecyclerView) findViewById(R.id.restaurant_menus_list_reports);
        restaurantMenuList.setLayoutManager(new LinearLayoutManager(ReportsActivity.this));
        restaurantMenuList.setAdapter(reportRestaurantMenusAdapter);

        mDatabase.child(Utils.restaurantItems).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productItemGridModelArrayList.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ProductItemGridModel productItemGridModel = new ProductItemGridModel();
                    AddItemMapModel addItemMapModel = dataSnapshot1.getValue(AddItemMapModel.class);
                    productItemGridModel.setiName(addItemMapModel.itemName);
                    productItemGridModel.setItemCategory(addItemMapModel.itemCategory);
                    productItemGridModel.setItemBannerUrl(addItemMapModel.itemBannerURL);
                    productItemGridModel.setItemPrice(addItemMapModel.itemPrice);
                    productItemGridModel.setItemKey(addItemMapModel.itemKey);
                    productItemGridModel.setItemPublic(addItemMapModel.itemPublic);
                    productItemGridModelArrayList.add(productItemGridModel);
                    reportRestaurantMenusAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
