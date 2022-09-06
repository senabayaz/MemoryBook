package com.example.memorybook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import com.example.memorybook.databinding.ActivityMainBinding;

import java.util.ArrayList;

import dalvik.system.InMemoryDexClassLoader;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    ArrayList<Model> modelList;
    Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        modelList = new ArrayList<Model>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(modelList);
        binding.recyclerView.setAdapter(adapter);

        sqlDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(MainActivity.this,Art.class);
        finish();
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private void sqlDatabase(){

        try{
            SQLiteDatabase data = this.openOrCreateDatabase("memories",MODE_PRIVATE,null);
            Cursor cursor = data.rawQuery("SELECT * FROM memory",null);
            int idIx = cursor.getColumnIndex("id");
            int titleIx = cursor.getColumnIndex("title");

            while(cursor.moveToNext()){
                int id = cursor.getInt(idIx);
                String title = cursor.getString(titleIx);
                Model model = new Model(id,title);
                modelList.add(model);
            }
            adapter.notifyDataSetChanged();
            cursor.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}