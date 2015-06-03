package com.vfdev.mimusicservicelib.core;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by vfomin on 5/27/15.
 */
public class TrackInfo {

    public String id;
    public String title;
    public int duration=-1;
    public String tags;
    public String streamUrl;

    public boolean equals(Object t) {
        if (t instanceof TrackInfo) {
            return this.id.compareTo(((TrackInfo) t).id) == 0;
        }
        return false;
    }

    public HashMap<String, String> fullInfo = new HashMap<>();

    public String toString() {
        return "TrackInfo : " + id + ", " + title;
    }


}
