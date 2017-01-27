package com.luizangel.movieapp;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.luizangel.movieapp.utilities.NetworkUtils;
import com.luizangel.movieapp.utilities.ReadJsonResponsesUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ListAdapter.ListAdapterOnClickHandler {

    private RecyclerView mPopularRecyclerView;

    private RecyclerView mSearchRecyclerView;

    private ListAdapter mPopularListAdapter;

    private ListAdapter mSearchListAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private int NUMBER_ITEMS = 10;

    private int pageNumberPopular = 1;

    private int pageNumberSearch = 1;

    private boolean isSearch = false;

    SearchView searchView;

    Context context;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        mPopularRecyclerView = (RecyclerView) findViewById(R.id.rv_popular_movies);

        mSearchRecyclerView = (RecyclerView) findViewById(R.id.rv_search_movies);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        final LinearLayoutManager layoutManagerPopular
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        LinearLayoutManager layoutManagerSearch
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mPopularRecyclerView.setLayoutManager(layoutManagerPopular);

        mSearchRecyclerView.setLayoutManager(layoutManagerSearch);

        /*
         * Improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mPopularRecyclerView.setHasFixedSize(true);

        mSearchRecyclerView.setHasFixedSize(true);

        /*
         * The Adapter is responsible for linking our data with the Views that
         * will end up displaying those data.
         */
        mPopularListAdapter = new ListAdapter(NUMBER_ITEMS, this);

        mSearchListAdapter = new ListAdapter(NUMBER_ITEMS, this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mPopularRecyclerView.setAdapter(mPopularListAdapter);

        mSearchRecyclerView.setAdapter(mSearchListAdapter);

        mPopularRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int visibleItemCount = layoutManagerPopular.getChildCount();
                int totalItemCount = layoutManagerPopular.getItemCount();
                int pastVisibleItems = layoutManagerPopular.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    pageNumberPopular += 1;
                    loadMovieData(null);
                }
            }
        });

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* Once all of our views are setup, we can load the movie data. */
        isSearch = false;
        loadMovieData(null);
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     *
     */
    private void showPopularMovieView() {
        isSearch = false;
        setTitle(getString(R.string.popular_movies));

        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mPopularRecyclerView.setVisibility(View.VISIBLE);
        mSearchRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showSearchMovieView() {
        isSearch = true;
        setTitle(getString(R.string.empty));

        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mPopularRecyclerView.setVisibility(View.INVISIBLE);
        mSearchRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie View.
     *
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mPopularRecyclerView.setVisibility(View.INVISIBLE);
        mSearchRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        if (isSearch) {
            mErrorMessageDisplay.setText(getString(R.string.error_message_search));
        } else {
            mErrorMessageDisplay.setText(getString(R.string.error_message_popular));
        }
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This method will tell some background method to get the movie data
     * in the background thread.
     *
     */
    private FetchMovieTask loadMovieData(String searchKeyword) {
        FetchMovieTask newTask = new FetchMovieTask();

        if (isSearch) {
            showSearchMovieView();
            newTask.execute(searchKeyword, String.valueOf(pageNumberSearch));
        } else {
            showPopularMovieView();
            newTask.execute(searchKeyword, String.valueOf(pageNumberPopular));
        }

        return newTask;
    }

    @Override
    public void onClick(HashMap<String, String> selectedMovie) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(context, selectedMovie.get("title"), Toast.LENGTH_SHORT);
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
            if (isCancelled() || params.length == 0) {
                return null;
            }

            URL movieRequestUrl;

            movieRequestUrl = NetworkUtils.buildUrl(isSearch, params[0], params[1]);

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
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap> movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                if (isSearch) {
                    showSearchMovieView();
                    mSearchRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                    mSearchListAdapter.setMovieData(movieData);
                } else {
                    showPopularMovieView();
                    mPopularRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                    mPopularListAdapter.setMovieData(movieData);
                }
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            final EditText searchPlate = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchPlate.setHint("Search a movie...");
            View searchPlateView = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
            searchPlateView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    showPopularMovieView();
//                    if (mToast != null) {
//                        mToast.cancel();
//                    }
//                    mToast = Toast.makeText(context, "Close button", Toast.LENGTH_SHORT);
//                    mToast.show();


                    return false;
                }
            });
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = v.getId();

//                    Log.v("CLICK ITEM MENU", "ASDSADASDASDSADSA");
//                    if (mToast != null) {
//                        mToast.cancel();
//                    }

                    if (id == R.id.action_search) {
                        showSearchMovieView();
//                        mToast = Toast.makeText(context, String.valueOf(id), Toast.LENGTH_SHORT);
//                        mToast.show();
                    }
                }
            });

            // use this method for search process
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                FetchMovieTask searchMovieTask = new FetchMovieTask();

                @Override
                public boolean onQueryTextSubmit(String query) {
                    // use this method when query submitted
//                    if (mToast != null) {
//                        mToast.cancel();
//                    }
//                    mToast = Toast.makeText(context, query, Toast.LENGTH_SHORT);
//                    mToast.show();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (searchMovieTask.getStatus() != AsyncTask.Status.FINISHED) {
                        searchMovieTask.cancel(true);
                    }

                    searchMovieTask = loadMovieData(newText);
                    mSearchRecyclerView.scrollToPosition(0);

                    return false;
                }
            });
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Log.v("backbutton","Search icon click, asdsadsasadsa");
        if (!searchView.isIconified()) {
            showPopularMovieView();
//            searchView.clearFocus();

            searchView.onActionViewCollapsed();

            searchView.setIconified(true);

        } else {
            super.onBackPressed();
        }

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_search) {
////            mListAdapter.setMovieData(null);
////            loadMovieData(getString(R.string.popular_movies));
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}
