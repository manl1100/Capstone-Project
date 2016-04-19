package com.example.manuelsanchez.udacitycapstone;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class EventItemDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    private Event mEvent;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.eventitem_detail, container, false);

        if (mEvent != null) {
            ((TextView) rootView.findViewById(R.id.eventitem_detail_venue)).setText(mEvent.getVenueName());
            ((TextView) rootView.findViewById(R.id.eventitem_detail_date)).setText(mEvent.getEventDate());
        }

        return rootView;
    }
}
