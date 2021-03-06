package com.pointofsalesandroid.androidbasedpos_inventory.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pointofsalesandroid.androidbasedpos_inventory.ProfileManagement;
import com.pointofsalesandroid.androidbasedpos_inventory.R;
import com.pointofsalesandroid.androidbasedpos_inventory.Restaurant.InventoryRestaurant;
import com.wang.avi.AVLoadingIndicatorView;

public class LogInActivity extends AppCompatActivity {
    ConstraintLayout parent;
    Button googleLoginButton;
    DatabaseReference mDatabase;
    FirebaseAuth mAunt;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    RelativeLayout prog;
    AVLoadingIndicatorView avi;

    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        prog = (RelativeLayout)findViewById(R.id.prog);
        parent = (ConstraintLayout) findViewById(R.id.login_parent_layout);
        parent.setPadding(0, getStatusBarHeight(), 0, 0);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        googleLoginButton = (Button) findViewById(R.id.logInGoogle);
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



    }

    // ************************************** Facebook Log In  ***************************************************


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        setProgress(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                setProgress(false);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            mDatabase.keepSynced(true);
                            /*Intent i  = new Intent(LogInActivity.this,ProfileManagement.class);
                            startActivity(i);*/
                            mDatabase.child("storeProfiles").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() == null) {
                                        Intent i = new Intent(LogInActivity.this, ProfileManagement.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Intent i = new Intent(LogInActivity.this, InventoryRestaurant.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            mDatabase.keepSynced(true);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            setProgress(false);
                        }
                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!=null){
            final FirebaseUser user = mAuth.getCurrentUser();
            mDatabase.keepSynced(true);
            setProgress(true);
            mDatabase.child("storeProfiles").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null){
                        Intent i = new Intent(LogInActivity.this,ProfileManagement.class);
                        startActivity(i);
                        finish();
                    }else {
                       Intent i =  new Intent(LogInActivity.this,InventoryRestaurant.class);
                       startActivity(i);
                       finish();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    setProgress(false);
                }
            });
        }
    }

    // ************************************** Utilities *****************************************************


    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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
