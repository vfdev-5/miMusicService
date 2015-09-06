package com.vfdev.mimusicservicelib.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Random;

import timber.log.Timber;

/**
 * Created by vfomin on 8/9/15.
 *
 *
 * Typical response :
     [
     {
         "id": "48250",
         "created_at": "2014-07-06 13:05:10",
         "user_id": "7",
         "duration": "7376",
         "permalink": "shawne-back-to-the-roots-2-05072014",
         "description": "Years: 2000 - 2005\r\nSet Time: Warm Up (11 pm - 01 am)\r\n",
         "downloadable": "1",
         "genre": "Drum & Bass",
         "genre_slush": "drumandbass",
         "title": "Shawne @ Back To The Roots 2 (05.07.2014)",
         "uri": "http:\/\/api-v2.hearthis.at\/\/shawne-back-to-the-roots-2-05072014\/",
         "permalink_url": "http:\/\/hearthis.at\/\/shawne-back-to-the-roots-2-05072014\/",
         "artwork_url": "http:\/\/hearthis.at\/_\/cache\/images\/track\/500\/801982cafc20a06ccf6203f21f10c08d_w500.png",
         "background_url": "",
         "waveform_data": "http:\/\/hearthis.at\/_\/wave_data\/7\/3000_4382f398c454c47cf171aab674cf00f0.mp3.js",
         "waveform_url": "http:\/\/hearthis.at\/_\/wave_image\/7\/4382f398c454c47cf171aab674cf00f0.mp3.png",
         "user": {
         "id": "7",
         "permalink": "shawne",
         "username": "Shawne (hearthis.at)",
         "uri": "http:\/\/api-v2.hearthis.at\/shawne\/",
         "permalink_url": "http:\/\/hearthis.at\/shawne\/",
         "avatar_url": "http:\/\/hearthis.at\/_\/cache\/images\/user\/512\/06a8299b0e7d8f2909a22697badd7c09_w512.jpg"
         },
         "stream_url": "http:\/\/hearthis.at\/shawne\/shawne-back-to-the-roots-2-05072014\/listen\/",
         "download_url": "http:\/\/hearthis.at\/shawne\/shawne-back-to-the-roots-2-05072014\/download\/",
         "playback_count": "75",
         "download_count": "9",
         "favoritings_count": "7",
         "favorited": false,
         "comment_count": "0"
     }
     ]
 *
 */
public class HearThisAtProvider extends RestApiJsonProvider {

    // Configuration
    protected final static String API_URL="http://api-v2.hearthis.at/";
    protected final static String REQUEST_TRACKS_URL_WITH_QUERY = API_URL + "search?t=";

    // -------- Public methods

    @Override
    public String getName() {
        return "HearThis.At";
    }


    // -------- Protected methods

    protected String setupRequest(int count, boolean useOffset) {
        String requestUrl = REQUEST_TRACKS_URL_WITH_QUERY;
        requestUrl += mQuery;
        if (useOffset) {
            requestUrl += "&page=" + String.valueOf(new Random().nextInt(50));
        }
        Timber.i("Request URL : " + requestUrl);
        return requestUrl;
    }

    protected TrackInfo parseTrackInfoJSON(JSONObject trackJSON) throws JSONException {
        String streamUrl = trackJSON.getString("stream_url");
        if (!streamUrl.isEmpty()) {
            TrackInfo tInfo = new TrackInfo();
            tInfo.id = trackJSON.getString("id");
            tInfo.title = trackJSON.getString("title");
            tInfo.duration = trackJSON.getInt("duration") * 1000;
            tInfo.tags = "";
            tInfo.streamUrl = streamUrl;
            Iterator<String> keys = trackJSON.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                tInfo.fullInfo.put(key, trackJSON.getString(key));
            }
            return tInfo;
        }
        return null;
    }



}
