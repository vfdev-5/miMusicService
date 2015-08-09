package com.vfdev.mimusicservicelib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.vfdev.mimusicservicelib.core.MusicPlayer;
import com.vfdev.mimusicservicelib.core.TrackInfo;
import com.vfdev.mimusicservicelib.core.TrackInfoProvider;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * Created by vfomin on 5/17/15.
 *
 * class MusicServiceHelper for easy usage of the MusicService
 *
 * class MusicServiceHendler.OnReadyListener
 * Listener used to respond when MusicServiceHelper is ready to be used
 *
 */
public class MusicServiceHelper implements
        ServiceConnection
{
    private Context mContext;
    private Class<?> mActivityClass;
    private TrackInfoProvider mProvider;
    private MusicService mService = null;
    private boolean mBound = false;
//    private OnReadyListener mListener = null;

    // ------ Public methods
    /**
     * Constructor
     * @param context
     * @param provider track info provider
     * @param activityClass notification activity class that should be called
     *
     */
    static private MusicServiceHelper _instance;

    static public MusicServiceHelper getInstance() {
        if (_instance == null) {
            _instance = new MusicServiceHelper();
        }
        return _instance;
    }

    private MusicServiceHelper() {

    }

    public MusicServiceHelper init(Context context, TrackInfoProvider provider, Class<?> activityClass) {
        mContext = context;
        mActivityClass = activityClass;
        mProvider = provider;
        return this;
    }

    public void startMusicService() {
        Timber.v("startMusicService");
        if (!isInit()) return;

        // start service and bind
        Intent i = new Intent(mContext, MusicService.class);
        i.putExtra("ActivityClass", mActivityClass);

        mContext.startService(i);
        mContext.bindService(new Intent(mContext, MusicService.class), this, Context.BIND_AUTO_CREATE);
    }

    public void stopMusicService() {
        Timber.v("stopMusicService");
        if (!isInit()) return;
        unbind();
        mContext.stopService(new Intent(mContext, MusicService.class));
    }

    public void release() {
        if (!isInit()) return;
        unbind();

    }

    public MusicPlayer getPlayer() {
        return mBound ? mService.getPlayer() : null;
    }

    public boolean play() {
        return mBound && mService.getPlayer().play();
    }

    public void pause() {
        if (mBound) {
            mService.getPlayer().pause();
        }
    }

    public boolean playNextTrack() {
        return mBound && mService.getPlayer().playNextTrack();
    }

    public boolean playPrevTrack() {
        return mBound && mService.getPlayer().playPrevTrack();
    }

    public void setupTracks(String query) {
        if (mBound) {
            mService.setupTracks(query);
        }
    }

    public ArrayList<TrackInfo> getPlaylist() {
        if (mBound) {
            return mService.getPlayer().getTracks();
        }
        return null;
    }

    public ArrayList<TrackInfo> getTracksHistory() {
        if (mBound) {
            return mService.getPlayer().getTracksHistory();
        }
        return null;
    }

    public void clearPlaylist() {
        if (mBound) {
            mService.getPlayer().clearTracks();
        }
    }

    public void clearTracksHistory() {
        if (mBound) {
            mService.getPlayer().clearTracksHistory();
        }
    }

    public TrackInfo getPlayingTrackInfo() {
        if (mBound) {
            return mService.getPlayer().getPlayingTrack();
        }
        return null;
    }

    public boolean isPlaying() {
        return (mBound) && mService.getPlayer().isPlaying();
    }

    public void setContinuousPlay(boolean value) {
        if (mBound) {
            mService.setContinuousPlay(value);
        }
    }

    // ----------- Protected methods

    protected void unbind() {
        // Unbind from the service
        if (mBound) {
            mContext.unbindService(this);
            mBound = false;
        }
    }

    protected boolean isInit() {
        boolean res = (mContext !=null) &&
                (mProvider != null) &&
                (mActivityClass != null);
        if (!res) {
            Timber.e("MusicServiceHelper is not initialized");
        }
        return res;
    }

    // ----------- Service connection

    // Defines callbacks for service binding, passed to bindService()
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
        Timber.v("MusicServiceHelper is connected to MusicService");

        // We've bound to MusicService, cast the IBinder and get LocalService instance
        MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
        mService = binder.getService();
        mBound = true;

        // setup once track info provider:
        if (mService.getTrackInfoProvider() == null) {
            mService.setTrackInfoProvider(mProvider);
            mService.setContinuousPlay(true);
        }

        EventBus.getDefault().post(new ReadyEvent());
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        Timber.v("MusicServiceHelper is disconnected from MusicService");
        mService = null;
        mBound = false;
        _instance = null;
    }


    // ---------------- MusicServiceHendler.ReadyEvent
    public class ReadyEvent {
        public ReadyEvent() {}
    }

}
