package com.example.testdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.StringJoiner;

public class Edit_profile extends AppCompatActivity {

    Button gen;
    TextInputEditText name,email,phone,address,github;
    TextInputLayout lname,lemail,lphone,laddress,lgithub;
    ProgressBar progressBar;
    ImageView imageView;
    String myText="";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        gen= findViewById(R.id.save_genrate);
        name=findViewById(R.id.inname);
        email=findViewById(R.id.inemail);
        phone=findViewById(R.id.inphone);
        address=findViewById(R.id.inaddress);
        github=findViewById(R.id.ingithub);
        imageView=findViewById(R.id.user_photo);

        lname=findViewById(R.id.layoutname);
        lemail=findViewById(R.id.layoutemail);
        lphone=findViewById(R.id.layoutphone);
        laddress=findViewById(R.id.layoutaddress);
        lgithub=findViewById(R.id.layoutgihub);

        progressBar=findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
        String userEmail=bundle.getBundle("Email").toString();
        email.setText(userEmail);
        email.setEnabled(false);
        }

        email.setText(firebaseUser.getEmail());
        name.setText(firebaseUser.getDisplayName());

         if(!TextUtils.isEmpty(name.getText().toString())){
             lname.setHelperTextEnabled(false);
         }

        gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(phone.getText().toString())&&TextUtils.isEmpty(name.getText().toString())&&TextUtils.isEmpty(email.getText().toString())){
                    Toast.makeText(Edit_profile.this, "Fill The Details", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                        lemail.setErrorEnabled(true);
                        lemail.setError("Enter Correct E-mail");
                    }
                    else{progressBar.setVisibility(View.VISIBLE);
                        genrate_save();}
                }
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lname.setHelperTextEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lphone.setHelperTextEnabled(false);
                lphone.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(phone.getText().toString())){
                    lphone.setErrorEnabled(true);
                    lphone.setError("Please fill");
                }
            }
        });

    }

    void genrate_save(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            StringJoiner joiner = new StringJoiner("$");
            joiner.add(name.getText().toString().trim()).add(email.getText().toString().trim()).add(phone.getText().toString().trim())
                    .add( github.getText().toString().trim()).add(address.getText().toString().trim());
            myText=joiner.toString();
        }
        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            BitMatrix mMatrix = mWriter.encode(myText, BarcodeFormat.QR_CODE, 800,800);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
//            String qrImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef=storage.getReference();

            StorageReference mountainsRef = storageRef.child("genrated_QR/"+firebaseUser.getEmail()+".jpg");
            UploadTask uploadTask = mountainsRef.putBytes(byteArray);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri qruri=uri;
                            String user_name=name.getText().toString();
                            String user_email=email.getText().toString();
                            String user_phone= phone.getText().toString();
                            String user_address=address.getText().toString();
                            String user_github=github.getText().toString();

                            user_profile_details upd=new user_profile_details(user_name,user_phone,user_address,user_github,qruri.toString());

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                            reference.child(firebaseUser.getUid()).setValue(upd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(Edit_profile.this, "Data save Succrssfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), " Please try again later", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

            Intent i = new Intent(Edit_profile.this,MainActivity.class);
            i.putExtra("QR",byteArray);
            startActivity(i);

        }
            catch (WriterException e) {
                e.printStackTrace();
        }
    }
}