package com.example.manuelsanchez.udacitycapstone;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ArtistDetailActivityFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    private TourDateRecyclerViewAdapter mTourDateRecyclerViewAdapter;
    private Artist mArtist;

    public ArtistDetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mArtist = getArguments().getParcelable(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mArtist.getArtistName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_detail, container, false);

        if (mArtist != null) {
            ((TextView) view.findViewById(R.id.performing_artist)).setText(mArtist.getArtistName());
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tour_dates);
            mTourDateRecyclerViewAdapter = new TourDateRecyclerViewAdapter();
            recyclerView.setAdapter(mTourDateRecyclerViewAdapter);
            new EventDetailAsyncTask(getContext(), mTourDateRecyclerViewAdapter).execute(mArtist.getEventfulArtistId());
        }

        return view;
    }

    public class TourDateRecyclerViewAdapter extends RecyclerView.Adapter<TourDateRecyclerViewAdapter.ViewHolder> {

        private List<Event> mValues;

        public TourDateRecyclerViewAdapter() {
            this.mValues = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tour_date_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.performanceDate.setText(DateUtil.getFormattedDateString(mValues.get(position).getEventDate()));
            holder.venue.setText(mValues.get(position).getVenueName());
            holder.location.setText(mValues.get(position).getVenueAddress());
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void setEventDates(List<Event> events) {
            mValues = events;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView performanceDate;
            public final TextView venue;
            public final TextView location;
            public Event mItem;

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
