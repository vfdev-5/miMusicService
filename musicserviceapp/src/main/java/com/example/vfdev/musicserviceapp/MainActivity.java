package com.example.vfdev.musicserviceapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vfdev.mimusicservicelib.MusicService;
import com.vfdev.mimusicservicelib.MusicServiceHelper;
import com.vfdev.mimusicservicelib.core.MusicPlayer;
import com.vfdev.mimusicservicelib.core.ProviderQuery;
import com.vfdev.mimusicservicelib.core.TrackInfo;
import com.vfdev.mimusicservicelib.core.TrackInfoProvider;

import de.greenrobot.event.EventBus;
import timber.log.Timber;


/*
TODO :
    23/06/2015
    1) OK = Restore UI when main activity is closed and started
    2) OK = Log system
    3) Think about ClientId of SoundCloud system
    4) Query handle :
        OK = a) option : append or set
        OK = b) have a callback
    24/06/2015
    5) OK = MusicServiceHelper : get playlist, trackshistory
    6) OK = MusicServiceHelper : clear playlist, clear trackshistory
    07/07/2015
    7)
*/

public class MainActivity extends Activity
{

    private MusicServiceHelper mMSHelper;

    TextView titleTV;
    TextView tagsTV;
    TextView durationTV;
    EditText queryET;
    ImageView artworkIV;
    EditText minDurationET;
    EditText maxDurationET;
    CheckBox hearthisatCB;
    CheckBox soundcloudCB;
    CheckBox jamendoCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }

        EventBus.getDefault().register(this);

        titleTV = (TextView) findViewById(R.id.title);
        tagsTV = (TextView) findViewById(R.id.tags);
        durationTV = (TextView) findViewById(R.id.duration);
        queryET = (EditText) findViewById(R.id.query);
        artworkIV = (ImageView) findViewById(R.id.artwork);
        minDurationET = (EditText) findViewById(R.id.minDuration);
        maxDurationET = (EditText) findViewById(R.id.maxDuration);
        soundcloudCB = (CheckBox) findViewById(R.id.soundcloud);
        hearthisatCB = (CheckBox) findViewById(R.id.hearthisat);
        jamendoCB = (CheckBox) findViewById(R.id.jamendo);

//        mMSHelper = MusicServiceHelper.getInstance().init(this, new SoundCloudProvider(), MainActivity.class);
//        mMSHelper = MusicServiceHelper.getInstance().init(this, new HearThisAtProvider(), MainActivity.class);
//        mMSHelper = MusicServiceHelper.getInstance().init(
//                this,
//                new TrackInfoProvider[] {new SoundCloudProvider(), new HearThisAtProvider()},
//                MainActivity.class
//        );


        String[] providerList = getProviders();

        TrackInfoProvider[] providers = new TrackInfoProvider[providerList.length];
        for (int i=0;i<providerList.length;i++) {
            providers[i] = MusicServiceHelper.createProvider(providerList[i]);
        }
        mMSHelper = MusicServiceHelper.getInstance().init(this, providers, MainActivity.class);
        mMSHelper.startMusicService();

        setupCheckBoxes(providerList);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mMSHelper.release();
        ImageLoader.getInstance().destroy();
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

    public void onSoundCloudClicked(View view) {
        if (soundcloudCB.isChecked()) {
            mMSHelper.addTrackInfoProvider(MusicServiceHelper.createProvider("SoundCloud"));
        } else {
            mMSHelper.removeTrackInfoProvider("SoundCloud");
        }
        String msg = "Currently, there are/is " + mMSHelper.getTrackInfoProviderNames().size() + " provider(s)";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "Providers : " + mMSHelper.getTrackInfoProviderNames());
    }

    public void onJamendoClicked(View view) {
        if (jamendoCB.isChecked()) {
            mMSHelper.addTrackInfoProvider(MusicServiceHelper.createProvider("Jamendo"));
        } else {
            mMSHelper.removeTrackInfoProvider("Jamendo");
        }
        String msg = "Currently, there are/is " + mMSHelper.getTrackInfoProviderNames().size() + " provider(s)";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "Providers : " + mMSHelper.getTrackInfoProviderNames());
    }


    public void onHearThisAtClicked(View view) {
        if (hearthisatCB.isChecked()) {
            mMSHelper.addTrackInfoProvider(MusicServiceHelper.createProvider("HearThisAt"));
        } else {
            mMSHelper.removeTrackInfoProvider("HearThisAt");
        }
        String msg = "Currently, there are/is " + mMSHelper.getTrackInfoProviderNames().size() + " provider(s)";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "Providers : " + mMSHelper.getTrackInfoProviderNames());
    }

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
        mMSHelper.clearPlaylist();

        ProviderQuery params = new ProviderQuery();
        if (!minDurationET.getText().toString().isEmpty()) {
            try {
                params.durationMin = Integer.parseInt(minDurationET.getText().toString())*1000;
            } catch (NumberFormatException e) {
                params.durationMin = -1;
            }

        }
        if (!maxDurationET.getText().toString().isEmpty()) {
            try {
                params.durationMax = Integer.parseInt(maxDurationET.getText().toString())*1000;
            } catch (NumberFormatException e) {
                params.durationMax = -1;
            }
        }
        params.text = queryET.getText().toString();
        mMSHelper.setupTracks(params);



    }

    protected String [] getProviders() {
        SharedPreferences prefs = getSharedPreferences("MSA", 0);
        String providers = prefs.getString("providers", "SoundCloud;HearThisAt");
        return providers.split(";");
    }

    protected void setupCheckBoxes(String[] providers) {
        soundcloudCB.setChecked(false);
        hearthisatCB.setChecked(false);
        jamendoCB.setChecked(false);
        for (String provider : providers) {
            if (provider.equalsIgnoreCase("SoundCloud")) {
                soundcloudCB.setChecked(true);
            } else if (provider.equalsIgnoreCase("HearThisAt")) {
                hearthisatCB.setChecked(true);
            } else if (provider.equalsIgnoreCase("Jamendo")) {
                jamendoCB.setChecked(true);
            }

        }
    }


    protected void restoreUi(TrackInfo trackInfo, boolean isPlaying) {

        durationTV.setText(MusicPlayer.getDuration(trackInfo.duration));
        titleTV.setText(trackInfo.title);
        tagsTV.setText(trackInfo.tags);
        ToggleButton button = (ToggleButton) findViewById(R.id.play);
        button.setChecked(isPlaying);

    }

    // --------- MusicServiceHelper.OnReadyListener

    public void onEvent(MusicServiceHelper.ReadyEvent event) {
        TrackInfo t = mMSHelper.getPlayingTrackInfo();
        if (t != null) {
            restoreUi(t, mMSHelper.isPlaying());
        }
    }


    // --------- MusicPlayer.StateEvent & MusicPlayer.ErrorEvent
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

            if (trackInfo.fullInfo.containsKey("artwork_url")){
                String artworkUri = trackInfo.fullInfo.get("artwork_url");
                Timber.v("Artwork URI : "+ artworkUri);
                if (!artworkUri.isEmpty()) {
                    ImageLoader.getInstance().displayImage(
                            artworkUri,
                            artworkIV
                    );
                }
            }

        } else if (event.state == MusicPlayer.State.Stopped) {
//            Toast.makeText(this, "DEBUG : On Stopped", Toast.LENGTH_SHORT).show();
            ToggleButton button = (ToggleButton) findViewById(R.id.play);
            button.setChecked(false);
        }
    }

    public void onEvent(MusicPlayer.ErrorEvent event) {
        if (event.code == MusicPlayer.ERROR_DATASOURCE ||
                event.code == MusicPlayer.ERROR_APP ||
                event.code == MusicPlayer.ERROR_NO_AUDIOFOCUS) {
            Toast.makeText(this, "Ops, there is an internal error", Toast.LENGTH_SHORT).show();
        }
    }

    // --------- MusicService.ErrorEvent
    public void onEvent(MusicService.ErrorEvent event) {
        if (event.code == MusicService.APP_ERR) {
            Toast.makeText(this, "Ops, there is an application error", Toast.LENGTH_SHORT).show();
        } else if (event.code == MusicService.NOTRACKS_ERR) {
            Toast.makeText(this, "No tracks found", Toast.LENGTH_SHORT).show();
        } else if (event.code == MusicService.CONNECTION_ERR) {
            Toast.makeText(this, "Ops, internet connection problem", Toast.LENGTH_SHORT).show();
        } else if (event.code == MusicService.QUERY_ERR) {
            Toast.makeText(this, "There is a problem with your query", Toast.LENGTH_SHORT).show();
        }
    }

    // --------- MusicService.QueryResponseEvent
    public void onEvent(MusicService.QueryResponseEvent event) {
        Toast.makeText(this, "Found " + event.tracks.size() + " tracks", Toast.LENGTH_SHORT).show();
    }


}
