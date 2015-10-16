package com.example.vfdev.musicapp.test.core;

import android.test.AndroidTestCase;

import com.vfdev.mimusicservicelib.core.MusicPlayer;

import cucumber.api.CucumberOptions;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * Created by vfomin on 8/10/15.
 */
@CucumberOptions(
        format = {"pretty","html:/data/data/com.example.vfdev.musicapp/html",
            "json:/data/data/com.example.vfdev.musicapp/jreport"},
        features = "features/core")
public class MusicPlayerUnitTestSteps extends AndroidTestCase {

    private MusicPlayer mPlayer;

    public MusicPlayerUnitTestSteps() {
        super();
    }

    @Before
    public void before() {
        mPlayer = new MusicPlayer(getContext());
    }


    @Given("^a MusicPlayer instance")
    public void given_a_MusicPlayer_instance() {
        assertTrue(mPlayer != null);
    }

    @Then("^test static method MusicPlayer getDuration")
    public void then_test_static_method_MusicPlayer_getDuration() {
        // Static method : getDuration
        assertEquals(MusicPlayer.getDuration(0), "00:00");
        assertEquals(MusicPlayer.getDuration(-1), "-1:-1:-1");
        assertEquals(MusicPlayer.getDuration(100*1000), "01:40");
        assertEquals(MusicPlayer.getDuration(1000*1000), "16:40");

    }

    @Then("^check the initial state")
    public void then_check_the_initial_state() {
        // mPlayer is simply initialized -> No tracks
        checkInitialState(0);
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
}
