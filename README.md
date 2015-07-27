# miMusicService

## 'my' Music Service library: a media player of stream music from cloud resources

    - MusicPlayer
    - MusicService
    - MusicServiceHelper 

## Cloud resources :

    - SoundCloud : www.soundcloud.com
    
## Simple usage :

    1) MainActivity implements MusicServiceHelper.OnReadyListener and override onReady method 
    2) declare MusicServiceHelper with SoundCloundProvider        
    3) In onDestroy  method release MusicServiceHelper
    4) Use methods of the MusicServiceHelper : 
        - play 
        - pause 
        - playNextTrack 
        - playPrevTrack
        - getPlayingTrackInfo
        - clearPlaylist
        - setupTracks
        - stopMusicService
        etc
``` java
public class MainActivity extends Activity implements MusicServiceHelper.OnReadyListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ...
        mMSHelper = new MusicServiceHelper(this, new SoundCloundProvider(), MainActivity.class);
        mMSHelper.startMusicService(this);
        
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
