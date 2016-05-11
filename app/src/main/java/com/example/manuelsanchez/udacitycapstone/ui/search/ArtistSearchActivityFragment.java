package com.example.manuelsanchez.udacitycapstone.ui.search;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.manuelsanchez.udacitycapstone.R;
import com.example.manuelsanchez.udacitycapstone.ui.Artist;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ArtistSearchActivityFragment extends Fragment {

    private ArtistSearchItemRecyclerViewAdapter simpleItemRecyclerViewAdapter;

    public ArtistSearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_search, container, false);


        // recycler view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.artist_search_recycle_view);
        simpleItemRecyclerViewAdapter = new ArtistSearchItemRecyclerViewAdapter(getContext());
        recyclerView.setAdapter(simpleItemRecyclerViewAdapter);

        // text input
        EditText input = (EditText) view.findViewById(R.id.artist_search);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView searchInput, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchInput.getText().length() > 0) {
                        new ArtistSearchAsyncTask(getContext(), simpleItemRecyclerViewAdapter).execute(searchInput.getText().toString());
                    }
                }
                return false;
            }
        });


        return view;
    }


    public static class ArtistSearchItemRecyclerViewAdapter
            extends RecyclerView.Adapter<ArtistSearchItemRecyclerViewAdapter.ViewHolder> {

        private List<Artist> artistSearchResults;
        private Context mContext;

        public ArtistSearchItemRecyclerViewAdapter(Context context) {
            mContext = context;
            artistSearchResults = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.artist_search_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mArtist = artistSearchResults.get(position);
            Picasso.with(mContext)
                    .load(artistSearchResults.get(position).getImageUrl())
                    .resize(150, 150)
                    .centerCrop()
                    .into(holder.mImageView);
            holder.mTextView.setText(artistSearchResults.get(position).getArtistName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ArtistDetailActivity.class);
                    intent.putExtra(ArtistDetailActivityFragment.ARG_ITEM_ID, holder.mArtist);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return artistSearchResults.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView;
            public Artist mArtist;

            public ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                mImageView = (ImageView) itemView.findViewById(R.id.artist_thumbnail);
                mTextView = (TextView) itemView.findViewById(R.id.artist_name);
            }
        }

        public void setData(List<Artist> artists) {
            artistSearchResults = artists;
        }
    }
}
