package com.vfdev.mimusicservicelib.core;

import android.util.Pair;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by vfomin on 8/9/15.
 *
 *
 * Abstract class requests a music provider Api using OkHttpClient.
 *
 * A request is of type
 *
 * http://API_URL/QUERY&LIMIT=10&OFFSET=123&CLIENT_ID=sdkfnbsdkjfks
 *
 * and get json output of type
 *
 * [
 * {
 *     "<ID_FIELD>"                 : "a_id_string__12345",
 *     "<DURACTION_IN_MILLIS_FIELD> : 123455,
 *     "<TITLE_FIELD>"              : "A_title_string__cool_track",
 *     "<STREAM_URL_FIELD>"         : "A_stream_url__http",
 *     "<TAGS_FIELD>"               : "A_tags__trance_house__",
 *     "<DESCRIPTION_FIELD>"        : "A_description__this_track_is__",
 *     ...
 *     ...
 * }
 * ]
 *
 */
public abstract class RestApiJsonProvider extends TrackInfoProvider {

    // Connection
    protected OkHttpClient mClient = new OkHttpClient();
    protected final static int HTTP_OK=200;


    /**
     * Implementation of the abstract method to retrieve track info
     * @param count is the number of tracks to retrieve
     * @return array of track infos
     */
    public Result retrieve(int count) {
        Result result = retrieve(count, mRandomize);
        if (!mRandomize) mRandomize = true;
        return result;
    }

    public Result retrieve(int count, boolean useOffset) {
        Timber.v("RestApiJsonProvider : request tracks on the query : " + mQuery + ", useOffset=" + useOffset);
        String requestUrl = setupRequest(count, useOffset);
        Pair<Integer, String> result = sendRequest(requestUrl);
        int code = result.first;
        if (code == OK) {
            Result anotherResult = parseResponse(result.second);
            if (anotherResult.code == OK) {
                return new Result(code, anotherResult.tracks);
            } else if (anotherResult.code == NOTRACKS_ERR && useOffset) {
                // Try to get tracks without offset if no result with non-zero offset
                return retrieve(count, false);
            }
            return new Result(anotherResult.code, null);
        }
        // handle errors:
        return new Result(code,  null);
    }

    // -------- Protected methods

    protected abstract String setupRequest(int count, boolean useOffset);

    protected Pair<Integer,String> sendRequest(String requestUrl) {
        try {
            Request request = new Request.Builder().url(requestUrl).build();
            Response response = mClient.newCall(request).execute();
            int code = response.code();
            String responseStr = response.body().string();
            if (code == HTTP_OK) {
                return new Pair<>(OK, responseStr);
            } else {
                Timber.e("getResponse : Request error : " + responseStr);
                return new Pair<>(APP_ERR, responseStr);
            }
        } catch (IOException e) {
            Timber.i(e, "getTracksInBackground : RestApiJsonProvider get request error : " + e.getMessage());
            return new Pair<>(CONNECTION_ERR, null);
        }
    }

    protected abstract TrackInfo parseTrackInfoJSON(JSONObject tracksJSON) throws JSONException;

    protected Result parseResponse(String responseStr) {
        int length = 0;
        JSONArray tracksJSON = null;
        // responseStr can have 'null' (from HearThisAtProvider)
        if (responseStr.equalsIgnoreCase("null")){
            return new Result(NOTRACKS_ERR, null);
        }

        try {
            // Parse the response:
            tracksJSON = new JSONArray(responseStr);
            length = tracksJSON.length();

            if (length == 0) {
                return new Result(NOTRACKS_ERR, null);
            }

            ArrayList<TrackInfo> tracks = new ArrayList<>();
            for (int i=0;i<length;i++) {
                JSONObject trackJSON = tracksJSON.getJSONObject(i);

                TrackInfo tInfo = parseTrackInfoJSON(trackJSON);
                if (tInfo != null) {
                    tracks.add(tInfo);
                }
            }
            if (!tracks.isEmpty()) {
                Timber.v("getTracksInBackground : found " + tracks.size() + " tracks");
                return new Result(OK, tracks);
            }
            else {
                return new Result(NOTRACKS_ERR, null);
            }
        } catch (JSONException e) {
            Timber.e(e, "getTracksInBackground : JSON parse error : " + e.getMessage() + ", responseStr : " + responseStr);
            return new Result(APP_ERR, null);
        }
    }


}
