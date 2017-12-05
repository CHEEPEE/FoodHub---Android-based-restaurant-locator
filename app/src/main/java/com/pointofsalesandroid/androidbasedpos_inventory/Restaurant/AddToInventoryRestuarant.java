package com.pointofsalesandroid.androidbasedpos_inventory.Restaurant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pointofsalesandroid.androidbasedpos_inventory.ProfileManagement;
import com.pointofsalesandroid.androidbasedpos_inventory.R;
import com.pointofsalesandroid.androidbasedpos_inventory.Utils;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.AddItemMapModel;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddToInventoryRestuarant extends AppCompatActivity {
    Spinner spinnerCategory;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    ArrayList<String> categoryItemList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    EditText itemName,itemCode,itemPrice;
    Spinner itemCategory;
    String Category = "default";
    Context c;
    Button saveToInven;
    ImageView itemBanner;
    Uri bannerUri;
    RelativeLayout prog;
    AVLoadingIndicatorView avi;
    StorageReference mStorageReference;
    private static final int READ_REQUEST_CODE = 42;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_inventroy_restaurant);

        //************ start initialaze ****************
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        prog = (RelativeLayout)findViewById(R.id.prog);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        itemBanner = (ImageView) findViewById(R.id.product_banner);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        c =AddToInventoryRestuarant.this;
        saveToInven = (Button) findViewById(R.id.saveToiInventory);
        spinnerCategory = (Spinner) findViewById(R.id.dropDownCategory);
        itemName = (EditText)findViewById(R.id.fItemName);
        itemCode = (EditText)findViewById(R.id.fMenuCode);
        itemPrice = (EditText) findViewById(R.id.fitemPrice);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position!=0){
                Category = categoryItemList.get(position);
                Utils.toster(c,categoryItemList.get(position));
             }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    categoryItemList.add(dataSnapshot1.child("category").getValue(String.class).toString());
                    System.out.println(dataSnapshot1.getValue().toString());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        saveToInven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm(itemName,itemCode,itemPrice)){
                    uploadItemBanner(bannerUri);
                    setProgress(true);

                }
            }
        });

        itemBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });


    }

    //********************* file Search *****************


    //**************** validation ************************
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
        }if (bannerUri==null){
            valid = false;
            Utils.toster(c,"Please Select Product Image");
        }
        return valid;
    }

    //****************** file Search And setting to ImageView

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Filter to show only images, using the image MIME data imageType.
        // If one wanted to search for ogg vorbis files, the imageType would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Choose File"), READ_REQUEST_CODE);

    }

    @Override

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("TAG", "Uri: " + uri.getLastPathSegment());
                setImage(uri,itemBanner);
            }else {
                System.out.println("null?");
            }

        }

    }

    private void setImage(Uri uri,ImageView imageView){
        // floatClearImage.setVisibility(View.VISIBLE);
        // Picasso.with(CreatePostActivity.this).load(uri).resize(300,600).into(imageToUpload);
        bannerUri = uri;
        Glide.with(c).load(uri).into(imageView);
        imageView.setColorFilter(getResources().getColor(R.color.transparent));
        imageView.setPadding(0,0,0,0);


    }

    public void uploadItemBanner(Uri ImageStorageURI){
        if (ImageStorageURI!=null){
            InputStream storeBannerFile = null;
            try {
                storeBannerFile = getContentResolver().openInputStream(ImageStorageURI);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            StorageReference ImagestoreRef = mStorageReference.child("images/"+Utils.restaurantItems+ File.separator+mAuth.getCurrentUser().getUid() + File.separator + getFileName(ImageStorageURI)
                    +storeBannerFile.toString()+File.separator+getFileName(ImageStorageURI));

            ImagestoreRef.putStream(storeBannerFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    saveItem(itemName.getText().toString(),itemCode.getText().toString(),
                            itemPrice.getText().toString(),Category,taskSnapshot.getDownloadUrl().toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    setProgress(false);
                }
            });
        }
    }

    private String getFileName(Uri uri) {

        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private void saveItem(String itemName,String code,String price,
                          String itemCat,String bannerURL){
        String UserId = mAuth.getUid();
        String key = mDatabase.push().getKey();
        AddItemMapModel addItemMapModel = new AddItemMapModel(itemName,code,price,itemCat,bannerURL,key);
        Map<String,Object> postValue = addItemMapModel.toMap();
        Map<String,Object> childUpdates = new HashMap<>();
        childUpdates.put(key,postValue);
        mDatabase.child(Utils.restaurantItems).child(UserId).updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setProgress(false);
                Intent i = new Intent(AddToInventoryRestuarant.this,InventoryRestaurant.class);
                startActivity(i);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                setProgress(false);
            }
        });


    }

    public void setProgress(boolean boo){
        if (boo){
            avi.setVisibility(View.VISIBLE);
            prog.setVisibility(View.VISIBLE);
        }else {
            avi.setVisibility(View.INVISIBLE);
            prog.setVisibility(View.INVISIBLE);
        }
    }


}
