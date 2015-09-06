package com.vfdev.mimusicservicelib;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;

import com.example.vfdev.mimusicservicelib.R;
import com.vfdev.mimusicservicelib.core.MusicPlayer;
import com.vfdev.mimusicservicelib.core.TrackInfo;
import com.vfdev.mimusicservicelib.core.TrackInfoProvider;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import timber.log.Timber;




public class MusicService extends Service implements
        TrackInfoProvider.OnDownloadTrackInfoListener
{

    private static final int NOTIFICATION_ID = 1;
    private Bitmap mServiceIcon;
    private Class<?> mActivityClass;

    // Wifi manager
    private WifiManager.WifiLock mWifiLock;

    // Bound service
    private IBinder mBinder;

    private MusicPlayer mPlayer;
    boolean mContinuousPlay = false;

    // Connection
//    private TrackInfoProvider mTrackInfoProvider;
    private List<TrackInfoProvider> mTrackInfoProviders;
    private static final int TRACKS_COUNT=5;


    // Error codes
    public final static int APP_ERR = TrackInfoProvider.APP_ERR;
    public final static int CONNECTION_ERR = TrackInfoProvider.CONNECTION_ERR;
    public final static int NOTRACKS_ERR = TrackInfoProvider.NOTRACKS_ERR;
    public final static int QUERY_ERR = TrackInfoProvider.QUERY_ERR;

    // -------- Service methods

    @Override
    public void onCreate() {
        Timber.v("Creating service");

        EventBus.getDefault().register(this);

        mServiceIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        mBinder = new LocalBinder();
        mPlayer = new MusicPlayer(this);

        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "MY_WIFI_LOCK");
        mWifiLock.acquire();

    }

    /**
     * Called when we receive an Intent. When we receive an intent sent to us via startService(),
     * this is the method that gets called. So here we react appropriately depending on the
     * Intent's action, which specifies what is being requested of us.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.v("onStartCommand");

        if (mActivityClass == null && intent.hasExtra("ActivityClass")) {
            mActivityClass = (Class<?>) intent.getSerializableExtra("ActivityClass");
            if (mActivityClass == null) {
                Timber.e("Failed to cast to Class<?>");
            }
        }

        showNotification(mPlayer.getPlayingTrack() != null ?
                mPlayer.getPlayingTrack().title : getString(R.string.no_tracks));

        return START_NOT_STICKY; // Don't automatically restart this Service if it is killed
    }

    @Override
    public void onDestroy() {
        // Service is being killed, so make sure we release our resources
        Timber.v("Destroy music service");
        EventBus.getDefault().unregister(this);
        releaseResources();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Timber.v("onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Timber.v("onUnbind");
        return true;
    }

    // ------------ Local binder

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    // ------------ OnDownloadTrackInfoListener

    @Override
    public void onDownloadTrackInfo(TrackInfoProvider.Result result) {
        int code = result.code;
        Timber.v("onDownloadTrackInfo : code = " + code);
        ArrayList<TrackInfo> tracks = result.tracks;

        if (code == TrackInfoProvider.OK) {
            if (tracks == null) {
                Timber.e("Resulting code is OK but no tracks provided");
                return;
            }
            Timber.v("Tracks : " + tracks.size());
            for (TrackInfo t : tracks) {
                Timber.v(t.toString());
            }
            mPlayer.addTracks(tracks);

            EventBus.getDefault().post(new QueryResponseEvent(tracks));

        } else if (code == TrackInfoProvider.APP_ERR) {
            EventBus.getDefault().post(new ErrorEvent(MusicService.APP_ERR, "Application error"));
        } else if (code == TrackInfoProvider.CONNECTION_ERR) {
            EventBus.getDefault().post(new ErrorEvent(MusicService.CONNECTION_ERR, "Connection error"));
        } else if (code == TrackInfoProvider.NOTRACKS_ERR) {
            EventBus.getDefault().post(new ErrorEvent(MusicService.NOTRACKS_ERR, "No tracks found"));
        } else if (code == TrackInfoProvider.QUERY_ERR) {
            EventBus.getDefault().post(new ErrorEvent(MusicService.QUERY_ERR, "Query error"));
        }
    }

    // ------------ Other methods

    public MusicPlayer getPlayer() {
        return mPlayer;
    }

    public void setContinuousPlay(boolean value) {
        mContinuousPlay = value;
    }

    public List<String> getTrackInfoProviderNames() {
        List<String> names = new ArrayList<>();
        if (!checkProvidersExist()) return names;

        for (TrackInfoProvider provider : mTrackInfoProviders) {
            names.add(provider.getName());
        }
        return names;
    }

    public synchronized void addTrackInfoProvider(TrackInfoProvider provider) {
        if (mTrackInfoProviders == null) {
            mTrackInfoProviders = new ArrayList<>();
        }
        mTrackInfoProviders.add(provider);
        provider.setOnDownloadTrackInfoListener(this);
    }

    public synchronized boolean removeTrackInfoProvider(int index) {
        if (!checkProvidersExist()) return false;
        if (index >= 0 && index < mTrackInfoProviders.size()) {
            mTrackInfoProviders.remove(index);
            return true;
        }
        return false;
    }

    @Deprecated
    public void setTrackInfoProvider(TrackInfoProvider provider) {
        if (mTrackInfoProviders == null) {
            mTrackInfoProviders = new ArrayList<>();
        }
        mTrackInfoProviders.add(0, provider);
        provider.setOnDownloadTrackInfoListener(this);
    }



    public void setupTracks(String query) {
        if (!checkProvidersExist()) return;
        retrieveTracks(query);
    }

    // ------- Private methods

    private boolean checkProvidersExist() {
        if (mTrackInfoProviders == null) {
            Timber.w("TrackInfoProviders are not yet initialized");
            return false;
        }
        return true;
    }

    private void retrieveTracks(String query) {
        for (TrackInfoProvider provider : mTrackInfoProviders) {
            if (query != null) provider.setQuery(query);
            provider.retrieveInBackground(TRACKS_COUNT);
        }
    }

    private void releaseResources() {
        // stop being a foreground service
        stopForeground(true);

        mPlayer.release();
        // we can also release the Wifi lock, if we're holding it
        if (mWifiLock.isHeld()) mWifiLock.release();

    }

    private void showNotification(String trackTitle) {

        Timber.v("Show notification");

        Context context = getApplicationContext();

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(trackTitle)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(mServiceIcon)
                .setOngoing(true);

        if (mActivityClass != null) {
            // Create a notification area notification so the user
            // can get back to a mActivityClass
            final Intent notificationIntent = new Intent(getApplicationContext(), mActivityClass);
            final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            builder.setContentIntent(pendingIntent);
        }

        final Notification notification = builder.build();

        // Put this Service in a foreground state, so it won't
        // readily be killed by the system
        startForeground(NOTIFICATION_ID, notification);

    }

    // --------- MusicPlayer.StateEvent
    public void onEvent(MusicPlayer.StateEvent event) {
        if (event.state == MusicPlayer.State.Preparing) {
            showNotification(event.trackInfo.title);
            if (checkProvidersExist()  &&
                    mContinuousPlay &&
                    mPlayer.getTracks().size() < 2) {
                retrieveTracks(null); // without query
            }
        }
    }

    // --------- MusicService.QueryResponseEvent
    public class QueryResponseEvent {
        public final ArrayList<TrackInfo> tracks;
        public QueryResponseEvent(ArrayList<TrackInfo> tracks) {
            this.tracks = tracks;
        }
    }

    // --------- MusicService.ErrorEvent
    public class ErrorEvent {
        public final int code;
        public final String message;
        public ErrorEvent(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

}
