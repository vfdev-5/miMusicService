package com.vfdev.mimusicservicelib.core;

import com.example.vfdev.mimusicservicelib.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Random;

import timber.log.Timber;

/**
 Typical response :
 {
 "headers":
 {
 "status":"success",
 "code":0,
 "error_message":"",
 "warnings":"",
 "results_count":1},
 "results":[
 {
 "id":"1081379",
 "name":"A State Of Trance",
 "duration":344,
 "artist_id":"434847",
 "artist_name":"K.R",
 "artist_idstr":"K.R",
 "album_name":"A State Of Trance",
 "album_id":"128822",
 "license_ccurl":"http:\/\/creativecommons.org\/licenses\/by-nc-sa\/3.0\/",
 "position":1,"releasedate":"2013-11-20",
 "album_image":"https:\/\/imgjam1.jamendo.com\/albums\/s128\/128822\/covers\/1.200.jpg",
 "audio":"https:\/\/storage.jamendo.com\/?trackid=1081379&format=mp31&from=app-4aa6f40a",
 "audiodownload":"https:\/\/storage.jamendo.com\/download\/track\/1081379\/mp32\/",
 "prourl":"https:\/\/licensing.jamendo.com\/track\/1081379",
 "shorturl":"http:\/\/jamen.do\/t\/1081379",
 "shareurl":"http:\/\/www.jamendo.com\/track\/1081379",
 "image":"https:\/\/imgjam1.jamendo.com\/albums\/s128\/128822\/covers\/1.200.jpg"
 "musicinfo":{
    "vocalinstrumental":"vocal",
     "lang":"en",
     "gender":"male",
     "acousticelectric":"electric",
     "speed":"high",
     "tags":{
         "genres":[
         "pop",
         "rock",
         "poprock",
         "newwave"
         ],
         "instruments":[
         "drum",
         "bass",
         "electricguitar"
         ],
         "vartags":[
         "summer",
         "dream",
         "acoustic",
         "lovesong",
         "lovemusic"
         ]
     }
 ],
 }
 ]
 }
 */

public class JamendoProvider extends RestApiJsonProvider {


    public static final String NAME = "Jamendo";
    public static final int DRAWABLE_ID = R.drawable.jamendo;

    // Configuration
    protected final static String API_URL="https://api.jamendo.com/v3.0/";
    protected final static String REQUEST_TRACKS_URL_WITH_QUERY = API_URL + "tracks/?format=json&include=musicinfo&search=";
    protected final static String CLIENT_ID="4aa6f40a";

    // -------- Static meta-methods

    public String getName() {
        return NAME;
    }

    // -------- Protected methods

    protected String setupRequest(int count, boolean useOffset) {
        String requestUrl = REQUEST_TRACKS_URL_WITH_QUERY;
        requestUrl += mQuery.text;
        requestUrl += "&limit=" + String.valueOf(count);
        if (useOffset) {
            requestUrl += "&offset=" + String.valueOf(new Random().nextInt(50));
        }
        if (mQuery.durationMin > 0 || mQuery.durationMax > 0) {
            requestUrl += "&durationbetween=";
            if (mQuery.durationMin > 0) {
                requestUrl+=String.valueOf(mQuery.durationMin*1000);
            } else {
                requestUrl+=String.valueOf(0);
            }
            requestUrl += "_";
            if (mQuery.durationMax > 0) {
                requestUrl += String.valueOf(mQuery.durationMax*1000);
            } else {
                requestUrl += String.valueOf(1000000);
            }
        }

        requestUrl += "&client_id=" + CLIENT_ID;
        Timber.i("Request URL : " + requestUrl);
        return requestUrl;
    }

    protected TrackInfo parseTrackInfoJSON(JSONObject trackJSON) throws JSONException {
        TrackInfo tInfo = new TrackInfo();
        tInfo.id = trackJSON.getString("id");
        tInfo.title = trackJSON.getString("name");
        tInfo.duration = trackJSON.getInt("duration")*1000;
        tInfo.tags = trackJSON.getJSONObject("musicinfo").getJSONObject("tags").getString("vartags");
        tInfo.description = "";
        tInfo.streamUrl = trackJSON.getString("audio");

        tInfo.artist = trackJSON.getString("artist_name");
        tInfo.artworkUrl = trackJSON.getString("image");
        tInfo.resourceUrl = trackJSON.getString("shareurl");

        Iterator<String> keys = trackJSON.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            tInfo.fullInfo.put(key, trackJSON.getString(key));
        }
        return tInfo;
    }

}
