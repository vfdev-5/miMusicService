package com.vfdev.mimusicservicelib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Pair;

import com.vfdev.mimusicservicelib.core.JamendoProvider;
import com.vfdev.mimusicservicelib.core.ProviderMetaInfo;
import com.vfdev.mimusicservicelib.core.SoundCloudProvider;
import com.vfdev.mimusicservicelib.core.HearThisAtProvider;
import com.vfdev.mimusicservicelib.core.MusicPlayer;
import com.vfdev.mimusicservicelib.core.ProviderQuery;
import com.vfdev.mimusicservicelib.core.TrackInfo;
import com.vfdev.mimusicservicelib.core.TrackInfoProvider;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * Created by vfomin on 5/17/15.
 *
 * class MusicServiceHelper for easy usage of the MusicService
 *
 */
public class MusicServiceHelper implements
        ServiceConnection
{
    private Context mContext;
    private Class<?> mActivityClass;
    private TrackInfoProvider[] mProviders;

    private MusicService mService = null;
    private boolean mBound = false;

    private static final ProviderMetaInfo[] availableProviders = new ProviderMetaInfo[] {
        new ProviderMetaInfo(
            SoundCloudProvider.NAME,
            SoundCloudProvider.DRAWABLE_ID,
            SoundCloudProvider.class),
        new ProviderMetaInfo(
            HearThisAtProvider.NAME,
            HearThisAtProvider.DRAWABLE_ID,
            HearThisAtProvider.class),
        new ProviderMetaInfo(
            JamendoProvider.NAME,
            JamendoProvider.DRAWABLE_ID,
            JamendoProvider.class)
    };


    // ------ Public methods
    /**
     * Constructor
     * @param context
     * @param provider track info provider
     * @param activityClass notification activity class that should be called
     *
     */
    static private MusicServiceHelper _instance = new MusicServiceHelper();

    static public MusicServiceHelper getInstance() {
        return _instance;
    }

    private MusicServiceHelper() {
    }

    public MusicServiceHelper init(Context context, TrackInfoProvider provider, Class<?> activityClass) {
        return init(context, new TrackInfoProvider[] {provider}, activityClass);
    }

    public MusicServiceHelper init(Context context, TrackInfoProvider[] providers, Class<?> activityClass) {
        mContext = context;
        mActivityClass = activityClass;
        mProviders = providers;
        return this;
    }

    // ---------- MusicService methods

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
        if (mContext == null) return;
        unbind();
        mContext.stopService(new Intent(mContext, MusicService.class));
    }

    public void release() {
        if (mContext == null) return;
        unbind();
    }

    // ------------ MusicPlayer methods

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

    public void setupTracks(ProviderQuery query) {
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

    // ---------- TrackInfoProvider methods

//    public boolean addTrackInfoProvider(TrackInfoProvider provider) {
//        if (mBound && provider != null) {
//            mService.addTrackInfoProvider(provider);
//            return true;
//        }
//        return false;
//    }

    public boolean addTrackInfoProvider(String name) {

        TrackInfoProvider provider = createProvider(name);
        if (mBound && provider != null) {
            mService.addTrackInfoProvider(provider);
            return true;
        }
        return false;
    }



    public boolean removeTrackInfoProvider(String name) {
        if (mBound) {
            List<String> list = mService.getTrackInfoProviderNames();
            int index = list.indexOf(name);
            if (index >= 0) {
                return mService.removeTrackInfoProvider(index);
            }
        }
        return false;
    }

    public boolean removeTrackInfoProvider(int index) {
        return mBound && mService.removeTrackInfoProvider(index);
    }

    public List<String> getTrackInfoProviderNames() {
        if (mBound) {
            return mService.getTrackInfoProviderNames();
        }
        return null;
    }

    /**
     * @param name, "SoundCloud", "HearThisAt", "Jamendo"
     * @return TrackInfoProvider instance, otherwise null if the name is not recognized
     */
    public static TrackInfoProvider createProvider(String name) {

        try {
            for (ProviderMetaInfo provider : availableProviders) {
                if (name.equalsIgnoreCase(provider.name)) {
                    return (TrackInfoProvider) provider.providerClass.newInstance();
                }
            }
        } catch (InstantiationException e) {
            Timber.e("InstantiationException. Create provider method failed to instantiate provider by name : " + name);
            return null;
        } catch (IllegalAccessException e) {
            Timber.e("IllegalAccessException. Create provider method failed to instantiate provider by name : " + name);
            return null;
        }
        Timber.e("Create provider method failed to instantiate provider by name : " + name);
        return null;
    }

    /**
     * Method to get information about all possible TrackInfoProviders
     * @return List of ProviderMetaInfo
     */
    public static ProviderMetaInfo[] availableProviders() {
        return availableProviders;
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
                (mProviders != null) &&
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
        if (!mService.isInitialized()) {
            for (TrackInfoProvider provider : mProviders) {
                mService.addTrackInfoProvider(provider);
            }
            mService.setContinuousPlay(true);
            mProviders = null;
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
