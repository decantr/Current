package com.current;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    //    vars for class
    private int c;
    private DBUtil db;
    private ArrayList a;
    private String[] curDisplay;

    //    ui elements
    private Button btnNext, btnPrev;
    private ImageView img;
    private TextView txtTitle, txtDesc, txtMore;
    private ProgressBar barPage;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
//        boiler plate
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_feed );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        if ( getSupportActionBar() != null )
            getSupportActionBar().setDisplayHomeAsUpEnabled( true );


//        database
        db = new DBUtil( this );

//        buttons
        btnNext = this.findViewById( R.id.btnNext );
        btnNext.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                loop( true );
                doDisplay();
            }
        } );
        btnPrev = this.findViewById( R.id.btnPrev );
        btnPrev.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                loop( false );
                doDisplay();
            }
        } );
        ImageButton btnDelete = this.findViewById( R.id.btnDelete );
        btnDelete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                int rtn;
                if ( delete() ) {
                    rtn = R.string.deleted;
                } else rtn = R.string.nodeleted;
                Snackbar.make( view, rtn, Snackbar.LENGTH_SHORT )
                        .setAction( "Action", null ).show();
            }
        } );

//        clickable ui
        ConstraintLayout cl = this.findViewById( R.id.cl );
        cl.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                try {
                    startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( curDisplay[3] ) ) );
                } catch ( Exception e ) {
                    toast( "No URL for this article" );
                }
            }
        } );

//        text views
        txtTitle = this.findViewById( R.id.txtTitle );
        txtDesc = this.findViewById( R.id.txtDesc );
        txtMore = this.findViewById( R.id.txtMore );
//        paging bar
        barPage = this.findViewById( R.id.barPage );
//        image view
        img = this.findViewById( R.id.img );

//        build ui
        reload();

    }

    /*
        method to handle creation and deletion of ui
    */
    private void reload() {

        if ( db.getArticles().size() == 0 ) finish();

        a = db.getArticles();
        loop( false );
        doDisplay();

    }

    /*
        method to toast before finishing
        if we run out of articles to show we kill the intent here
    */
    @Override
    public void finish() {

        toast( "You have no saved articles" );
        super.finish();

    }

    /*
        ensure that c, our article counter, is always between zero
        and the amount of articles we got back from the api
        @t indicates where we want to increase or decrease the counter
    */
    public void loop( boolean t ) {

        if ( t && c != a.size() - 1 ) c++;
        else if ( !t && c != 0 ) c--;

        btnSwtch();

    }

    /*
        turn off the correct button when we reach max or hit zero
    */
    private void btnSwtch() {

        btnPrev.setEnabled( true );
        btnPrev.setEnabled( true );

        if ( c == 0 )
            btnPrev.setEnabled( false );
        if ( c == a.size() - 1 )
            btnNext.setEnabled( false );

    }

    /*
        helper method to display the text passed in
        @t string to be displayed
    */
    void toast( String t ) {
        Toast.makeText( this, t, Toast.LENGTH_SHORT ).show();
    }

    /*
        handle displaying the article to the main page
        first creates all default values then creates t inside
        a try catch incase of failure.
        if any values are returned as null from the current article then they
        are left blank.
    */
    private void doDisplay() {

        if ( a.size() != 0 )
            curDisplay = (String[]) a.get( c );

        String title = "";
        String desc = "";
        String image = "http://via.placeholder.com/350x150";
        String more = "";

        if ( curDisplay != null ) {
            if ( curDisplay[1] != null ) title = curDisplay[1];

            if ( curDisplay[2] != null ) desc = curDisplay[2];

            if ( curDisplay[3] != null ) more = "Tap to read more";

            if ( curDisplay[4] != null ) image = curDisplay[4];
        }

        barPage.setMax( a.size() - 1 );
        barPage.setProgress( c );

        txtTitle.setText( title );
        txtDesc.setText( desc );
        txtMore.setText( more );

        Glide.with( this ).load( image ).into( img );

    }

    /*
        handles deleting the current article from the db
    */
    boolean delete() {
        if ( db.deleteArticle( curDisplay[1] ) == 1 ) {
            reload();
            return true;
        } else return false;
    }
}
