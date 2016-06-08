package com.example.manuelsanchez.udacitycapstone.ui.search;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.manuelsanchez.udacitycapstone.R;

public class ArtistDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(ArtistDetailActivityFragment.ARG_ITEM_ID,
                    getIntent().getParcelableExtra(ArtistDetailActivityFragment.ARG_ITEM_ID));
            ArtistDetailActivityFragment fragment = new ArtistDetailActivityFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.artist_detail_container, fragment)
                    .commit();
        }
    }

}
