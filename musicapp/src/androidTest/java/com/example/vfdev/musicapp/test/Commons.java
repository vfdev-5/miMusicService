package com.example.vfdev.musicapp.test;

import com.vfdev.mimusicservicelib.core.TrackInfo;

/**
 * Created by vfomin on 9/6/15.
 */
public class Commons {

    public static TrackInfo getOneGoodTrackInfo() {
        TrackInfo trackInfo = new TrackInfo();

        trackInfo.id = "178781982";
        trackInfo.title = "Au5 - Crossroad (feat. Danyka Nadeau)";
        trackInfo.duration = -1;
        trackInfo.tags = "Monstercat Au5 \\\"Danyka Nadeau\\\" House Electro Electronic Music Progressive EDM Crossroad";
        trackInfo.streamUrl = "https://api.soundcloud.com/tracks/178781982/stream";

        return trackInfo;
    }

    private static TrackInfo getOneBadTrackInfo() {
        TrackInfo trackInfo = new TrackInfo();

        trackInfo.id = "178781982";
        trackInfo.title = "Au5 - Crossroad (feat. Danyka Nadeau)";
        trackInfo.duration = -1;
        trackInfo.tags = "Monstercat Au5 \\\"Danyka Nadeau\\\" House Electro Electronic Music Progressive EDM Crossroad";
        trackInfo.streamUrl = "https://api.soundcloud.com/tracks/178781982/streamdfsldkfn";

        return trackInfo;
    }



}
