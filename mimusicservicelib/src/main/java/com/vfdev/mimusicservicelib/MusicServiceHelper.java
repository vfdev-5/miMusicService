package com.vfdev.mimusicservicelib;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.vfdev.mimusicservicelib.core.TrackInfoProvider;

import timber.log.Timber;

/**
 * Created by vfomin on 5/17/15.
 */
public class MusicServiceHelper implements
        ServiceConnection
{
    private Context mContext;
    private Class<?> mActivityClass;
    private TrackInfoProvider mProvider;
    private MusicService mService = null;
    private boolean mBound = false;

    // ------ Public methods
    /**
     * Constructor
     * @param context
     * @param provider track info provider
     * @param activityClass notification activity class that should be called
     *
     */
    public MusicServiceHelper(Context context, TrackInfoProvider provider, Class<?> activityClass) {
        mContext = context;
        mActivityClass = activityClass;
        mProvider = provider;
    }

    public void startMusicService() {
        Timber.v("startMusicService");
        // start service and bind
        Intent i = new Intent(mContext, MusicService.class);
        i.putExtra("ActivityClass", mActivityClass);

        mContext.startService(i);
        mContext.bindService(new Intent(mContext, MusicService.class), this, Context.BIND_AUTO_CREATE);
    }

    public void stopMusicService() {
        Timber.v("stopMusicService");
        if (mBound) {
            mContext.unbindService(this);
            mBound = false;
        }
        mContext.stopService(new Intent(mContext, MusicService.class));
    }

    public void play() {
        if (mBound) {
            mService.play();
        }
    }

    public void pause() {
        if (mBound) {
            mService.pause();
        }
    }


    // ----------- Service connection

    // Defines callbacks for service binding, passed to bindService()
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
        Timber.v("Main activity is connected to MusicService");

        // We've bound to MusicService, cast the IBinder and get LocalService instance
        MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
        mService = binder.getService();
        mBound = true;

        // setup track info provider:
        mService.setTrackInfoProvider(mProvider);

//        mService.setErrorListener(this);
//        mMainFragment.setService(mService);
//        mPlaylistFragment.setService(mService);
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        Timber.v("Main activity is disconnected from MusicService");
    }

}
