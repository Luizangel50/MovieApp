package com.luizangel.movieapp.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility functions to handle JSON data from Http responses
 */

public final class ReadJsonResponsesUtils {

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the movies.
     *
     * @param responseJsonStr JSON response from server
     *
     * @return Array of Strings describing data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ArrayList<HashMap> getSimpleMovieStringsFromJson(Context context, String responseJsonStr)
            throws JSONException {

        // Results information
        final String FIELD_RESULTS = "results";

        final String FIELD_TOTAL_RESULTS = "total_results";

        final String FIELD_PAGE = "page";

        final String FIELD_TOTAL_PAGES = "total_pages";

        final String FIELD_MESSAGE_CODE = "status_code";

        final String FIELD_OVERVIEW = "overview";

        final String FIELD_ID = "id";

        final String FIELD_RELEASE_DATE = "release_date";

        final String FIELD_POSTER_PATH = "poster_path";

        final String FIELD_TITLE = "title";

        /* String array to hold each movie String */
        ArrayList<HashMap> parsedMovieData;

        JSONObject responseJson = new JSONObject(responseJsonStr);

        /* Is there an error? */
        if (responseJson.has(FIELD_MESSAGE_CODE)) {
            int errorCode = responseJson.getInt(FIELD_MESSAGE_CODE);

            if (errorCode == 7) {
                return null;
            }

            Log.e("CONNECTION ERROR", "No Internet connection");
        }

        int pageNumber = responseJson.getInt(FIELD_PAGE);

        int totalPagesNumber = responseJson.getInt(FIELD_TOTAL_PAGES);

        int totalResults = responseJson.getInt(FIELD_TOTAL_RESULTS);

        JSONArray movieArray = responseJson.getJSONArray(FIELD_RESULTS);

        parsedMovieData = new ArrayList<>(movieArray.length());

        for (int i = 0; i < movieArray.length(); i++) {
            //These are the values that will be collected
            String title;
            String posterPath;
            String overview;
            String releaseYear;
            String idMovie;

            JSONObject currentMovieObject = movieArray.getJSONObject(i);

            HashMap movieInfo = new HashMap();

            movieInfo.put(FIELD_TITLE,
                    currentMovieObject.getString(FIELD_TITLE));


            movieInfo.put(FIELD_OVERVIEW,
                    currentMovieObject.getString(FIELD_OVERVIEW));

            movieInfo.put(FIELD_RELEASE_DATE,
                    currentMovieObject.getString(FIELD_RELEASE_DATE));

            movieInfo.put(FIELD_ID,
                    currentMovieObject.getString(FIELD_ID));

            try {
                posterPath = currentMovieObject.getString(FIELD_POSTER_PATH);
//                Log.v("Imagem path: ", posterPath);
                Bitmap bm = null;

                if (!posterPath.equals("null")) {
                    bm = NetworkUtils.getImageFromHttpUrl(
                            NetworkUtils.imageURL(currentMovieObject.getString(FIELD_POSTER_PATH)));
                }

                movieInfo.put("movie_image", bm);
            } catch (IOException e) {
                e.printStackTrace();
            }

            parsedMovieData.add(movieInfo);

        }

//        Log.v("Test Movie Data:", (String) parsedMovieData.get(0).get(FIELD_TITLE));
        return parsedMovieData;
    }

}
