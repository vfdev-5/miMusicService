package com.example.vfdev.musicserviceapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.vfdev.mimusicservicelib.MusicServiceHelper;
import com.vfdev.mimusicservicelib.core.MusicPlayer;
import com.vfdev.mimusicservicelib.core.SoundCloundProvider;
import com.vfdev.mimusicservicelib.core.TrackInfo;

import de.greenrobot.event.EventBus;
import timber.log.Timber;


/*
TODO :
    23/06/2015
    1) OK = Restore UI when main activity is closed and started
    2) Log system
    3) Think about ClientId of SoundCloud system
    4) Query handle :
        a) option : append or set
        b) have a callback
*/

public class MainActivity extends Activity implements
        MusicServiceHelper.OnReadyListener
{

    private MusicServiceHelper mMSHelper;

    TextView titleTV;
    TextView tagsTV;
    TextView durationTV;
    EditText queryET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (BuildConfig.DEBUG) {
//            Timber.plant(new Timber.DebugTree());
//        }

        EventBus.getDefault().register(this);

        titleTV = (TextView) findViewById(R.id.title);
        tagsTV = (TextView) findViewById(R.id.tags);
        durationTV = (TextView) findViewById(R.id.duration);
        queryET = (EditText) findViewById(R.id.query);

        mMSHelper = new MusicServiceHelper(this, new SoundCloundProvider(), MainActivity.class);
        mMSHelper.startMusicService(this);

    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        if (hasFocus) {
//
//        }
//    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mMSHelper.release();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // -------- Other class methods

    public void onExitButtonClicked(View view) {
        mMSHelper.stopMusicService();
        finish();
    }

    public void onPlayButtonClicked(View view) {

        ToggleButton button = (ToggleButton) view;
        if (button.isChecked()) {
            mMSHelper.play();

        } else {
            mMSHelper.pause();
        }

    }

    public void onNextButtonClicked(View view) {
        mMSHelper.playNextTrack();
    }

    public void onPrevButtonClicked(View view) {
        mMSHelper.playPrevTrack();
    }

    public void onSendQuery(View view) {
        mMSHelper.setupTracks(queryET.getText().toString());
    }


    protected void restoreUi(TrackInfo trackInfo, boolean isPlaying) {

        durationTV.setText(MusicPlayer.getDuration(trackInfo.duration));
        titleTV.setText(trackInfo.title);
        tagsTV.setText(trackInfo.tags);
        ToggleButton button = (ToggleButton) findViewById(R.id.play);
        button.setChecked(isPlaying);

    }

    // --------- MusicServiceHelper.OnReadyListener

    @Override
    public void onReady() {
        TrackInfo t = mMSHelper.getPlayingTrackInfo();
        if (t != null) {
            restoreUi(t, mMSHelper.isPlaying());
        }
    }


    // --------- MusicPlayer.StateEvent
    public void onEvent(MusicPlayer.StateEvent event) {

        if (event.state == MusicPlayer.State.Playing) {
            Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
            ToggleButton button = (ToggleButton) findViewById(R.id.play);
            button.setChecked(true);
//        } else if (event.state == MusicPlayer.State.Paused) {
////            Toast.makeText(this, "DEBUG : On Paused", Toast.LENGTH_SHORT).show();
        } else if (event.state == MusicPlayer.State.Preparing) {
            Toast.makeText(this, "Preparing ...", Toast.LENGTH_SHORT).show();
            TrackInfo trackInfo = event.trackInfo;
            titleTV.setText(trackInfo.title);
            tagsTV.setText(trackInfo.tags);
            if (trackInfo.duration > 0) {
                durationTV.setText(MusicPlayer.getDuration(trackInfo.duration));
            }
        } else if (event.state == MusicPlayer.State.Stopped) {
//            Toast.makeText(this, "DEBUG : On Stopped", Toast.LENGTH_SHORT).show();
            ToggleButton button = (ToggleButton) findViewById(R.id.play);
            button.setChecked(false);
        }

    }

    // --------- MusicPlayer.OnStateChangeListener
//    @Override
//    public void onPrepared(TrackInfo trackInfo) {
////        Toast.makeText(this, "DEBUG : On prepared", Toast.LENGTH_SHORT).show();
//        durationTV.setText(MusicPlayer.getDuration(trackInfo.duration));
//    }
//
//    @Override
//    public void onStarted() {
//        Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onPaused() {
////        Toast.makeText(this, "DEBUG : On Paused", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onIsPreparing(TrackInfo trackInfo) {
//        Toast.makeText(this, "Preparing ...", Toast.LENGTH_SHORT).show();
//        titleTV.setText(trackInfo.title);
//        tagsTV.setText(trackInfo.tags);
//
//    }
//
//    @Override
//    public void onStopped() {
////        Toast.makeText(this, "DEBUG : On Stopped", Toast.LENGTH_SHORT).show();
//        ToggleButton button = (ToggleButton) findViewById(R.id.play);
//        button.setChecked(false);
//    }


}
