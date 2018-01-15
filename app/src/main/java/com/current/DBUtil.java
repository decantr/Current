package com.current;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBUtil {
    SQLiteDatabase main;
    Context c;

    public DBUtil(Context c) {
        DBHandler db = new DBHandler(c);
        this.main = db.getWritableDatabase();
        this.c = c;
    }

    public ArrayList getArticles() {
        int dbSize = (int) this.c.getDatabasePath(DBHandler.getName()).length();

        ArrayList<String[]> articles = new ArrayList<>();

        String[] t;
        Cursor cur = null;

        try {
            cur = main.query(DBHandler.getName(),
                    new String[]{"source_name, title, description, url, image"},
                    null, null,
                    null, null, null);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < dbSize; i++) {
            t = new String[5];

            for (int j = 0; j < 5; j++)
                t[j] = cur.getString(j);

            articles.add(t);
        }

        return articles;
    }

    void saveArticle(String n, String t, String d, String u, String i){
        ContentValues a = new ContentValues();

        String[] h = {"source_name", "title", "description", "url", "image"};
        String[] c = {n, t, d, u, i};

        for (int j = 0; j < 5; j++)
            a.put(h[j], c[j]);

        this.main.insert(DBHandler.getName(), null, a);
    }
}
