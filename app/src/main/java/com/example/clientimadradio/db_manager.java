package com.example.clientimadradio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

//Create by abdoellah khalid
public class db_manager extends SQLiteOpenHelper {
    public static String db_name = "db_stations";
    Context context;

    public db_manager(Context context) {
        super(context, db_name, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String mySql = "create table stations (idS INTEGER,img TEXT,nameS TEXT,descS TEXT, urlS TEXT, favorite INTEGER)";
        db.execSQL(mySql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists stations");
        onCreate(db);
    }


    public boolean insertdb(ContentValues contentValues) {
        SQLiteDatabase db = getWritableDatabase();
        long insert = db.insert("stations", null, contentValues);
        if (insert == -1) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }

    public ArrayList<class_itm> getsations() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<class_itm> station = new ArrayList<>();
        Cursor res = db.rawQuery("select * from stations", null);
        res.moveToFirst();
        if (res == null) {

            Toast.makeText(context, "vide", Toast.LENGTH_SHORT).show();
            return null;
        }
        do {
            try {
                station.add(new class_itm(
                        res.getInt(0),
                        res.getString(1),
                        res.getString(2),
                        res.getString(3),
                        res.getString(4),
                        res.getInt(5)));
            } catch (Exception ex) {
                Toast.makeText(context, "station" + String.valueOf(ex), Toast.LENGTH_LONG).show();
            } finally {
                // Toast.makeText(context, String.valueOf(res.getCount()), Toast.LENGTH_SHORT).show();
            }

        } while (res.moveToNext());
        return station;
    }


    //get favorit
    public ArrayList<class_itm> getsationsfavorite() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<class_itm> stationfav = new ArrayList<>();
        Cursor res = db.rawQuery("select * from stations where favorite=1", null);
        res.moveToFirst();
        if (res == null) {
            Toast.makeText(context, "vide", Toast.LENGTH_SHORT).show();
            return null;
        } else {
            do {
                try {
                    stationfav.add(new class_itm(
                            res.getInt(0),
                            res.getString(1),
                            res.getString(2),
                            res.getString(3),
                            res.getString(4),
                            res.getInt(5)));
                } catch (Exception ex) {
                    //  Toast.makeText(context, "favo"+String.valueOf(ex), Toast.LENGTH_LONG).show();

                } finally {
                    // Toast.makeText(context, String.valueOf(res.getCount()), Toast.LENGTH_SHORT).show();
                }

            } while (res.moveToNext());
        }

        return stationfav;
    }

    public void updatefav(int id, int fav) {
        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL;
        if (fav == 1) {
            strSQL = "UPDATE stations SET favorite = 0 WHERE idS = " + id;
        } else strSQL = "UPDATE stations SET favorite = 1 WHERE idS = " + id;

        db.execSQL(strSQL);

    }


}
