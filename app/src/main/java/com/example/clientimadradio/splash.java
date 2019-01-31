package com.example.clientimadradio;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class splash extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef;
    int id;
    String img;
    String name;
    String desc;
    String url;
    int favorite;
    ContentValues contentValues;
    db_manager dbase;
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    InsertdataTask insertdataTask;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;
        contentValues = new ContentValues();
        dbase = new db_manager(this);
        sharedPreferences = getSharedPreferences("myshared", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("station");
        if (!sharedPreferences.getBoolean("first_time", false)) {
            editor.putBoolean("first_time", true);
            editor.commit();
            insertdataTask = new InsertdataTask();
            insertdataTask.doInBackground();
        } else startActivity(new Intent(context, MainActivity.class));


    }

    public void insert_in_sqlite() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    contentValues.clear();
                    id = Integer.parseInt(String.valueOf(childDataSnapshot.child("id").getValue()));
                    name = String.valueOf(childDataSnapshot.child("name").getValue());
                    desc = String.valueOf(childDataSnapshot.child("longDesc").getValue());
                    url = String.valueOf(childDataSnapshot.child("streamURL").getValue());
                    //img=String.valueOf(childDataSnapshot.child("streamURL").getValue());
                    contentValues.put("idS", id);
                    contentValues.put("nameS", name);
                    contentValues.put("img", "0");
                    contentValues.put("descS", desc);
                    contentValues.put("favorite", 0);
                    contentValues.put("urlS", url);
                    if (dbase.insertdb(contentValues)) {
                        //Toast.makeText(splash.this,"success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(splash.this, "feild", Toast.LENGTH_SHORT).show();
                        editor.putBoolean("first_time", false);
                        editor.commit();
                    }

                }

                startActivity(new Intent(context, MainActivity.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(splash.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //insert data
    public class InsertdataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

            insert_in_sqlite();
            return true;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);


        }
    }


}

