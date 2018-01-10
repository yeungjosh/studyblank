package com.example.bao48.studyblank;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class home extends AppCompatActivity {

    private static final String TAG = "home";

    private EditText emailField;
    private EditText passwordField;


    // declare authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configure signin to request ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // create an account button
        findViewById(R.id.createaccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                goToCreateAccount();
            }
        });

        // get text fields
        emailField = (EditText) findViewById(R.id.email);
        passwordField = (EditText) findViewById(R.id.password);

        // submit
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(emailField.getText().toString(), passwordField.getText().toString());
            }
        });


        // initialize authentication
        mAuth = FirebaseAuth.getInstance();
    }

    private void signIn(String emailSigningIn, String passwordSigningIn) {
        Log.d(TAG, "signIn:" + emailSigningIn);
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(emailSigningIn, passwordSigningIn)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // success, update UI with signed-in user's info
                            Log.d(TAG, "signInWithEmail: success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            gotoMainActivity();

                        } else {
                            // unsuccessful
                            Log.w(TAG, "signInWithEmail: Faileure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean validateForm() {
        boolean valid = true;

        String emailSigningIn = emailField.getText().toString();
        String passwordSigningIn = passwordField.getText().toString();

        if (TextUtils.isEmpty(emailSigningIn)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        if (TextUtils.isEmpty(passwordSigningIn)) {
            passwordField.setError("Required");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;

    }

    private void goToCreateAccount() {
        Intent intent = new Intent(this, createAccount.class);
        startActivity(intent);
    }

    // Checks user
    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non null) and update UI access
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // stuff happens
        }
    }
}
