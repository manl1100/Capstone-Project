package com.example.manuelsanchez.udacitycapstone;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class EventItemDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    private Event mEvent;
    private HeadLinerRecyclerViewAdapter mHeadLinerRecyclerViewAdapter;

    public EventItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mEvent = getArguments().getParcelable(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mEvent.getHeadLinerName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.eventitem_detail, container, false);
        if (mEvent != null) {
            ((TextView) rootView.findViewById(R.id.eventitem_detail_venue)).setText(mEvent.getVenueName());
            ((TextView) rootView.findViewById(R.id.eventitem_detail_date)).setText(mEvent.getEventDate());

            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.lineup);
            mHeadLinerRecyclerViewAdapter = new HeadLinerRecyclerViewAdapter(mEvent.getArtists());
            recyclerView.setAdapter(mHeadLinerRecyclerViewAdapter);
        }

        return rootView;
    }

    public class HeadLinerRecyclerViewAdapter extends RecyclerView.Adapter<HeadLinerRecyclerViewAdapter.ViewHolder> {

        private List<Artist> mValues;

        public HeadLinerRecyclerViewAdapter(List<Artist> artists) {
            mValues = artists;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.performer_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mPerformer.setText(mValues.get(position).artistName);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mPerformer;
            public Artist mItem;

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
