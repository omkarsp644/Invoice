package com.svttechnology.invoice.activities.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.signin.SignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.svttechnology.invoice.R;

import java.util.concurrent.TimeUnit;

public class PhoneAuthentication extends AppCompatActivity implements View.OnClickListener {

    GoogleSignInOptions gso;
    private FirebaseAuth mAuth;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private CardView googleSignin,phoneAuthentication;

    private TextInputEditText fieldPhoneNumber;


    //----- Phone Auth-----
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);

        phoneAuthentication=(CardView)findViewById(R.id.phoneAuthentication);
        fieldPhoneNumber=(TextInputEditText) findViewById(R.id.fieldPhoneNumber);
        SigninInitializationGoogle();
        SigninInitializationPhone();


        googleSignin=(CardView)findViewById(R.id.GoogleSignin);
        googleSignin.setOnClickListener(this);
        phoneAuthentication.setOnClickListener(this);


    }

    private void SigninInitializationPhone() {

            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    mVerificationInProgress = false;
                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(getApplicationContext(),"Phone Authentication failed...",Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {

                    mVerificationId = verificationId;
                    mResendToken = token;

                }
            };
        }

    private void SigninInitializationGoogle() {
        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.my_api_key_google_signin))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(fieldPhoneNumber.getText().toString());
        }

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(getApplicationContext(),"Phone Authentication Successfully Done!!!",Toast.LENGTH_SHORT).
                                    show();
                        }
                    }
                });
    }
    private boolean validatePhoneNumber() {
        String phoneNumber = fieldPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            fieldPhoneNumber.setError("Invalid phone number.");
            return false;
        }


        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(),"Google Signin Failed :"+e.getMessage(),Toast.LENGTH_SHORT).show();
                Log.d("Google Signin", String.valueOf(e));
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),"Signin Successfull...",Toast.LENGTH_SHORT).show();
                        startHome();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Snackbar.make(getCurrentFocus(), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void startHome() {
        startActivity(new Intent(PhoneAuthentication.this,Scan.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.GoogleSignin:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case R.id.phoneAuthentication:
                if (!validatePhoneNumber()) {
                    return;
                }
                startPhoneNumberVerification("+91"+fieldPhoneNumber.getText().toString());
                break;
        }
    }
}