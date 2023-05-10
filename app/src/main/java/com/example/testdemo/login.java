package com.example.testdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 10;
    private TextInputEditText user, pass;
    private TextView forgot,accreate;
    private Button logins , google;
    private ProgressBar progressBar ;
    private FirebaseAuth mAuth ;
    private GoogleSignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = findViewById(R.id.usertxt);
        pass = findViewById(R.id.passtxt);
        forgot = findViewById(R.id.btnfrogot);
        accreate = findViewById(R.id.btnregister);
        logins= findViewById(R.id.btnlogin);
        google = findViewById(R.id.btngoogle);
        progressBar = findViewById(R.id.progressBar2);

        progressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();

        forgot.setOnClickListener(v -> Toast.makeText(login.this, "Forgot Password?", Toast.LENGTH_SHORT).show());

        accreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(login.this,signup.class));
            }
        });


        logins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this,gso);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(login.this,MainActivity.class);
            startActivity(i);
        }
    }
    private void loginUserAccount()
    { progressBar.setVisibility(View.VISIBLE);

    String email, password;
    email = Objects.requireNonNull(user.getText()).toString();
    password = Objects.requireNonNull(pass.getText()).toString();

        if (TextUtils.isEmpty(email)) {
        Toast.makeText(getApplicationContext(),"Please enter email!!",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        return;
    }
        if (TextUtils.isEmpty(password)) {
        Toast.makeText(getApplicationContext(),"Please enter password!!",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        return;
    }

        mAuth.signInWithEmailAndPassword(email,password).
                addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                user.setText("");
                pass.setText("");
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),"Login successful!!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    Intent intent= new Intent(getApplicationContext(),Edit_profile.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Email",email);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Login failed!!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        });


    }

    private void signIn() {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account= task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),"Login successful!!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    Intent intent= new Intent(getApplicationContext(),Edit_profile.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Login failed!!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        });
    }

}