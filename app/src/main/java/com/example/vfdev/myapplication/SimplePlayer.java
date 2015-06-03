package com.example.vfdev.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.okhttp.Response;
import com.vfdev.mimusicservicelib.MusicServiceHelper;
import com.vfdev.mimusicservicelib.core.MusicPlayer;
import com.vfdev.mimusicservicelib.core.SoundCloundProvider;
import com.vfdev.mimusicservicelib.core.TrackInfo;
import com.vfdev.mimusicservicelib.core.TrackInfoProvider;

import org.w3c.dom.Text;

import java.util.ArrayList;

import timber.log.Timber;

/*

[{
    "kind":"track",
    "id":178781982,
    "created_at":"2014/11/26 19:50:36 +0000",
    "user_id":8553751,
    "duration":417422,
    "commentable":true,
    "state":"finished",
    "original_content_size":73618308,
    "last_modified":"2015/05/31 03:04:44 +0000",
    "sharing":"public",
    "tag_list":"Monstercat Au5 \"Danyka Nadeau\" House Electro Electronic Music Progressive EDM Crossroad",
    "permalink":"au5-crossroad-feat-danyka-nadeau",
    "streamable":true,
    "embeddable_by":"all",
    "downloadable":false,
    "purchase_url":"https://itunes.apple.com/ca/album/crossroad-feat.-danyka-nadeau/id943935414?i=943935648&ign-mpt=uo%3D4",
    "label_id":null,
    "purchase_title":"Support on iTunes",
    "genre":"Trance",
    "title":"Au5 - Crossroad (feat. Danyka Nadeau)",
    "description":"Support on iTunes: http://monster.cat/1FrpUAC
        \nSupport on Beatport: http://monster.cat/120nObU
        \nSupport on Bandcamp: http://monster.cat/11UFQgC
        \n---
        \n
        Watch on YouTube: http://monster.cat/1FlXeYD\n
        Listen on Spotify: http://monster.cat/1y2jJSs\n\n
        ▼ Follow Monstercat\nYouTube: http://www.youtube.com/Monstercat\n
        Spotify: http://monster.cat/1hGrCWk\n
        Facebook: http://facebook.com/Monstercat\n
        Twitter: http://twitter.com/Monstercat\n
        Instagram: http://instagram.com/monstercat\n
        Vine: https://vine.co/monstercat\n
        SoundCloud: http://soundcloud.com/Monstercat\n
        Google+: https://plus.google.com/+Monstercat\n
        Podcast: http://live.monstercat.com/\n
        \n▼ Follow Au5\nFacebook: http://www.facebook.com/TheAu5\nTwitter: http://twitter.com/Au5music\n
        Soundcloud: https://soundcloud.com/au5\nYoutube: http://www.youtube.com/au5music\n\n▼ Follow Danyka Nadeau\n
        Facebook: http://facebook.com/danykanadeaumusic\nTwitter: http://twitter.com/ndanyka&\n
        Soundcloud: https://soundcloud.com/danykanadeau\nYoutube: http://youtube.com/user/danykanadeauofficial\n\n▼
        Want some new Merchandise?\nhttp://monster.cat/MonstercatShop",
    "label_name":"Monstercat",
    "release":"MCS282",
    "track_type":"original",
    "key_signature":"",
    "isrc":"",
    "video_url":null,
    "bpm":null,
    "release_year":2014,
    "release_month":11,
    "release_day":18,
    "original_format":"wav",
    "license":"all-rights-reserved",
    "uri":"https://api.soundcloud.com/tracks/178781982",
    "user":{
        "id":8553751,
        "kind":"user",
        "permalink":"monstercat",
        "username":"Monstercat",
        "last_modified":"2015/05/29 17:46:43 +0000",
        "uri":"https://api.soundcloud.com/users/8553751",
        "permalink_url":"http://soundcloud.com/monstercat",
        "avatar_url":"https://i1.sndcdn.com/avatars-000147435518-59movz-large.jpg"
        },
    "permalink_url":"http://soundcloud.com/monstercat/au5-crossroad-feat-danyka-nadeau",
    "artwork_url":"https://i1.sndcdn.com/artworks-000098406561-bx0m7f-large.jpg",
    "waveform_url":"https://w1.sndcdn.com/ZSBluhpDXNzQ_m.png",
    "stream_url":"https://api.soundcloud.com/tracks/178781982/stream",
    "playback_count":639122,
    "download_count":0,
    "favoritings_count":15397,
    "comment_count":489,
    "attachments_uri":"https://api.soundcloud.com/tracks/178781982/attachments",
    "policy":"ALLOW"
 }]

 */
public class SimplePlayer extends Activity implements
        MusicPlayer.OnStateChangeListener,
        TrackInfoProvider.OnDownloadTrackInfoListener
{

//    private MusicServiceHelper mMSHelper;
    private MusicPlayer mPlayer;

    TextView titleTV;
    TextView tagsTV;
    TextView durationTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_player);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        titleTV = (TextView) findViewById(R.id.title);
        tagsTV = (TextView) findViewById(R.id.tags);
        durationTV = (TextView) findViewById(R.id.duration);

//        mMSHelper = new MusicServiceHelper(this, new SoundCloundProvider(), this.getClass());
//        mMSHelper.startMusicService();
        mPlayer = new MusicPlayer(this);
        mPlayer.setStateChangeListener(this);

        setupTracks();

    }


    private void setupTracks() {
        Timber.v("setupTracks");
        SoundCloundProvider provider = new SoundCloundProvider();
        provider.setQuery("Sia Chandelier");
        provider.setOnDownloadTrackInfoListener(this);
        provider.retrieveInBackground(5);
    }

    @Override
    protected void onDestroy() {

        mPlayer.release();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple_player, menu);
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
//        mMSHelper.stopMusicService();
        mPlayer.release();
        finish();
    }

    public void onPlayButtonClicked(View view) {

        ToggleButton button = (ToggleButton) view;
        if (button.isChecked()) {

//            mMSHelper.play();
            mPlayer.play();

        } else {

//            mMSHelper.pause();
            mPlayer.pause();
        }

    }

    public void onNextButtonClicked(View view) {
        mPlayer.playNextTrack();
    }

    public void onPrevButtonClicked(View view) {
        mPlayer.playPrevTrack();
    }

    // --------- TrackInfoProvider.OnDownloadTrackInfoListener

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
        } else if (code == TrackInfoProvider.APP_ERR) {
            Toast.makeText(this, "Application error", Toast.LENGTH_SHORT).show();
        } else if (code == TrackInfoProvider.CONNECTION_ERR) {
            Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
        } else if (code == TrackInfoProvider.NOTRACKS_ERR) {
            Toast.makeText(this, "No tracks found on the query", Toast.LENGTH_SHORT).show();
        }
    }


    // --------- MusicPlayer.OnStateChangeListener

    @Override
    public void onPrepared(TrackInfo trackInfo) {
        Toast.makeText(this, "On prepared", Toast.LENGTH_SHORT).show();
        durationTV.setText(MusicPlayer.getDuration(trackInfo.duration));
    }

    @Override
    public void onStarted() {
        Toast.makeText(this, "On Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaused() {
        Toast.makeText(this, "On Paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onIsPreparing(TrackInfo trackInfo) {
        Toast.makeText(this, "On IsPreparing", Toast.LENGTH_SHORT).show();
        titleTV.setText(trackInfo.title);
        tagsTV.setText(trackInfo.tags);

    }

    @Override
    public void onStopped() {
        Toast.makeText(this, "On Stopped", Toast.LENGTH_SHORT).show();
    }



}
