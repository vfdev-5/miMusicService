package com.example.vfdev.musicapp.core;

//import org.junit.Test;
//import java.util.regex.Pattern;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.vfdev.mimusicservicelib.core.MusicPlayer;
import com.vfdev.mimusicservicelib.core.TrackInfo;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.greenrobot.event.EventBus;

/**
 * Created by vfomin on 8/9/15.
 */
@RunWith(AndroidJUnit4.class)
public class MusicPlayerUnitTests {

    private static final String TAG = "MusicPlayerUnitTests";

    private MusicPlayer mPlayer;
    private MusicPlayer.State mState;

    @Test
    public void testMethods() {

        // Static method : getDuration
        assertEquals(MusicPlayer.getDuration(0), "00:00");
        assertEquals(MusicPlayer.getDuration(-1), "-1:-1:-1");
        assertEquals(MusicPlayer.getDuration(100*1000), "01:40");
        assertEquals(MusicPlayer.getDuration(1000*1000), "16:40");

        Context context = InstrumentationRegistry.getTargetContext();
        assertTrue(context != null);
        mPlayer = new MusicPlayer(context);
        mState = MusicPlayer.State.Stopped;


        // mPlayer is simply initialized -> No tracks
        checkInitialState(0);
        checkPlayback(false);

        // Add a track
        TrackInfo trackInfo = getOneGoodTrackInfo();
        mPlayer.addTrack(trackInfo);
        checkInitialState(1);

//        // Start playback :
//        // a) normal use-case
//        assertTrue(mPlayer.play());
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

    private void checkPlayback(boolean hasTracks) {

        if (!hasTracks) {
            assertFalse(mPlayer.play());
            mPlayer.pause(); // This should not change the state
            assertFalse(mPlayer.playNextTrack());
            assertFalse(mPlayer.playPrevTrack());
        } else {

            mPlayer.play();

        }
    }

//    public void onEvent(MusicPlayer.StateEvent event) {
//        MusicPlayer.State state = event.state;
//        assertTrue(state == mState);
//        assertTrue(false);
//    }
//
//    public void onEvent(MusicPlayer.ErrorEvent event) {
////        int code = event.code;
////        if (code == MusicPlayer.ERROR_APP) {
////
////        } else if (code == MusicPlayer.ERROR_NO_AUDIOFOCUS) {
////
////        } else if (code == MusicPlayer.ERROR_DATASOURCE) {
////
////        }
//    }
//
//    public void onEvent(MusicPlayer.UpdateEvent event) {
//
//    }

    private TrackInfo getOneGoodTrackInfo() {
        TrackInfo trackInfo = new TrackInfo();

        trackInfo.id = "178781982";
        trackInfo.title = "Au5 - Crossroad (feat. Danyka Nadeau)";
        trackInfo.duration = -1;
        trackInfo.tags = "Monstercat Au5 \\\"Danyka Nadeau\\\" House Electro Electronic Music Progressive EDM Crossroad";
        trackInfo.streamUrl = "https://api.soundcloud.com/tracks/178781982/stream";

        return trackInfo;
    }

    private TrackInfo getOneBadTrackInfo() {
        TrackInfo trackInfo = new TrackInfo();

        trackInfo.id = "178781982";
        trackInfo.title = "Au5 - Crossroad (feat. Danyka Nadeau)";
        trackInfo.duration = -1;
        trackInfo.tags = "Monstercat Au5 \\\"Danyka Nadeau\\\" House Electro Electronic Music Progressive EDM Crossroad";
        trackInfo.streamUrl = "https://api.soundcloud.com/tracks/178781982/streamdfsldkfn";

        return trackInfo;
    }







}
