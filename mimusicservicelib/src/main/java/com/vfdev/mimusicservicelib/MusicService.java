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

//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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

    // Connection
    private TrackInfoProvider mTrackInfoProvider;
    private static final int TRACKS_COUNT=5;


    // Error codes
    public final static int APP_ERR = TrackInfoProvider.APP_ERR;
    public final static int CONNECTION_ERR = TrackInfoProvider.CONNECTION_ERR;
    public final static int NOTRACKS_ERR = TrackInfoProvider.NOTRACKS_ERR;
    public final static int QUERY_ERR = TrackInfoProvider.QUERY_ERR;

    // ImageLoader onLoadingComplete Callback instance
//    private _SimpleImageLoadingListener mLoadingListener;
//    private Bitmap mCurrentWaveform;

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

        // Image loader
        // Create global configuration and initialize ImageLoader with this config
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//                .build();
//        ImageLoader.getInstance().init(config);
//        mLoadingListener = new _SimpleImageLoadingListener();

        showNotification(getString(R.string.no_tracks));
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

    // ----------- ImageLoader onLoadingComplete listener
/*
    private class _SimpleImageLoadingListener extends SimpleImageLoadingListener {
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            // Do whatever you want with Bitmap
            mCurrentWaveform = loadedImage;
            if (mListener != null) {
                mListener.onWaveformLoaded(loadedImage);
            }
        }
    }
*/
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

//        Timber.v("onDownloadTrackInfo");
//        if (result.getBoolean("Result")) {
////            mTracks.addAll(tracks);
////            Timber.v("Add tracks -> tracklist size = " + mTracks.size());
//        } else {

//            if (mErrorListener != null) {
//                int errorType = result.getInt("ErrorType");
//                if (errorType == TrackInfoProvider.APP_ERR) {
//                    mErrorListener.onShowErrorMessage(getString(R.string.app_err));
//                } else if (errorType == TrackInfoProvider.CONNECTION_ERR) {
//                    mErrorListener.onShowErrorMessage(getString(R.string.connx_err));
//                } else if (errorType == TrackInfoProvider.NOTRACKS_ERR) {
//                    mErrorListener.onShowErrorMessage(getString(R.string.notrack_err));
//                }
//            }

////            DO NOT STOP PLAYING
////            mState = State.Stopped;
////            if (mListener != null) {
////                mListener.onStopped();
////            }
////            mMediaPlayer.reset();
//        }


    // ------------ Other methods

    public MusicPlayer getPlayer() {
        return mPlayer;
    }

    public TrackInfoProvider getTrackInfoProvider() {
        return mTrackInfoProvider;
    }

    public void setTrackInfoProvider(TrackInfoProvider provider) {
        mTrackInfoProvider = provider;
        mTrackInfoProvider.setOnDownloadTrackInfoListener(this);
        // NO NEED
        // Get some tracks
        //  mTrackInfoProvider.retrieveInBackground(TRACKS_COUNT);
    }

    public void setupTracks(String query) {
        if (mTrackInfoProvider != null) {
            mTrackInfoProvider.setQuery(query);
            mTrackInfoProvider.retrieveInBackground(TRACKS_COUNT);
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
