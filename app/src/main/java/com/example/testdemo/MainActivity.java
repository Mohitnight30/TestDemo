package com.example.testdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Random;
import java.util.StringJoiner;

public class MainActivity extends AppCompatActivity {

    ImageButton con, scan;
    ImageView qr;
    Button edit, logout, save, share;
    Bitmap bmp;
    String userphone;
    TextView uname, uphone, uemail;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    user_profile_details upd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        con = findViewById(R.id.btncontact);
        scan = findViewById(R.id.btnqrscan);
        qr = findViewById(R.id.qr_show);
        edit = findViewById(R.id.edit);
        logout = findViewById(R.id.btnlogout);
        save = findViewById(R.id.btnsave);
        share = findViewById(R.id.btnshare);

        uname = findViewById(R.id.disname);
        uemail = findViewById(R.id.disemail);
        uphone = findViewById(R.id.disphone);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            byte[] byteArray = bundle.getByteArray("QR");
            bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            qr.setImageBitmap(bmp);
        }

        getuserdata();
        uname.setText(currentUser.getDisplayName());
        uemail.setText(currentUser.getEmail());

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    try {
                        Uri qrCodeUri = Uri.parse(upd.getQruri());
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/JPEG");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, qrCodeUri);
                        startActivity(Intent.createChooser(shareIntent, "Share QR code"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        saveQRCode(Uri.parse(upd.getQruri()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }

            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, scanner.class);
                startActivity(i);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Edit_profile.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mAuth.signOut();
                    Intent i = new Intent(MainActivity.this,login.class);
                    startActivity(i);
            }
        });


    }

    private void getuserdata() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    upd = snapshot.getValue(user_profile_details.class);
                    uphone.setText(upd.getPnumber());
                    uname.setText(upd.getName());
                    Glide.with(MainActivity.this).load(upd.getQruri()).into(qr);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void saveQRCode(Uri imageUri) {
        String filename = System.currentTimeMillis() + ".jpg";
        OutputStream fos = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            Uri newImageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            try {
                fos = resolver.openOutputStream(newImageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = new File(imagesDir, filename);
            try {
                fos = new FileOutputStream(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (fos != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                }
                Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            Intent i = new Intent(MainActivity.this, login.class);
            startActivity(i);
        }
    }
}