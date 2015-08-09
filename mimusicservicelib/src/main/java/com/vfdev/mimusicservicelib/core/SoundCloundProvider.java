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
import java.util.Iterator;
import java.util.Random;

import timber.log.Timber;

/**
 * Created by vfomin on 5/16/15.

    Example of response from SoundCloud
     [{
     "kind":"track",
     "id":178781982,
     "created_at":"2014/11/26 19:50:36 +0000",
     "user_id":8553751,
     "duration":417422,
     "commentable":true,
     "state":"finished",
     "original_content_size":73618308,
     "last_modified":"2015/05/31 03:04:44 +0000",
     "sharing":"public",
     "tag_list":"Monstercat Au5 \"Danyka Nadeau\" House Electro Electronic Music Progressive EDM Crossroad",
     "permalink":"au5-crossroad-feat-danyka-nadeau",
     "streamable":true,
     "embeddable_by":"all",
     "downloadable":false,
     "purchase_url":"https://itunes.apple.com/ca/album/crossroad-feat.-danyka-nadeau/id943935414?i=943935648&ign-mpt=uo%3D4",
     "label_id":null,
     "purchase_title":"Support on iTunes",
     "genre":"Trance",
     "title":"Au5 - Crossroad (feat. Danyka Nadeau)",
     "description":"Support on iTunes: http://monster.cat/1FrpUAC
     \nSupport on Beatport: http://monster.cat/120nObU
     \nSupport on Bandcamp: http://monster.cat/11UFQgC
     \n---
     \n
     Watch on YouTube: http://monster.cat/1FlXeYD\n
     Listen on Spotify: http://monster.cat/1y2jJSs\n\n
     ▼ Follow Monstercat\nYouTube: http://www.youtube.com/Monstercat\n
     Spotify: http://monster.cat/1hGrCWk\n
     Facebook: http://facebook.com/Monstercat\n
     Twitter: http://twitter.com/Monstercat\n
     Instagram: http://instagram.com/monstercat\n
     Vine: https://vine.co/monstercat\n
     SoundCloud: http://soundcloud.com/Monstercat\n
     Google+: https://plus.google.com/+Monstercat\n
     Podcast: http://live.monstercat.com/\n
     \n▼ Follow Au5\nFacebook: http://www.facebook.com/TheAu5\nTwitter: http://twitter.com/Au5music\n
     Soundcloud: https://soundcloud.com/au5\nYoutube: http://www.youtube.com/au5music\n\n▼ Follow Danyka Nadeau\n
     Facebook: http://facebook.com/danykanadeaumusic\nTwitter: http://twitter.com/ndanyka&\n
     Soundcloud: https://soundcloud.com/danykanadeau\nYoutube: http://youtube.com/user/danykanadeauofficial\n\n▼
     Want some new Merchandise?\nhttp://monster.cat/MonstercatShop",
     "label_name":"Monstercat",
     "release":"MCS282",
     "track_type":"original",
     "key_signature":"",
     "isrc":"",
     "video_url":null,
     "bpm":null,
     "release_year":2014,
     "release_month":11,
     "release_day":18,
     "original_format":"wav",
     "license":"all-rights-reserved",
     "uri":"https://api.soundcloud.com/tracks/178781982",
     "user":{
     "id":8553751,
     "kind":"user",
     "permalink":"monstercat",
     "username":"Monstercat",
     "last_modified":"2015/05/29 17:46:43 +0000",
     "uri":"https://api.soundcloud.com/users/8553751",
     "permalink_url":"http://soundcloud.com/monstercat",
     "avatar_url":"https://i1.sndcdn.com/avatars-000147435518-59movz-large.jpg"
     },
     "permalink_url":"http://soundcloud.com/monstercat/au5-crossroad-feat-danyka-nadeau",
     "artwork_url":"https://i1.sndcdn.com/artworks-000098406561-bx0m7f-large.jpg",
     "waveform_url":"https://w1.sndcdn.com/ZSBluhpDXNzQ_m.png",
     "stream_url":"https://api.soundcloud.com/tracks/178781982/stream",
     "playback_count":639122,
     "download_count":0,
     "favoritings_count":15397,
     "comment_count":489,
     "attachments_uri":"https://api.soundcloud.com/tracks/178781982/attachments",
     "policy":"ALLOW"
     }]
 */


public class SoundCloundProvider extends RestApiJsonProvider {

    // Configuration
    protected final static String API_URL="http://api.soundcloud.com/";
    protected final static String REQUEST_TRACKS_URL_WITH_QUERY = API_URL + "tracks.json?q=";
    protected final static String CLIENT_ID="1abbcf4f4c91b04bb5591fe5a9f60821";

    // -------- Protected methods

    protected String setupRequest(int count, boolean useOffset) {
        String requestUrl = REQUEST_TRACKS_URL_WITH_QUERY;
        requestUrl += mQuery;
        requestUrl += "&limit=" + String.valueOf(count);
        if (useOffset) {
            requestUrl += "&offset=" + String.valueOf(new Random().nextInt(50));
        }
        requestUrl += "&client_id=" + CLIENT_ID;
        Timber.i("Request URL : " + requestUrl);
        return requestUrl;
    }

    protected TrackInfo parseTrackInfoJSON(JSONObject trackJSON) throws JSONException {
        if (trackJSON.getBoolean("streamable")) {
            TrackInfo tInfo = new TrackInfo();
            tInfo.id = trackJSON.getString("id");
            tInfo.title = trackJSON.getString("title");
            tInfo.duration = trackJSON.getInt("duration");
            tInfo.tags = trackJSON.getString("tag_list");
            tInfo.description = trackJSON.getString("description");
            tInfo.streamUrl = trackJSON.getString("stream_url") + "?client_id=" + CLIENT_ID;
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
