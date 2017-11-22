package com.pointofsalesandroid.androidbasedpos_inventory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ProfileManagement extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    Uri imageToUploadUri;
    EditText fieldStorename,fieldStoreAddress, fieldStoreContact;
    Button submitInformation;
    ImageView StoreBanner;
    ImageView StoreProfileImage;
    ImageView tempImage;
    Uri bannerURL,iconURL;
    String imageType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_management);
        submitInformation = (Button) findViewById(R.id.submitInformation);
        StoreBanner = (ImageView) findViewById(R.id.storeBanner);
        StoreProfileImage = (ImageView)findViewById(R.id.store_profile_icon);
        submitInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm(fieldStorename,fieldStoreAddress, fieldStoreContact)){

                }

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
                    bannerURL = uri;
                }if (imageType.equals("profile")){
                    iconURL = uri;
                }
            }else {
                System.out.println("null?");
            }

        }

    }
    private void setImage(Uri uri,ImageView imageView){
       // floatClearImage.setVisibility(View.VISIBLE);
       // Picasso.with(CreatePostActivity.this).load(uri).resize(300,600).into(imageToUpload);
        Glide.with(ProfileManagement.this).load(uri).into(imageView);

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

        String contact = storeAddress.getText().toString();
        if (TextUtils.isEmpty(contact)) {
            storeContact.setError("Required.");
            valid = false;
        } else {
            storeContact.setError(null);
        }

        return valid;
    }
    private void validateStoreType(){

    }
}
