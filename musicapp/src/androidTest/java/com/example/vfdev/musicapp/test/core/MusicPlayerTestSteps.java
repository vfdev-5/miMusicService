package com.example.vfdev.musicapp.test.core;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.android.support.test.deps.guava.eventbus.EventBus;
import com.example.vfdev.musicapp.SimplePlayer;
import com.example.vfdev.musicapp.test.Commons;
import com.vfdev.mimusicservicelib.core.MusicPlayer;
import com.vfdev.mimusicservicelib.core.TrackInfo;

import org.junit.experimental.theories.Theory;

import java.util.ArrayList;
import java.util.EventListener;

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

//            if (tracksCount-1 > 0) {
//                assertTrue(mPlayer.playNextTrack());
//            } else {
//                assertFalse(mPlayer.playNextTrack());
//                assertTrue(mPlayerState == MusicPlayer.State.Preparing);
//            }


        }




    }

//    @Then("^test all public methods")
//    public void then_test_all_public_methods() {
//    }

    @When("^no network")
    public void when_no_network() {

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


    public void onEvent(MusicPlayer.StateEvent event) {
        mPlayerState = event.state;
    }

    public void onEvent(MusicPlayer.UpdateEvent event) {
        playlistSize = event.playlist.size();
    }



}
