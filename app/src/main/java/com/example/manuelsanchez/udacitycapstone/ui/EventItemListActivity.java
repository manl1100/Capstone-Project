package com.example.manuelsanchez.udacitycapstone.ui;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manuelsanchez.udacitycapstone.R;
import com.example.manuelsanchez.udacitycapstone.data.EventContract;
import com.example.manuelsanchez.udacitycapstone.ui.search.ArtistSearchActivity;
import com.example.manuelsanchez.udacitycapstone.ui.search.ArtistSearchActivityFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Permission;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.example.manuelsanchez.udacitycapstone.data.EventContract.*;


public class EventItemListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String LOG_TAG = EventItemListActivity.class.getSimpleName();

    private boolean mTwoPane;
    private RecyclerView recyclerView;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    LocationRequest mLocationRequest;

    private static final int REQUEST_COARSE_LOCATION_PERMISSION = 0;

    public static final int COL_EVENT_ID = 0;
    public static final int COL_PERFORMER = 1;
    public static final int COL_VENUE = 2;
    public static final int COL_LATITUDE = 3;
    public static final int COL_LONGITUDE = 4;
    public static final int COL_DATE = 5;
    public static final int COL_COUNTRY = 6;
    public static final int COL_REGION = 7;
    public static final int COL_REGION_ABBR = 8;
    public static final int COL_VENUE_CITY = 9;
    public static final int COL_VENUE_ADDRESS = 10;
    public static final int COL_VENUE_POSTAL_CODE = 11;

    // TODO move to its own file
    public static final String[] EVENT_COLUMNS = {
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_EVENT_ID,
            PerformerEntry.COLUMN_PERFORMER_NAME,
            EventEntry.COLUMN_VENUE,
            EventEntry.COLUMN_COORD_LATITUDE,
            EventEntry.COLUMN_COORD_LONGITUDE,
            EventEntry.COLUMN_DATE,
            EventEntry.COLUMN_COUNTRY,
            EventEntry.COLUMN_REGION,
            EventEntry.COLUMN_REGION_ABBR,
            EventEntry.COLUMN_VENUE_CITY,
            EventEntry.COLUMN_VENUE_ADDRESS,
            EventEntry.COLUMN_VENUE_POSTAL_CODE,
    };

    public static final String[] PERFORMER_EVENT_COLUMNS = {
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_EVENT_ID,
            EventEntry.COLUMN_VENUE,
            EventEntry.COLUMN_COORD_LATITUDE,
            EventEntry.COLUMN_COORD_LONGITUDE,
            EventEntry.COLUMN_DATE,
            EventEntry.COLUMN_COUNTRY,
            EventEntry.COLUMN_REGION,
            EventEntry.COLUMN_REGION_ABBR,
            EventEntry.COLUMN_VENUE_CITY,
            EventEntry.COLUMN_VENUE_ADDRESS,
            EventEntry.COLUMN_VENUE_POSTAL_CODE,
    };

    public static final int EVENT_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventitem_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTwoPane) {
                    ArtistSearchActivityFragment fragment = new ArtistSearchActivityFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.eventitem_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ArtistSearchActivity.class);
                    context.startActivity(intent);
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.eventitem_list);

        if (findViewById(R.id.eventitem_detail_container) != null) {
            mTwoPane = true;
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        getLoaderManager().initLoader(EVENT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            new EventAsyncTask(this).execute("75209");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri eventUri = EventEntry.buildEventUriWithDateAndLocation("Dallas", System.currentTimeMillis());
        return new CursorLoader(getApplicationContext(),
                eventUri,
                EVENT_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        SimpleItemRecyclerViewAdapter adapter = new SimpleItemRecyclerViewAdapter(data);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recyclerView.setAdapter(null);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_COARSE_LOCATION_PERMISSION);
            }
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            handleNewLocation(mLastLocation);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLastLocation != null) {
                        handleNewLocation(mLastLocation);
                    } else {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    }
                }
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(LOG_TAG, "onConnectionSuspended");
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        String lat = String.valueOf(mLastLocation.getLatitude());
        String lon = String.valueOf(mLastLocation.getLongitude());
        Toast.makeText(getApplicationContext(), "Lat: " + lat + "; Long: " + lon, Toast.LENGTH_LONG).show();
    }


    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private Cursor mCursor;

        public SimpleItemRecyclerViewAdapter(Cursor cursor) {
            mCursor = cursor;
        }

        private String getEventId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getString(COL_EVENT_ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.eventitem_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            mCursor.moveToPosition(position);

            holder.mContentView.setText(mCursor.getString(COL_VENUE));
            holder.mIdView.setText(mCursor.getString(COL_PERFORMER));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(EventItemDetailFragment.ARG_ITEM_ID, EventContract.EventEntry.buildEventUriWithId(getEventId(holder.getAdapterPosition())));
                        EventItemDetailFragment fragment = new EventItemDetailFragment();
                        fragment.setArguments(arguments);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.eventitem_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, EventItemDetailActivity.class);
                        intent.putExtra(EventItemDetailFragment.ARG_ITEM_ID, EventContract.EventEntry.buildEventUriWithId(getEventId(holder.getAdapterPosition())));
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Event mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}
