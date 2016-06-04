package com.example.manuelsanchez.udacitycapstone.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.manuelsanchez.udacitycapstone.R;
import com.example.manuelsanchez.udacitycapstone.util.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import static com.example.manuelsanchez.udacitycapstone.ui.EventLoader.*;


public class EventItemDetailFragment extends Fragment implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String DETAIL_URI = "detail_uri";

    private Event mEvent;
    private HeadLinerRecyclerViewAdapter mHeadLinerRecyclerViewAdapter;
    private MapView mMapView;

    private Cursor mCursor;
    private Uri uri;

    private TextView mVenueTextView;
    private TextView mDateTextView;
    private TextView mVenueAddress;
    private TextView mVenueCityStateZip;
    private ImageView mImageView;
    private RecyclerView mLineupRecyclerView;

    private boolean mTwoPane;

    public EventItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            uri = getArguments().getParcelable(ARG_ITEM_ID);
        }
        getLoaderManager().initLoader(EVENT_LOADER, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.eventitem_detail, container, false);

        mVenueTextView = ((TextView) rootView.findViewById(R.id.eventitem_detail_venue));
        mDateTextView = ((TextView) rootView.findViewById(R.id.eventitem_detail_date));
        mVenueAddress = ((TextView) rootView.findViewById(R.id.venue_address));
        mImageView = ((ImageView) rootView.findViewById(R.id.performer_thumb_nail));
        if (mImageView != null) {
            mTwoPane = true;
        }
        mVenueCityStateZip = ((TextView) rootView.findViewById(R.id.venue_city_state_zip));
        mLineupRecyclerView = (RecyclerView) rootView.findViewById(R.id.lineup);
        mMapView = (MapView) rootView.findViewById(R.id.venue_map);
        mMapView.onCreate(savedInstanceState);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latlng = new LatLng(mCursor.getDouble(COL_LATITUDE), mCursor.getDouble(COL_LONGITUDE));
        googleMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title(mCursor.getString(COL_VENUE)));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (uri != null) {
            return new CursorLoader(getActivity(),
                    uri,
                    EVENT_COLUMNS,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        if (data != null && data.moveToFirst()) {

            mMapView.getMapAsync(this);
            mVenueTextView.setText(data.getString(COL_VENUE));
            mDateTextView.setText(Utility.getFormattedDateString(data.getString(COL_DATE), true));

            if (mTwoPane) {
                String[] url = data.getString(COL_PERFORMER_URL).split(",");
                if (url.length > 0) {
                    Picasso.with(getActivity())
                        .load(url[0])
                        .into(mImageView);
                }
            }

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            ImageView imageView = (ImageView) activity.findViewById(R.id.toolbar_performer_thumbnail);
            if (appBarLayout != null) {
                appBarLayout.setTitle(data.getString(COL_PERFORMER));
                Picasso.with(activity)
                        .load(data.getString(COL_PERFORMER_URL).split(",")[0])
                        .into(imageView);
            }

            mVenueAddress.setText(data.getString(COL_VENUE_ADDRESS));
            String venueCityStatePost = data.getString(COL_VENUE_CITY) + ", " + data.getString(COL_REGION_ABBR) + " " + data.getString(COL_VENUE_POSTAL_CODE);
            mVenueCityStateZip.setText(venueCityStatePost);

            mHeadLinerRecyclerViewAdapter = new HeadLinerRecyclerViewAdapter(data.getString(COL_PERFORMER).split(","));
            mLineupRecyclerView.setAdapter(mHeadLinerRecyclerViewAdapter);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class HeadLinerRecyclerViewAdapter extends RecyclerView.Adapter<HeadLinerRecyclerViewAdapter.ViewHolder> {

        private String[] mValues;

        public HeadLinerRecyclerViewAdapter(String[] artists) {
            mValues = artists;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.performer_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mPerformer.setText(mValues[position]);
        }

        @Override
        public int getItemCount() {
            return mValues.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mPerformer;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mPerformer = (TextView) view.findViewById(R.id.performer);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mPerformer.getText() + "'";
            }
        }
    }
}
