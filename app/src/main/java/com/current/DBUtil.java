package com.current;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

class DBUtil {
    SQLiteDatabase main;
    private Context c;

    /*
        creates our new db
    */
    DBUtil(Context c) {
        DBHandler db = new DBHandler(c);
        main = db.getWritableDatabase();
        this.c = c;
    }

    /*
        Method to return a multidimensional
            arraylist of arrays for our saved articles
    */
    ArrayList getArticles() {

        ArrayList<String[]> articles = new ArrayList<>();

        String[] t;
        Cursor cur = null;

        try {
            cur = main.query(DBHandler.getName(),
                    new String[]{"source_name, title, description, url, image"},
                    null, null,
                    null, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (cur != null) while (cur.moveToNext()) {
            t = new String[5];

            for (int j = 0; j < 5; j++)
                t[j] = cur.getString(j);

            articles.add(t);
        }

        if (cur != null) cur.close();
        return articles;
    }

    /*
        method to save the the information provided as an article
        @n source_name
        @t title
        @d description
        @u url
        @i urlToImage

        returns -1 if failed or the index of the new row
    */
    int saveArticle(String n, String t, String d, String u, String i) {

        ContentValues a = new ContentValues();

        String[] h = {"source_name", "title", "description", "url", "image"};
        String[] c = {n, t, d, u, i};

        for (int j = 0; j < 5; j++)
            a.put(h[j], c[j]);

        return (int) main.insert(DBHandler.getName(), null, a);

    }

    /*
        method to delete the article described by the title
        @t title

        returns -1 if failed or the index of the removed row
        String.valueOf(t) is import to prevent single quotes in titles
            messing with the select
    */
    int deleteArticle(String t) {
            return main.delete("main", "title=?",
                    new String[] { String.valueOf(t) });
    }
}
