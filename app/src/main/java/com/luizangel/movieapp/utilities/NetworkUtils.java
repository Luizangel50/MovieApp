package com.luizangel.movieapp.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the API
 */

public final class NetworkUtils {

    /********************************** Variables, constants and constructor **********************/

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_URL =
            "https://api.themoviedb.org/3/movie";

    private static final String POPULAR_MOVIES_URL =
            MOVIE_URL + "/popular";

    private static final String KEYWORD_MOVIES_URL =
            "https://api.themoviedb.org/3/search/movie";

    private static final String IMAGE_URL =
            "https://image.tmdb.org/t/p/w185";


    /**
     * URL Parameter keys
     */
    //Parameters used on movies URLs
    private static final String AUTH_PARAM = "api_key";

    private static final String LANGUAGE_PARAM = "language";

    private static final String PAGE_PARAM = "page";

    //Parameter required just for keyword search URL
    private static final String QUERY_PARAM = "query";

    //Parameters used on popular movies URL
    private static final String REGION_PARAM = "region";


    /**
     * URL Parameter values
     */
    private static final String apiKey = "6504571503b5b64e6a71bb94a2972b4d";

    private static final String language = "en-US";

//    private static final String pageNumber = "1";

    private static final String region = "en-US";

    /********************************** Non-overridden methods ***********************************/

    /**
     * Builds the URL used to talk to the The Movie DB API
     * for getting popular movies and keyword search
     *
     * @return The URL to use to query TheMovieDB server.
     */
    public static URL buildUrl(boolean isSearch, String keywordSearch, String pageNumber) {

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

//        Log.v(TAG, "Building search URL " + url);

        return url;
    }


    /**
     * Builds the URL used to talk to the The Movie DB API
     * for getting details about a certain movie
     *
     * @return The URL to use to query TheMovieDB server.
     */
    public static URL buildDetailsURL (String movieID) {

        String detailsMovieURL = MOVIE_URL
                + "/" + movieID + "/" + "images";

        Uri builtUri;
        URL url = null;

        builtUri = Uri.parse(detailsMovieURL).buildUpon()
                .appendQueryParameter(AUTH_PARAM, apiKey)
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

//        Log.v(TAG, "Building movie details URL " + url);

        return url;
    }

    /**
     * Builds the URL used to return a movie's image
     *
     * @return The URL to use to query TheMovieDB server.
     */
    public static URL imageURL (String filePathImage) {

        String imageMovieURL = IMAGE_URL + filePathImage;

        Uri builtUri;
        URL url = null;

        builtUri = Uri.parse(imageMovieURL).buildUpon().build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

//        Log.v(TAG, "Building image URL " + url);

        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
//            urlConnection.setConnectTimeout(2000);
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * This method returns the image from a URL.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static Bitmap getImageFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {

            InputStream in = urlConnection.getInputStream();

            BufferedInputStream bis = new BufferedInputStream(in);

            Bitmap bm = BitmapFactory.decodeStream(bis);
            bis.close();
            in.close();

            if (bm == null) {
//                Log.v(TAG, "not existent image");
                return null;
            } else {
                return bm;
            }

        } finally {
            urlConnection.disconnect();
        }
    }

}
