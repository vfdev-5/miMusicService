package com.vfdev.mimusicservicelib.core;

import android.os.AsyncTask;
import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

//import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by vfomin on 5/16/15.
 */
public abstract class TrackInfoProvider {


    public class Result {
        public int code = 0;
        public ArrayList<TrackInfo> tracks;
        Result(int code, ArrayList<TrackInfo> tracks) {
            this.code = code;
            this.tracks = tracks;
        }
    }

    // REQUEST CODES
    public final static int OK=0;
    public final static int APP_ERR=1;
    public final static int CONNECTION_ERR=2;
    public final static int NOTRACKS_ERR=3;
    public final static int QUERY_ERR=4;

    protected OnDownloadTrackInfoListener mTrackInfoListener;
    protected String mQuery = "Trance";

    // Async task
    private DownloadTrackInfoAsyncTask mDownloader;


    // ---------- Abstract methods

    /**
     * Method to retrieve track info asynchronously
     * @param count is the number of tracks to retrieve
     */
    public synchronized void retrieveInBackground(int count) {
        if (mDownloader == null) {
            mDownloader = new DownloadTrackInfoAsyncTask(mTrackInfoListener);
            mDownloader.execute(count);
        }
    }

    /**
     * Abstract method to retrieve track info
     * @param count is the number of tracks to retrieve
     * @return instance of Result : code and array of track infos
     */
    public abstract Result retrieve(int count);

    public synchronized void setQuery(String query) {
        try {
            mQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            if (mTrackInfoListener != null) {
                mTrackInfoListener.onDownloadTrackInfo(new Result(QUERY_ERR, null));
            }
            mQuery = "";
        }
    }


    // ---------- Public methods
    public void setOnDownloadTrackInfoListener(OnDownloadTrackInfoListener listener) {
        mTrackInfoListener = listener;
    }

    // ---------- OnDownloadTrackInfoListener
    public interface OnDownloadTrackInfoListener {
        public void onDownloadTrackInfo(Result result);
    }

    // ------------ DownloadTrackInfoAsyncTask
    private class DownloadTrackInfoAsyncTask extends AsyncTask<Integer, Void, Result> {

        OnDownloadTrackInfoListener mListener;
        DownloadTrackInfoAsyncTask(OnDownloadTrackInfoListener listener) {
            mListener = listener;
        }
        @Override
        protected Result doInBackground(Integer... params) {
            if (!mQuery.isEmpty()) {
                return TrackInfoProvider.this.retrieve(params[0]);
            } else {
                return new Result(QUERY_ERR, null);
            }
        }

        @Override
        protected void onPostExecute(Result result) {
            if (mListener != null) {
                mListener.onDownloadTrackInfo(result);
            }
            mDownloader = null;
        }

    }

}
