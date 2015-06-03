package com.vfdev.mimusicservicelib.core;

import android.os.AsyncTask;
import android.os.Bundle;
import java.util.ArrayList;

import org.apache.commons.lang3.StringEscapeUtils;

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
        String q = StringEscapeUtils.escapeHtml4(query);
        mQuery = q.replaceAll("\\s","%20");
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
            return TrackInfoProvider.this.retrieve(params[0]);
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
