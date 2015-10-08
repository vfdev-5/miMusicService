package com.vfdev.mimusicservicelib.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * Created by vfomin on 5/31/15.
 *
 *
 * MusicPlayer implements a simple music player using Android MediaPlayer
 * It has two main attributes :
 * 1) Tracks (list of tracks to play)
 * 2) TrackHistory (list of played and playing tracks)
 * A track is realized using the class TrackInfo.
 *
 *
 */
public class MusicPlayer implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        AudioManager.OnAudioFocusChangeListener
{


    // MediaPlayer
    private MediaPlayer mMediaPlayer;

    // AudioManager
    private AudioManager mAudioManager;
    private boolean hasAudiofocus=false;
    private boolean lostAudiofocusWhilePlaying=false;

    public static final int ERROR_DATASOURCE = 1;
    public static final int ERROR_APP = 2;
    public static final int ERROR_NO_AUDIOFOCUS = 3;
    public static final int ERROR_MP_ERROR = 3;

    // Player states
    public enum State {
        Stopped,    // media player is stopped and not prepared to play
        Preparing,  // media player is preparing...
        Playing,    // playback active (media player ready!)
        Paused      // playback paused (media player ready!)
    }
    private State mState = State.Stopped;

    // Tracks
    private static final int TRACKSHISTORY_LIMIT=50;
    private ArrayList<TrackInfo> mTracksHistory; // All played tracks
    private ArrayList<TrackInfo> mTracks; // Track to play in future

    // -------- Public methods

    public MusicPlayer(Context context) {

        mMediaPlayer = new MediaPlayer();

        // Make sure the media player will acquire a wake-lock while playing. If we don't do
        // that, the CPU might go to sleep while the song is playing, causing playback to stop.
        mMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);

        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.reset();

        // fetch tracks
        mTracks = new ArrayList<>();
        mTracksHistory = new ArrayList<>();

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        // release audio focus
        releaseAudioFocus();
    }

    public TrackInfo getPlayingTrack() {
        if (!mTracksHistory.isEmpty()){
            // get the last track
            return mTracksHistory.get(mTracksHistory.size()-1);
        }
        return null;
    }

    public void clearTracksHistory() {
        mTracksHistory.clear();
    }

    public void clearTracks() {
        mTracks.clear();
    }

    public ArrayList<TrackInfo> getTracksHistory() {
        return mTracksHistory;
    }

    public ArrayList<TrackInfo> getTracks() {
        return mTracks;
    }

    public int getTracksCount() {
        return mTracks.size();
    }

    public void setTracks(ArrayList<TrackInfo> tracks) {
        if (tracks != null) {
            mTracks = tracks;
        }
    }
    public void addTracks(ArrayList<TrackInfo> tracks) {
        mTracks.addAll(tracks);
    }
    public void addTrack(TrackInfo track) {
        mTracks.add(track);
    }

    public boolean isPlaying() {
        return mState == State.Playing;
    }

    public void pause(){
        Timber.v("Pause");

        if (mState == State.Playing) {
            mMediaPlayer.pause();
            changeState(State.Paused, null);
        }
    }


    public boolean play() {
        Timber.v("Play");

        if (mState == State.Paused) {
            mMediaPlayer.start();
            changeState(State.Playing, null);
            return true;
        } else if (mState == State.Stopped) {
            // Only when Player is stopped, request audio focus
            if (requestAudioFocus()) {
                return playNextTrack();
            }
        } else if (mState == State.Preparing) {
            // Probably it is ok
            return true;
        }
        return false;
    }

    public int getTrackDuration() {
        if (mState == State.Playing ||
                mState == State.Paused) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getTrackCurrentPosition() {
        if (mState == State.Playing ||
                mState == State.Paused) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void rewindTrackTo(int seconds) {
        if (mState == State.Playing ||
                mState == State.Paused) {
            mMediaPlayer.seekTo(seconds);
        }
    }

    public boolean playNextTrack() {

        // Prepare player : get track's info: title, duration, stream_url,  waveform_url
        Timber.v("playNextTrack");

        // This fixes the problem if user first starts the playback
        // using this method
        if (mState == State.Stopped) {
            // Only when Player is stopped, request audio focus
            if (!requestAudioFocus()) {
                Timber.v("Failed to get audio focus");
                return false;
            }
        }

        // this should solve the problem of multiple 'machine gun' touches problem
//        if (mState == State.Preparing)
//            return true;

        if (mTracks.isEmpty()) {
            Timber.v("Track list is empty");
            toStoppedState();
            return false;
        }

        // get track index randomly :
        debugShowTracks();
        int index = new Random().nextInt(mTracks.size());
        TrackInfo track = mTracks.remove(index);
//        debugShowTracks();
        mTracksHistory.add(track);

        if (mTracksHistory.size() > TRACKSHISTORY_LIMIT) {
            mTracksHistory.remove(0);
        }

        EventBus.getDefault().post(new UpdateEvent(mTracksHistory));

        return prepareAndPlay(track);
    }

    public boolean playPrevTrack() {
        Timber.v("playPrevTrack");

        // this should solve the problem of multiple 'machine gun' touches problem
////        if (mState == State.Preparing)
////            return true;

        // This fixes the problem if user first starts the playback
        // using this method
        if (mState == State.Stopped) {
            // Only when Player is stopped, request audio focus
            if (!requestAudioFocus()) {
                Timber.v("Failed to get audio focus");
                return false;
            }
        }

        if (mTracksHistory.size() > 1) {

            mTracksHistory.remove(mTracksHistory.size() - 1);
            TrackInfo track = mTracksHistory.get(mTracksHistory.size() - 1);

            EventBus.getDefault().post(new UpdateEvent(mTracksHistory));

            return prepareAndPlay(track);
        }
        return false;
    }

    static public String getDuration(int durationInMillis) {
        int hours = ((int) Math.floor(durationInMillis * 0.001 / 3600.0)) % 60;
        int minutes = ((int) Math.floor(durationInMillis * 0.001 / 60.0)) % 60;
        int seconds = ((int) Math.floor(durationInMillis * 0.001)) % 60;
        if (hours != 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }

    // ------- Protected & Private methods

    private void debugShowTracks() {
        Timber.d("Tracks : ------- ");
        for (TrackInfo track  : mTracks) {
            Timber.d("\t " + track.id + " | \t" + track.title);
        }
        Timber.d("---------------- ");
    }

    private void toStoppedState() {
        changeState(State.Stopped, null);
        mMediaPlayer.reset();
    }

    private void toPreparingState(TrackInfo trackInfo) {
        changeState(State.Preparing, trackInfo);
        mMediaPlayer.reset();
    }

    private void changeState(State state, TrackInfo trackInfo) {
        if (mState != state) {
            mState = state;
            EventBus.getDefault().post(new StateEvent(mState, trackInfo));
        }
    }

    private boolean prepareAndPlay(TrackInfo track) {

        toPreparingState(track);

        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(track.streamUrl);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            Timber.e(e, "prepareAndPlay : request error : " + e.getMessage());
            EventBus.getDefault().post(new ErrorEvent(ERROR_DATASOURCE, e.getMessage()));

            toStoppedState();
            return false;
        }

        // debug : get tags:
        Timber.v("Track title : "+ track.title);
        Timber.v("Track tags : "+ track.tags);

        return true;
    }


    private boolean requestAudioFocus() {
        // request audio focus :
        int result = mAudioManager.requestAudioFocus(this,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Timber.v("Audio focus request is not granted");
            EventBus.getDefault().post(new ErrorEvent(ERROR_NO_AUDIOFOCUS, "Audio focus request is not granted"));
            hasAudiofocus = false;
        } else {
            hasAudiofocus = true;
        }
        return hasAudiofocus;
    }

    private void releaseAudioFocus() {
        hasAudiofocus=false;
        mAudioManager.abandonAudioFocus(this);
    }


    // -------- MediaPlayer listeners

    public void onCompletion(MediaPlayer player) {
        Timber.v("onCompletion MediaPlayer");
        // The media player finished playing the current Track, so we go ahead and start the next.
        playNextTrack();
    }

    public void onPrepared(MediaPlayer player) {
        Timber.v("onPrepared MediaPlayer");

        // check mTracksHistory.size
        if (mTracksHistory.isEmpty()){
            toStoppedState();
            EventBus.getDefault().post(new ErrorEvent(ERROR_APP, "Track history is empty"));
        }

//        TrackInfo currentTrack = mTracksHistory.get(mTracksHistory.size()-1);
//        currentTrack.duration = player.getDuration();
//        changeState(State.Preparing, currentTrack);

        // The media player is done preparing. That means we can start playing!
        // This handles the case when :
        // audio focus is lost while player was in State.Preparing
        // Player should not start
        if (hasAudiofocus) {
            Timber.v("Has audio focus. Start playing");
            changeState(State.Playing, null);
            player.start();
        } else {
            Timber.v("Has no audio focus. Do not start");
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        Timber.i("what=" + String.valueOf(what) + ", extra=" + String.valueOf(extra));
        //EventBus.getDefault().post(new ErrorEvent(ERROR_MP_ERROR, "MediaPlayer error : " + "what=" + String.valueOf(what) + ", extra=" + String.valueOf(extra)));
        toStoppedState();
        return true; // true indicates we handled the error
    }


    // ------------ OnAudioFocusChangeListener
    @Override
    public void onAudioFocusChange(int focusChange) {
        Timber.v("onAudioFocusChange");

        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            Timber.v("AUDIOFOCUS_LOSS_TRANSIENT");

            hasAudiofocus=false;
            // Pause playback
            if (mMediaPlayer.isPlaying()) {
                pause();
                lostAudiofocusWhilePlaying=true;
            }
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            Timber.v("AUDIOFOCUS_GAIN");

            // Resume playback
            hasAudiofocus=true;
            if (!mMediaPlayer.isPlaying() &&
                    lostAudiofocusWhilePlaying) {
                play();
                lostAudiofocusWhilePlaying=false;
            }
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            Timber.v("AUDIOFOCUS_LOSS");
            // Audio focus loss is permanent
            releaseAudioFocus();
        }
    }


    // ---------- Music Player Listener Interface

    public class StateEvent {
        public final TrackInfo trackInfo;
        public final State state;
        public StateEvent(State state, TrackInfo trackInfo) {
            this.state = state;
            this.trackInfo = trackInfo;
        }
    }

    public class ErrorEvent {
        public final int code;
        public final String message;
        public ErrorEvent(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    public class UpdateEvent {
        public final ArrayList<TrackInfo> playlist;
        public UpdateEvent(ArrayList<TrackInfo> playlist) {
            this.playlist = playlist;
        }
    }
}
