package com.example.manuelsanchez.udacitycapstone;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.example.manuelsanchez.udacitycapstone.data.EventContract.*;


public class EventItemListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EventItemListActivity.class.getSimpleName();

    private boolean mTwoPane;
    private RecyclerView recyclerView;

    private static final int COL_EVENT_ID = 0;
    private static final int COL_PERFORMER = 1;
    private static final int COL_VENUE = 2;
    private static final int COL_LAT = 3;
    private static final int COL_LONG = 4;
    private static final int COL_DATE = 5;

    private static final String[] EVENT_COLUMNS = {
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_EVENT_ID,
            PerformerEntry.COLUMN_PERFORMER_NAME,
            EventEntry.COLUMN_VENUE,
            EventEntry.COLUMN_COORD_LATITUDE,
            EventEntry.COLUMN_COORD_LONGITUDE,
            EventEntry.COLUMN_DATE,

    };

    private static final int EVENT_LOADER = 0;

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


    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private Cursor mCursor;

        public SimpleItemRecyclerViewAdapter(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(COL_EVENT_ID);
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
//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mTwoPane) {
//                        Bundle arguments = new Bundle();
//                        arguments.putParcelable(EventItemDetailFragment.ARG_ITEM_ID, holder.mItem);
//                        EventItemDetailFragment fragment = new EventItemDetailFragment();
//                        fragment.setArguments(arguments);
//                        getFragmentManager().beginTransaction()
//                                .replace(R.id.eventitem_detail_container, fragment)
//                                .commit();
//                    } else {
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, EventItemDetailActivity.class);
//                        intent.putExtra(EventItemDetailFragment.ARG_ITEM_ID, holder.mItem);
//                        context.startActivity(intent);
//                    }
//                }
//            });
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
