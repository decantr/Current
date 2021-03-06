package com.current;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "main";
    private static final int DB_VERSION = 1;

    DBHandler(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }

    static String getName() {
        return DB_NAME;
    }

    /*
        create the database
        using title as pk as a dirty way to prevent duplicates
    */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE TABLE " + DB_NAME +
                        "(source_name text not null, " +
                        "title text primary key not null, " +
                        "description text, " +
                        "url text, " +
                        "image text);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
