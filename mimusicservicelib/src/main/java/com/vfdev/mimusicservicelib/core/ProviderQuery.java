package com.vfdev.mimusicservicelib.core;

/**
 *  Class to specify query to TrackInfoProvider with parameters
 *      - query text
 *      - minimal duration in msec
 *      - maximal duration in msec
 *
 */
public class ProviderQuery {

    public String text = "Trance";
    public int durationMin = -1; // in msec
    public int durationMax = -1; // in msec


}
