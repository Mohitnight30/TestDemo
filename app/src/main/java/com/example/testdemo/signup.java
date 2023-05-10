package com.example.testdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class signup extends AppCompatActivity {

     TextInputEditText passinput ,emailinput,passwordinput;
     TextInputLayout laemail,lapass,lacpass;
     TextView haveac ;
     Button create;
     ProgressBar progressbar ;
     FirebaseAuth mAuth ;
    String password,email,fname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        passinput = findViewById(R.id.nametxt);
        emailinput = findViewById(R.id.emailtxt);
        passwordinput = findViewById(R.id.passinputtxt);
        haveac = findViewById(R.id.btnalreadyac);
        create= findViewById(R.id.btnnewac);
        progressbar = findViewById(R.id.progressBar);

        laemail=findViewById(R.id.emaillay);
        lapass=findViewById(R.id.passwordlay);
        lacpass=findViewById(R.id.cpasslay);

        mAuth = FirebaseAuth.getInstance();
        progressbar.setVisibility(View.GONE);

        haveac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),login.class);
                startActivity(i);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerNewUser();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(this,login.class));
        }
    }

    private void registerNewUser()
    {
        progressbar.setVisibility(View.VISIBLE);

        password = Objects.requireNonNull(passinput.getText()).toString();
        email = Objects.requireNonNull(emailinput.getText()).toString();
        fname = Objects.requireNonNull(passwordinput.getText()).toString();

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),"Please enter password!!",Toast.LENGTH_LONG).show();
            progressbar.setVisibility(View.GONE);
            return;
        }
        if (password.length()<5) {
            Toast.makeText(getApplicationContext(),"Please enter atlest 6 digit password!!",Toast.LENGTH_LONG).show();
            progressbar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),"Please enter email!!",Toast.LENGTH_LONG).show();
            progressbar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(fname)) {
            Toast.makeText(getApplicationContext(),"Please Enter name!!",Toast.LENGTH_LONG).show();
            progressbar.setVisibility(View.GONE);
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailinput.getText().toString()).matches()){
            laemail.setErrorEnabled(true);
            laemail.setError("Enter Correct E-mail");
        }

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    updateuser();
                    Toast.makeText(getApplicationContext(),"Registration successful!",Toast.LENGTH_LONG).show();
                    progressbar.setVisibility(View.GONE);
                    Intent intent= new Intent(getApplicationContext(),Edit_profile.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Registration failed!!"+ " Please try again later",Toast.LENGTH_LONG).show();
                    progressbar.setVisibility(View.GONE);
                }

            }
        });
    }

    private void updateuser() {
        UserProfileChangeRequest changeRequest= new UserProfileChangeRequest.Builder()
                .setDisplayName(fname)
                .build();
        mAuth.getCurrentUser().updateProfile(changeRequest);
    }

}