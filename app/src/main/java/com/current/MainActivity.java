package com.current;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnPref, btnHome, btnFeed;
    ImageView img;
    TextView txtOut;
    HttpRequest r;
    JSONArray j;
    int c;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPref = this.findViewById(R.id.btnPref);
        btnHome = this.findViewById(R.id.btnHome);
        btnFeed = this.findViewById(R.id.btnFeed);
        txtOut = this.findViewById(R.id.txtOut);
        img = this.findViewById(R.id.img);
        btnPref.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnFeed.setOnClickListener(this);
        txtOut.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override public void onClick(View v) {
        if (v == btnPref) opnPref();
        else if (v == btnFeed) opnFeed();
        else if (v == btnHome) req(v);
    }

    public void opnPref() {
        Intent in = new Intent(this, SettingsActivity.class);
        startActivity(in);
    }

    public void opnFeed() {
        Intent in = new Intent(this, null);
        startActivity(in);
    }

    public void req(View v) {
        if (r == null) {
            r = new HttpRequest();
            r.execute("https://newsapi.org/v2/top-headlines?sources=bbc-news&apiKey=088fb1a3c9e3440db5b65f2c48c3f705");
            c = 0;
        }

        c++;

        JSONObject[] s;

        try {
            j = r.getResultAsJSON();
            s = new JSONObject[1000];

            txtOut.setText(R.string.loadingText);

            if (j == null) return;

            for (int i = 0; i < j.length(); i++)
                s[i] = j.getJSONObject(i);

            txtOut.setText(s[c].getString("title"));
            Glide.with(this).load(s[c].getString("urlToImage")).into(img);

        } catch (JSONException ignored) {
        }
    }
}
