package com.current;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Feed extends AppCompatActivity {

    private int c;
    private DBUtil db;
    private ArrayList a;
    private String[] curDisplay;

    private Button btnNext, btnPrev;
    private ImageButton btnDelete;
    private ImageView img;
    private TextView txtTitle, txtDesc, txtMore;
    private ProgressBar barPage;
    private ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtTitle = this.findViewById(R.id.txtTitle);
        txtDesc = this.findViewById(R.id.txtDesc);
        txtMore = this.findViewById(R.id.txtMore);

        barPage = this.findViewById(R.id.barPage);

        img = this.findViewById(R.id.img);

        cl = this.findViewById(R.id.cl);
        cl.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(curDisplay[3])));
                } catch (Exception e) {
                    toast("No URL for this article");
                }
            }
        });

        btnNext = this.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                loop(true);
                doDisplay();
            }
        });
        btnPrev = this.findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                loop(false);
                doDisplay();
            }
        });
        btnDelete = this.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                int rtn;
                if (delete()) {
                    rtn = R.string.deleted;
                } else rtn = R.string.nodeleted;
                Snackbar.make(view, rtn, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        db = new DBUtil(this);

        reload();
    }

    private void reload() {
        a = db.getArticles();
        if (a.size() == 0) finish();
        loop(false);
        doDisplay();
    }

    @Override public void finish() {
        toast("You have no saved articles");
        super.finish();
    }

    public void loop(boolean t) {
        if (t && c != a.size() - 1) c++;
        else if (c != 0) c--;
        btnSwtch();
    }

    private void btnSwtch() {
        btnPrev.setEnabled(true);
        btnPrev.setEnabled(true);
        if (c == 0)
            btnPrev.setEnabled(false);
        if (c == a.size() - 1 || a.size() == 1)
            btnNext.setEnabled(false);
    }

    void toast(String t) {
        Toast.makeText(this, t, Toast.LENGTH_SHORT).show();
    }

    private void doDisplay() {
        if (a.size() != 0)
        curDisplay = (String[]) a.get(c);
        Log.e("meme", "reload: z");
        String title = "";
        String desc = "";
        String image = "http://via.placeholder.com/350x150";
        String more = "";
        Log.e("meme", "reload: x");
        if (curDisplay != null) {
            if (curDisplay[1] != null) title = curDisplay[1];

            if (curDisplay[2] != null) desc = curDisplay[2];

            if (curDisplay[3] != null) more = "Tap to read more";

            if (curDisplay[4] != null) image = curDisplay[4];
        }
        Log.e("meme", "reload: y");
        barPage.setMax(a.size() - 1);
        barPage.setProgress(c);

        txtTitle.setText(title);
        txtDesc.setText(desc);
        txtMore.setText(more);
        Glide.with(this).load(image).into(img);
    }

    boolean delete() {
        if (db.deleteArticle(curDisplay[1]) == 1) {
            reload();
            return true;
        }
        else return false;
    }
}
