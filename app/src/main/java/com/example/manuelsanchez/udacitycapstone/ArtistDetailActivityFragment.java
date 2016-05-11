package com.example.manuelsanchez.udacitycapstone;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.manuelsanchez.udacitycapstone.data.EventContract;

import java.util.ArrayList;
import java.util.List;


public class ArtistDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ITEM_ID = "item_id";
    private TourDateRecyclerViewAdapter mTourDateRecyclerViewAdapter;
    private Artist mArtist;
    private RecyclerView mRecyclerView;
    private String performerId;

    public ArtistDetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mArtist = getArguments().getParcelable(ARG_ITEM_ID);
            if (mArtist != null) {
                performerId =  mArtist.getEventfulArtistId();
            }
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mArtist.getArtistName());
            }
        }

        getLoaderManager().initLoader(EventItemListActivity.EVENT_LOADER, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_detail, container, false);

        if (mArtist != null) {
            ((TextView) view.findViewById(R.id.performing_artist)).setText(mArtist.getArtistName());
            mRecyclerView = (RecyclerView) view.findViewById(R.id.tour_dates);
            new EventDetailAsyncTask(getActivity()).execute(mArtist.getEventfulArtistId());
        }

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri eventUri = EventContract.PerformerEntry.buildEventUriWithPerformerId(performerId);
        return new CursorLoader(getActivity(),
                eventUri,
                EventItemListActivity.PERFORMER_EVENT_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        TourDateRecyclerViewAdapter adapter = new TourDateRecyclerViewAdapter(data);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    public class TourDateRecyclerViewAdapter extends RecyclerView.Adapter<TourDateRecyclerViewAdapter.ViewHolder> {

        private Cursor cursor;

        public TourDateRecyclerViewAdapter(Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tour_date_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            cursor.moveToPosition(position);

            holder.performanceDate.setText(DateUtil.getFormattedDateString(cursor.getString(EventItemListActivity.COL_DATE)));
            holder.venue.setText(cursor.getString(1));
            holder.location.setText(cursor.getString(4));
        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView performanceDate;
            public final TextView venue;
            public final TextView location;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                performanceDate = (TextView) view.findViewById(R.id.performance_date);
                venue = (TextView) view.findViewById(R.id.venue_name);
                location = (TextView) view.findViewById(R.id.venue_location);
            }
        }
    }
}
