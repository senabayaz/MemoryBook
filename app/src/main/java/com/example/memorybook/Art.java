package com.example.memorybook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.memorybook.databinding.ActivityArtBinding;
import com.example.memorybook.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class Art extends AppCompatActivity {
    private ActivityArtBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionResultLauncher;
    Bitmap bitmap;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art);

        binding = ActivityArtBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        permissionRegister();
        database = this.openOrCreateDatabase("memories",MODE_PRIVATE,null);

    }

    public void clickImg(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else{
            Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        }
    }

    public void permissionRegister(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intentG = result.getData();
                    if(intentG != null){
                        Uri uriImg = intentG.getData();
                        try{
                            if(Build.VERSION.SDK_INT >= 28){
                                ImageDecoder.Source source = ImageDecoder.createSource(Art.this.getContentResolver(),uriImg);
                                bitmap = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(bitmap);
                            }else{
                                bitmap = MediaStore.Images.Media.getBitmap(Art.this.getContentResolver(),uriImg);
                                binding.imageView.setImageBitmap(bitmap);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        permissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intent);
                }else{
                    Toast.makeText(Art.this,"Permission denied",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void saveImg(View view){

        String title = binding.editTextTextPersonName.toString();
        String day = binding.editTextTextPersonName2.toString();
        Bitmap image = smallerImg(bitmap,300);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArr = outputStream.toByteArray();

        try{
            database = this.openOrCreateDatabase("memories",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS memory(id INTEGER PRIMARY KEY,title VARCHAR,day VARCHAR,image BLOB)");
            String sqlS = "INSERT INTO memories VALUES (?,?,?,?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlS);
            sqLiteStatement.bindString(1,title);
            sqLiteStatement.bindString(2,day);
            sqLiteStatement.bindBlob(3,byteArr);
            sqLiteStatement.execute();

        }catch(Exception e){
            e.printStackTrace();
        }

        Intent intentAc = new Intent(Art.this,MainActivity.class);
        intentAc.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentAc);
    }

    public Bitmap smallerImg(Bitmap img,int size){
        int width = img.getWidth();
        int height = img.getHeight();
        float ratio = (float) width / (float) height;
        if(ratio > 1){
            width = size;
            height = (int) (width / ratio);
        }else{
            height = size;
            width = (int) (width * ratio);
        }
        return Bitmap.createScaledBitmap(img,width,height,true);
    }

}