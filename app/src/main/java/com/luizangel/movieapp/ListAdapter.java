package com.luizangel.movieapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luizangel.movieapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListAdapterViewHolder> {

    private int mNumberItems;

    private ArrayList<HashMap> mMovieData;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final ListAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListAdapterOnClickHandler {
        void onClick(HashMap<String, String> selectedMovie);
    }

    /**
     * Creates a ListAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public ListAdapter(int numberItems, ListAdapterOnClickHandler clickHandler) {
        mNumberItems = numberItems;
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a list item.
     */
    public class ListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mMovieTextView;

        public ListAdapterViewHolder(View view) {
            super(view);
            mMovieTextView = (TextView) view.findViewById(R.id.tv_movie_data);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            HashMap<String, String> selectedMovie = mMovieData.get(adapterPosition);
            mClickHandler.onClick(selectedMovie);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  It's possible to use this viewType integer to provide a different layout.
     *
     * @return A new ListAdapterViewHolder that holds the View for each list item
     */
    @Override
    public ListAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ListAdapterViewHolder(view);
    }

    private void buildMovieInformations(ListAdapterViewHolder listAdapterViewHolder,
                                        HashMap itemMovie) {

        listAdapterViewHolder.mMovieTextView.setText(
                "Title: " + itemMovie.get("title") + "\n\n"
                + "Overview: " + itemMovie.get("overview") + "\n\n"
                + "Year of release: " + ((String) itemMovie.get("release_date")).substring(0, 4));


        Bitmap bm = (Bitmap) itemMovie.get("movie_image");
        Drawable d = new BitmapDrawable(bm);
        d.setBounds(0, 0, bm.getWidth(), bm.getHeight());
        listAdapterViewHolder.mMovieTextView.setCompoundDrawables(d, null, null, null);

    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. Update the contents of the ViewHolder to display the items
     * details for this particular position
     *
     * @param listAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ListAdapterViewHolder listAdapterViewHolder, int position) {
        HashMap<String, String> itemMovie = mMovieData.get(position);
        buildMovieInformations(listAdapterViewHolder, itemMovie);
    }

    /**
     * This method simply returns the number of items to display.
     *
     * @return The number of items available
     */
    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
//        return mMovieData.size();
        return mNumberItems;
    }

    /**
     * This method is used to set the data on Adapter if it was already created one.
     *
     * @param movieData The new data to be displayed.
     */
    public void setMovieData(ArrayList<HashMap> movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }
}
