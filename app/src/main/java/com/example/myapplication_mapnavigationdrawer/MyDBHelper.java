package com.example.myapplication_mapnavigationdrawer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper{
    private    static  final String name = "mdatabase.db"; //資料庫名稱
    private    static final int version = 1;//資料庫版本

    //自訂建構子，只需傳入一個context物件即可
    public MyDBHelper(Context context){
        super(context,name,null,version);
    }
    @Override
    public void  onCreate(SQLiteDatabase db){

        db.execSQL("CREATE TABLE myTable(favorite_id text PRIMARY KEY,favorite_name text NOT NULL," +
                "favorite_lat text NOT NULL,favorite_lng text NOT NULL,favorite_price text NOT NULL,favorite_address text NOT NULL)");
    }
    @Override
    public  void  onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion){

        db.execSQL("DROP TABLE IF EXISTS myTable");

        onCreate(db);
    }
}
