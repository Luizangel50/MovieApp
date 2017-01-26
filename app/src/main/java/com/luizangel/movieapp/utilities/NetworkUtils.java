package com.luizangel.movieapp.utilities;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;


public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String POPULAR_MOVIES_URL =
            "https://api.themoviedb.org/3/movie/popular";

    private static final String KEYWORD_MOVIES_URL =
            "https://api.themoviedb.org/3/search/keyword";


    /**
     * URL Parameter keys
     */
    //Parameters used on all URLs
    private static final String AUTH_PARAM = "api_key";

    private static final String PAGE_PARAM = "page";

    //Parameter required just for keyword search URL
    private static final String QUERY_PARAM = "query";

    //Parameters used on popular movies URL
    private static final String LANGUAGE_PARAM = "language";

    private static final String REGION_PARAM = "region";


    /**
     * URL Parameter values
     */
    private static final String apiKey = "6504571503b5b64e6a71bb94a2972b4d";

    private static final String language = "en-US";

    private static final String pageNumber = "1";

    private static final String region = "en-US";

    /**
     * Builds the URL used to talk to the The Movie DB API
     *
     * @return The URL to use to query TheMovieDB server.
     */
    public static URL buildUrl(boolean isSearch, String keywordSearch) {

        Uri builtUri;
        URL url = null;

        if (isSearch) {
            builtUri = Uri.parse(KEYWORD_MOVIES_URL).buildUpon()
                    .appendQueryParameter(AUTH_PARAM, apiKey)
                    .appendQueryParameter(QUERY_PARAM, keywordSearch)
                    .appendQueryParameter(PAGE_PARAM, pageNumber)
                    .build();

        }
        else {
            builtUri = Uri.parse(POPULAR_MOVIES_URL).buildUpon()
                    .appendQueryParameter(AUTH_PARAM, apiKey)
                    .appendQueryParameter(LANGUAGE_PARAM, language)
                    .appendQueryParameter(PAGE_PARAM, pageNumber)
                    .appendQueryParameter(REGION_PARAM, region)
                    .build();
        }

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Builting URL " + url);

        return url;
    }

}
