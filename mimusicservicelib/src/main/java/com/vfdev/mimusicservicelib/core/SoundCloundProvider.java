package com.vfdev.mimusicservicelib.core;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import timber.log.Timber;

/**
 * Created by vfomin on 5/16/15.
 */
public class SoundCloundProvider extends TrackInfoProvider {


    // Connection
    private OkHttpClient mClient = new OkHttpClient();
    private String CLIENT_ID="1abbcf4f4c91b04bb5591fe5a9f60821";

    private final static int HTTP_OK=200;
    private final static String API_URL="http://api.soundcloud.com/";
    private final static String REQUEST_TRACKS_URL_WITH_QUERY=API_URL+"tracks.json?q=";
    private final static String REQUEST_A_TRACK_URL=API_URL+"tracks/";
    private final static int TRACKS_LIMIT=7;

    /**
     * Abstract method to retrieve track info asynchronously
     * @param count is the number of tracks to retrieve
     */
//    public synchronized void retrieveInBackground(int count) {
//        if (mDownloader == null) {
//            mDownloader = new DownloadTrackInfoAsyncTask(mTrackInfoListener);
//            mDownloader.execute(count);
//        }
//    }

    /**
     * Abstract method to retrieve track info
     * @param count is the number of tracks to retrieve
     * @return array of track infos
     */
    public Result retrieve(int count) {

        Timber.v("SoundCloundProvider : request tracks on the query : " + mQuery);
        String requestUrl = setupRequest(count, false);
        Pair<Integer,String> result = sendRequest(requestUrl);
        int code = result.first;
        if (code == OK) {
            Result anotherResult = parseResponse(result.second);
            if (anotherResult.code == OK) {
                return new Result(code, anotherResult.tracks);
            }
        }
        // handle errors:
        return new Result(code,  null);
    }

    // -------- Protected methods

    protected String setupRequest(int count, boolean useOffset) {
        String requestUrl = REQUEST_TRACKS_URL_WITH_QUERY;
        requestUrl += mQuery;
        requestUrl += "&limit=" + String.valueOf(count);
        if (useOffset) {
            requestUrl += "&offset=" + String.valueOf(new Random().nextInt(1000));
        }
        requestUrl += "&client_id=" + CLIENT_ID;
        return requestUrl;
    }


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
            Timber.i(e, "getTracksInBackground : SoundCloud get request error : " + e.getMessage());
            return new Pair<>(CONNECTION_ERR, null);
        }
    }

    protected Result parseResponse(String responseStr) {
        int length = 0;
        JSONArray tracksJSON = null;
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
                if (trackJSON.getBoolean("streamable")) {
                    TrackInfo tInfo = new TrackInfo();
                    tInfo.id = trackJSON.getString("id");
                    tInfo.title = trackJSON.getString("title");
                    tInfo.duration = trackJSON.getInt("duration");
                    tInfo.tags = trackJSON.getString("tag_list");
                    tInfo.streamUrl = trackJSON.getString("stream_url") + "?client_id=" + CLIENT_ID;

                    Iterator<String> keys = trackJSON.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        tInfo.fullInfo.put(key, trackJSON.getString(key));
                    }
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
            Timber.e(e, "getTracksInBackground : JSON parse error : " + e.getMessage());
            return new Result(APP_ERR, null);
        }
    }

}
