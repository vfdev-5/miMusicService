# miMusicService
a library with a media player and a service used to listen streaming music from cloud resources :

- [SoundCloud](https://soundcloud.com)
- [HearThis.at](https://hearthis.at)

## Simple usage :
    1) Initialize the singleton MusicServiceHelper with a track info provider : SoundCloundProvider or HearThisAtProvider or both in your activity         
    2) Release MusicServiceHelper in onDestroy method 
    3) Use methods of the MusicServiceHelper : 
        - play 
        - pause 
        - playNextTrack 
        - playPrevTrack
        - getPlayingTrackInfo
        - clearPlaylist
        - setupTracks
        - startMusicService
        - stopMusicService
        etc
    4) Subscribe to various events (using EventBus.getDefault().register(this)):
        - MusicServiceHelper.ReadyEvent when service is ready (e.g. to update UI)
        - MusicPlayer.StateEvent when player updates its internal event.state {Playing, Paused, Preparing, Stopped}
        - MusicPlayer.ErrorEvent when there is an error in the player, event.code {ERROR_DATASOURCE, ERROR_APP, ERROR_NO_AUDIOFOCUS}
        - MusicService.ErrorEvent when there is an error in the service, event.code {APP_ERR, CONNECTION_ERR, NOTRACKS_ERR, QUERY_ERR}
        - MusicService.QueryResponseEvent when service receives tracks from a provider (e.g. SoundCloud) and sends a list of tracks, event.tracks

``` java
public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ...
        mMSHelper = new MusicServiceHelper.getInstance().init(this, new SoundCloundProvider(), MainActivity.class);
        // Or for multiple providers :
        // mMSHelper = new MusicServiceHelper.getInstance().init(this, 
        //                      new TrackInfoProvider{ new SoundCloundProvider(), new HearThisAtProvider() },
        //                      MainActivity.class);
        mMSHelper.startMusicService();
        
    }
    // ...
    @Override
    public void onReady() {
        // Synchonize UI
    }
    // ...
    
    public void onNextButtonClicked(View view) {
        mMSHelper.playNextTrack();
    }
    
    // ...
    
    @Override
    protected void onDestroy() {
        // ...
        mMSHelper.release();
        // ...
    }
    
    // ...
}
```

    
## See examples for more details :

- [musicplayerapp](https://github.com/vfdev-5/miMusicService/blob/master/musicapp), a single activity application with an instance of [MusicPlayer](https://github.com/vfdev-5/miMusicService/blob/master/mimusicservicelib/src/main/java/com/vfdev/mimusicservicelib/core/MusicPlayer.java)
- [musicserviceapp](https://github.com/vfdev-5/miMusicService/blob/master/musicserviceapp), a single activity application which uses [MusicServiceHelper](https://github.com/vfdev-5/miMusicService/blob/master/mimusicservicelib/src/main/java/com/vfdev/mimusicservicelib/MusicServiceHelper.java) to start a [MusicService](https://github.com/vfdev-5/miMusicService/blob/master/mimusicservicelib/src/main/java/com/vfdev/mimusicservicelib/MusicService.java) that plays music.
