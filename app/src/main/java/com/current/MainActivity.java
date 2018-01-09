package com.current;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnPref;
    Button btnHome;
    Button btnFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPref = (Button) this.findViewById(R.id.btnPref);
        btnPref.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnPref) opnPref();
        else if (v == btnFeed) opnFeed();
    }

    public void opnPref() {
        Intent in = new Intent(this, SettingsActivity.class);
        startActivity(in);
    }

    public void opnFeed() {
        Intent in = new Intent(this, null);
        startActivity(in);
    }
}
