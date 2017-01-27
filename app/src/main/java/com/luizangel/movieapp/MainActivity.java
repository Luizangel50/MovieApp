package com.luizangel.movieapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.luizangel.movieapp.utilities.NetworkUtils;
import com.luizangel.movieapp.utilities.ReadJsonResponsesUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ListAdapter.ListAdapterOnClickHandler {

    private RecyclerView mRecyclerView;

    private ListAdapter mListAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private int NUMBER_ITEMS = 20;

    private int pageNumber = 1;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * The Adapter is responsible for linking our data with the Views that
         * will end up displaying those data.
         */
        mListAdapter = new ListAdapter(NUMBER_ITEMS, this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mListAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* Once all of our views are setup, we can load the movie data. */
        setTitle(getString(R.string.app_name) + ": " + "Popular Movies");
        loadMovieData(getString(R.string.popular_movies), null);
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     *
     */
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie View.
     *
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This method will tell some background method to get the movie data
     * in the background thread.
     *
     */
    private void loadMovieData(String typeScreen, String searchKeyword) {
        showMovieDataView();

        new FetchMovieTask().execute(typeScreen, searchKeyword, String.valueOf(pageNumber));
    }

    @Override
    public void onClick(HashMap<String, String> selectedMovie) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, selectedMovie.get("title"), Toast.LENGTH_SHORT);
        mToast.show();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<HashMap>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<HashMap> doInBackground(String... params) {

            /* If there's no params, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String typeScreen = params[0];
            boolean isSearch = false;
            URL movieRequestUrl;

            if (typeScreen.contains("search")) {
                isSearch = true;
            }

            movieRequestUrl = NetworkUtils.buildUrl(isSearch, params[1], params[2]);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                ArrayList<HashMap> simpleJsonMovieData = ReadJsonResponsesUtils
                        .getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);

                return simpleJsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap> movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                showMovieDataView();
                mListAdapter.setMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
//        MenuInflater inflater = getMenuInflater();
//        /* Use the inflater's inflate method to inflate our menu layout to this menu */
//        inflater.inflate(R.menu.movie_menu, menu);
//        /* Return true so that the menu is displayed in the Toolbar */
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_refresh) {
//            mListAdapter.setMovieData(null);
//            loadMovieData(getString(R.string.popular_movies));
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}
