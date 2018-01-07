package com.pointofsalesandroid.androidbasedpos_inventory;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pointofsalesandroid.androidbasedpos_inventory.Restaurant.InventoryRestaurant;
import com.pointofsalesandroid.androidbasedpos_inventory.adapter.GlideApp;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.StoreProfileInformationMap;
import com.pointofsalesandroid.androidbasedpos_inventory.models.StoreProfileModel;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    Uri imageToUploadUri;
    EditText fieldStorename,fieldStoreAddress, fieldStoreContact;
    Button submitInformation;
    DatabaseReference mDatabase;
    ImageView StoreBanner,StoreProfileImage;
    ImageView tempImage,getLocation;
    Uri bannerUri, iconUri;
    String imageType;
    String storetype;
    FirebaseAuth mAuth;
    private String bannerDownloadURL,iconDownloadURL;
    AVLoadingIndicatorView savingProgress;
    RelativeLayout progBlocker;
    private Location mLastKnownLocation;

    private boolean value;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_management);
        submitInformation = (Button) findViewById(R.id.submitInformation);


        mAuth = FirebaseAuth.getInstance();
        progBlocker = (RelativeLayout) findViewById(R.id.prog);
        savingProgress = (AVLoadingIndicatorView)findViewById(R.id.avi);

        fieldStorename = (EditText) findViewById(R.id.input_name);
        fieldStoreAddress = (EditText) findViewById(R.id.input_address);
        fieldStoreContact = (EditText) findViewById(R.id.input_contact);
        StoreBanner = (ImageView) findViewById(R.id.storeBanner);
        StoreProfileImage = (ImageView)findViewById(R.id.store_profile_icon);
        getLocation = (ImageView) findViewById(R.id.getLocation);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        submitInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm(fieldStorename,fieldStoreAddress,fieldStoreContact)){
                    if (bannerUri !=null && iconUri !=null){
                        toaster("true");
                        uploadBanner(bannerUri,bannerDownloadURL);
                        setProgress(true);
                    }else {
                        toaster("missing banner of profile icon");
                    }
                }
            }
        });

        mDatabase.child(Utils.storeProfiles).child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StoreProfileModel storeProfileModel = new StoreProfileModel();
                StoreProfileInformationMap storeProfileInformationMap = dataSnapshot.getValue(StoreProfileInformationMap.class);
               try {
                   GlideApp.with(UpdateProfile.this).load(storeProfileInformationMap.storeBannerUrl).into(StoreBanner);
                   GlideApp.with(UpdateProfile.this).load(storeProfileInformationMap.storeProfileUrl).into(StoreProfileImage);
               }catch (IllegalArgumentException e){

               }
                bannerDownloadURL = storeProfileInformationMap.storeBannerUrl;
                iconDownloadURL = storeProfileInformationMap.storeProfileUrl;
                bannerUri =Uri.parse(storeProfileInformationMap.storeBannerUrl);
                iconUri = Uri.parse(storeProfileInformationMap.storeProfileUrl);

                fieldStorename.setText(storeProfileInformationMap.storeName);
                fieldStoreContact.setText(storeProfileInformationMap.storeContact);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child(Utils.restaurantLocation).child(mAuth.getUid()).child("restauarantAddress").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fieldStoreAddress.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        StoreBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch(StoreBanner,"banner");
            }
        });
        StoreProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch(StoreProfileImage,"profile");
            }
        });

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UpdateProfile.this,MapsProfileUpdateActivity.class);
                startActivity(i);
            }
        });

    }
    // ********************* Submitting Information ********************
    public void performFileSearch(ImageView imageView,String ImageType) {

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
        tempImage = imageView;
        imageType = ImageType;
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
                setImage(uri,tempImage);
                imageToUploadUri = uri;
                if (imageType.equals("banner")){
                    bannerUri = uri;
                }if (imageType.equals("profile")){
                    iconUri = uri;
                }
            }else {
                System.out.println("null?");
            }

        }

    }
    private void setImage(Uri uri,ImageView imageView){
        // floatClearImage.setVisibility(View.VISIBLE);
        // Picasso.with(CreatePostActivity.this).load(uri).resize(300,600).into(imageToUpload);

        Glide.with(UpdateProfile.this).load(uri).into(imageView);
        imageView.setColorFilter(null);


    }
    // ************** form Validation *******************
    private boolean validateForm(EditText storename,EditText storeAddress,EditText storeContact) {
        boolean valid = true;

        String name = storename.getText().toString();
        if (TextUtils.isEmpty(name)) {
            storename.setError("Required.");
            valid = false;
        } else {
            storename.setError(null);
        }

        String address = storeAddress.getText().toString();
        if (TextUtils.isEmpty(address)) {
            storeAddress.setError("Required.");
            valid = false;
        } else {
            storeAddress.setError(null);
        }

        String contact = storeContact.getText().toString();
        if (TextUtils.isEmpty(contact)) {
            storeContact.setError("Required.");
            valid = false;
        } else {
            storeContact.setError(null);
        }

        return valid;
    }

    //*************************************** Submit Profile Info **************************
    private void saveStoreProfile(String storename,String storeAddress,String storeContact,
                                  String storeBannerURL, String storeProfileIconUrl){

        String key = mAuth.getUid();
        StoreProfileInformationMap storeProfileInformationMap = new StoreProfileInformationMap(iconDownloadURL,
                bannerDownloadURL,storename,storeAddress,storeContact,key);
        Map<String,Object> postValue = storeProfileInformationMap.toMap();
        Map<String,Object> childUpdates = new HashMap<>();
        childUpdates.put(key,postValue);

        mDatabase.child("storeProfiles")
                .updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                toaster("success");
                Intent i = new Intent(UpdateProfile.this, InventoryRestaurant.class);
                startActivity(i);
            }
        });
    }




    private void uploadBanner(Uri ImageStorageURI, final String setToThis){
        if (ImageStorageURI!=null) {
            InputStream storeBannerFile = null;
            try {
                storeBannerFile = getContentResolver().openInputStream(ImageStorageURI);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
           try{
               StorageReference ImageStorageStore = mStorageRef.child("images/storeProfileImages" + File.separator+mAuth.getCurrentUser().getUid() + File.separator + getFileName(ImageStorageURI)
                       +storeBannerFile.toString()+File.separator+getFileName(ImageStorageURI));
               ImageStorageStore.putStream(storeBannerFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       @SuppressWarnings("VisibleForTests")
                       String dlURL = taskSnapshot.getDownloadUrl().toString();
                       bannerDownloadURL = dlURL;
                       uploadProfileIcon(iconUri,bannerDownloadURL);

                   }

               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       setProgress(false);
                       toaster("Saving Failed");
                   }
               });
           }catch (NullPointerException e){
                updateRestoCredentials();
               uploadProfileIcon(iconUri,bannerDownloadURL);
           }

        }
    }
    private void uploadProfileIcon(Uri ImageStorageURI, final String setToThis){
        if (ImageStorageURI!=null) {
            InputStream storeBannerFile = null;
            try {
                storeBannerFile = getContentResolver().openInputStream(ImageStorageURI);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
           try {
               StorageReference ImageStorageStore = mStorageRef.child("images/storeProfileImages" + File.separator+mAuth.getCurrentUser().getUid() + File.separator + getFileName(ImageStorageURI)
                       +storeBannerFile.toString()+File.separator+getFileName(ImageStorageURI));
               ImageStorageStore.putStream(storeBannerFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       @SuppressWarnings("VisibleForTests")
                       String dlURL = taskSnapshot.getDownloadUrl().toString();
                       setDownloadUrl(dlURL,iconDownloadURL);
                       iconDownloadURL = dlURL;
                       saveStoreProfile(fieldStorename.getText().toString(),fieldStoreAddress.getText().toString()
                               ,fieldStoreContact.getText().toString(),bannerDownloadURL,iconDownloadURL);

                   }

               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       setProgress(false);
                       toaster("Saving Failed");
                   }
               });
           }catch (NullPointerException e){
                updateRestoCredentials();
           }

        }
    }
    private void setDownloadUrl(String url,String setToThis){
        setToThis = url;
    }

    //******************* Utilities ******************

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

    private void toaster(String text){
        Toast.makeText(UpdateProfile.this,text,Toast.LENGTH_SHORT).show();
    }
    public void setProgress(boolean boo){
        if (boo){
            savingProgress.setVisibility(View.VISIBLE);
            progBlocker.setVisibility(View.VISIBLE);
        }else {
            savingProgress.setVisibility(View.INVISIBLE);
            progBlocker.setVisibility(View.INVISIBLE);
        }
    }

    private void updateRestoCredentials(){
      final DatabaseReference storeProfielDataBaseReference = mDatabase.child(Utils.storeProfiles).child(mAuth.getUid());

        storeProfielDataBaseReference.child(Utils.restaurantProfileItems.restoName).setValue(fieldStorename.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                storeProfielDataBaseReference.child(Utils.restaurantProfileItems.restoAddress).setValue(fieldStoreAddress.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        storeProfielDataBaseReference.child(Utils.restaurantProfileItems.storeContact).setValue(fieldStoreContact.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                             storeProfielDataBaseReference.child(Utils.restaurantProfileItems.storeBannerUrl).setValue(bannerDownloadURL).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     storeProfielDataBaseReference.child(Utils.restaurantProfileItems.storeProfileUrl).setValue(iconDownloadURL).addOnSuccessListener(new OnSuccessListener<Void>() {
                                         @Override
                                         public void onSuccess(Void aVoid) {
                                             Intent i = new Intent(UpdateProfile.this, InventoryRestaurant.class);
                                             startActivity(i);
                                             finish();
                                         }
                                     });
                                 }
                             });
                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                setProgress(false);
            }
        });


    }

}
