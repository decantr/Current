package com.current;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBUtil {
    SQLiteDatabase main;
    Context c;

    public DBUtil(Context c) {
        DBHandler db = new DBHandler(c);
        this.main = db.getWritableDatabase();
        this.c = c;
    }
}
