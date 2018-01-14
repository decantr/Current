package com.current;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPref, btnHome, btnFeed;
    private ImageView img;
    private TextView txtTitle, txtDesc;
    private HttpRequest r;
    private int c;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPref = this.findViewById(R.id.btnPref);
        btnHome = this.findViewById(R.id.btnHome);
        btnFeed = this.findViewById(R.id.btnFeed);
        txtTitle = this.findViewById(R.id.txtTitle);
        txtDesc = this.findViewById(R.id.txtDesc);
        img = this.findViewById(R.id.img);
        btnPref.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnFeed.setOnClickListener(this);
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
            txtTitle.setText(R.string.loadingText);
        } else try {
            c++;
            JSONArray j = r.getResultAsJSON();

            if (j == null) return;

            txtTitle.setText(j.getJSONObject(c).getString("title"));
            Glide.with(this).load(j.getJSONObject(c).getString("urlToImage")).into(img);

        } catch (JSONException ignored) {}
    }
}
