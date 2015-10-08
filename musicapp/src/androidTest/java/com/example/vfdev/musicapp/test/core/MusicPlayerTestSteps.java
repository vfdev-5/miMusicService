package com.example.vfdev.musicapp.test.core;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.test.ActivityInstrumentationTestCase2;

import com.example.vfdev.musicapp.SimplePlayer;
import com.example.vfdev.musicapp.test.Commons;
import com.vfdev.mimusicservicelib.core.MusicPlayer;
import com.vfdev.mimusicservicelib.core.TrackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cucumber.api.CucumberOptions;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Created by vfomin on 8/10/15.
 */
@CucumberOptions(
        format = {"pretty","html:/data/data/com.example.vfdev.musicapp/html",
                "json:/data/data/com.example.vfdev.musicapp/jreport"},
        features = "features/core")
public class MusicPlayerTestSteps extends ActivityInstrumentationTestCase2<SimplePlayer> {

    private MusicPlayer mPlayer;
    private MusicPlayer.State mPlayerState = MusicPlayer.State.Stopped;
    private int playlistSize = 0;
    private boolean isLoadingAndPlaying = false;
    private CountDownLatch signal;

    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener;

    public MusicPlayerTestSteps() {
        super(SimplePlayer.class);

    }

    @Before
    public void before() {
        assertNotNull(getActivity());
        Context context = getActivity().getApplicationContext();
        assertTrue(context != null);
        mPlayer = new MusicPlayer(context);
        de.greenrobot.event.EventBus.getDefault().register(this);
        onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                MusicPlayerTestSteps.this.onBufferingUpdate(mp, percent);
            }
        };
    }

    @After
    public void after() {
        de.greenrobot.event.EventBus.getDefault().unregister(this);
    }

    @Given("^no tracks")
    public void given_no_tracks() {
        mPlayer.clearTracks();
    }

    @Given("^(\\d+) good track info added")
    public void given_a_good_track_info_added(final int tracksCount) {
        TrackInfo goodTrack = Commons.getOneGoodTrackInfo();
        for (int i=0;i<tracksCount;i++) {
            mPlayer.addTrack(goodTrack);
        }
    }

    @Then("^test public methods when given (\\d+) tracks")
    public void then_test_public_methods(final int tracksCount) {
        assertTrue(tracksCount == mPlayer.getTracksCount());
        mPlayer.pause();

        if (tracksCount == 0){
            assertFalse(mPlayer.play());
            assertTrue(mPlayerState == MusicPlayer.State.Stopped);

            assertFalse(mPlayer.playNextTrack());
            assertTrue(mPlayerState == MusicPlayer.State.Stopped);

            assertFalse(mPlayer.playPrevTrack());
            assertTrue(mPlayerState == MusicPlayer.State.Stopped);

        } else {
            int size = mPlayer.getTracksHistory().size();
            assertTrue(mPlayer.play());

            assertTrue(size + 1 == playlistSize);
            assertTrue(mPlayerState == MusicPlayer.State.Preparing);
        }

    }


    @Then("^test play method when given (\\d+) tracks")
    public void then_test_play_method(final int tracksCount) throws Throwable {
        assertTrue(tracksCount == mPlayer.getTracksCount());
        mPlayer.pause();

        if (tracksCount == 0){
            assertFalse(mPlayer.play());
            assertTrue(mPlayerState == MusicPlayer.State.Stopped);
        } else {

            // THIS DOES NOT WORK
            // PROBLEM IS :
            // DO NOT KNOW HOW TO LAUNCH EVENT LOOP IN MAIN THREAD
            // DISPATCHING COMING EVENTS

//            signal = new CountDownLatch(3);

            int size = mPlayer.getTracksHistory().size();
            assertTrue(mPlayer.play());
            assertTrue(size + 1 == playlistSize);
            assertTrue(mPlayerState == MusicPlayer.State.Preparing);

//            signal.await(10, TimeUnit.SECONDS);
//            assertTrue(isLoadingAndPlaying);
//            assertTrue(mPlayerState == MusicPlayer.State.Playing);
        }

    }

//    @Then("^data is loaded and player is playing")
//    public void then_data_is_loaded_and_player_is_playing() {
//        assertTrue(isLoadingAndPlaying);
//        assertTrue(mPlayerState == MusicPlayer.State.Playing);
//    }


//    @Then("^test all public methods")
//    public void then_test_all_public_methods() {
//    }

    @When("^no network")
    public void when_no_network() {
        enableNetwork(false);
    }

    @When("^network is enabled")
    public void when_network_is_enabled() {
        enableNetwork(true);
        try {
            Field f = mPlayer.getClass().getDeclaredField("mMediaPlayer");
            f.setAccessible(true);
            MediaPlayer player = (MediaPlayer) f.get(mPlayer);
            player.setOnBufferingUpdateListener(onBufferingUpdateListener);
        } catch (Exception e) {
            // Failed to get MediaPlayer
            assertTrue(false);
        }
    }

    private void enableNetwork(boolean enable) {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Method m = cm.getClass().getDeclaredMethod("setMobileDataEnabled", boolean.class);
            m.invoke(cm, enable);
        } catch (Exception e) {
            // Failed to disable network
            assertTrue(false);
        }
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enable);
    }


    private void checkInitialState(int nbOfTracks) {
        assertTrue(mPlayer.getPlayingTrack() == null);
        assertTrue(mPlayer.getTracksCount() == nbOfTracks);
        assertTrue(mPlayer.getTracks() != null);
        assertTrue(mPlayer.getTracksHistory() != null);
        assertTrue(mPlayer.getTracksHistory().isEmpty());
        assertTrue(!mPlayer.isPlaying());
        assertTrue(mPlayer.getTrackCurrentPosition() == 0);
        assertTrue(mPlayer.getTrackDuration() == 0);
    }


    private void onBufferingUpdate(MediaPlayer mp, int percent) {
        isLoadingAndPlaying = percent > 0;
        if (signal != null) {
            signal.countDown();
        }
    }

    public void onEvent(MusicPlayer.StateEvent event) {
        mPlayerState = event.state;
        if (signal != null) {
            signal.countDown();
        }
    }

    public void enEvent(MusicPlayer.ErrorEvent event) {

    }

    public void onEvent(MusicPlayer.UpdateEvent event) {
        playlistSize = event.playlist.size();
    }



}
