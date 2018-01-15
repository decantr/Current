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
        return "main";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE TABLE " + DB_NAME +
                        "(id integer primary key autoincrement not null, " +
                        "source_name text not null, " +
                        "title text not null, " +
                        "description text, " +
                        "url text, " +
                        "image text);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
