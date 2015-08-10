# miMusicService

## 'my' Music Service library: a media player of stream music from cloud resources

    - MusicPlayer
    - MusicService
    - MusicServiceHelper 

## Cloud resources :

    - <a href="www.soundcloud.com">SoundCloud</a>
    - <a href="https://hearthis.at/">HearThis.at</a>
    
## Simple usage :

    1) MainActivity implements MusicServiceHelper.OnReadyListener and override onReady method 
    2) declare MusicServiceHelper with a track info provider : SoundCloundProvider or HearThisAtProvider or both          3) In onDestroy  method release MusicServiceHelper
    4) Use methods of the MusicServiceHelper : 
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
    5) Subscribe to various events : 
        - MusicServiceHelper.ReadyEvent when service is ready 
        - MusicPlayer.StateEvent when player updates its internal event.state {Playing, Paused, Preparing, Stopped}
        - MusicPlayer.ErrorEvent when there is an error in the player, event.code {ERROR_DATASOURCE, ERROR_APP, ERROR_NO_AUDIOFOCUS}
        - MusicService.ErrorEvent when there is an error in the service, event.code {APP_ERR, CONNECTION_ERR, NOTRACKS_ERR, QUERY_ERR}
        - MusicService.QueryResponseEvent when service receives tracks from a provider (e.g. SoundCloud) and sends a list of tracks, event.tracks

``` java
public class MainActivity extends Activity implements MusicServiceHelper.OnReadyListener
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

    
## See examples for details :

    - musicserviceapp
    - musicplayerapp
