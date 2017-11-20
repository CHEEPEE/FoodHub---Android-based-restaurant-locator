package com.pointofsalesandroid.androidbasedpos_inventory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ProfileManagement extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    Uri imageToUploadUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_management);
    }


    // ********************* Submitting Information ********************

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        //intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
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
                setImage(uri);
                imageToUploadUri = uri;
            }else {
                System.out.println("null?");
            }

        }

    }
    private void setImage(Uri uri){
       // floatClearImage.setVisibility(View.VISIBLE);
       // Picasso.with(CreatePostActivity.this).load(uri).resize(300,600).into(imageToUpload);

    }
    // ************** form Validation *******************
    private boolean validateForm(EditText title,EditText content) {
        boolean valid = true;

        String Title = title.getText().toString();
        if (TextUtils.isEmpty(Title)) {
            title.setError("Required.");
            valid = false;
        } else {
            title.setError(null);
        }

        String Content = content.getText().toString();
        if (TextUtils.isEmpty(Content)) {
            content.setError("Required.");
            valid = false;
        } else {
            content.setError(null);
        }

        return valid;
    }
}
