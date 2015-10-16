# miMusicService [![Build Status](https://travis-ci.org/vfdev-5/miMusicService.svg?branch=master)](https://travis-ci.org/vfdev-5/miMusicService)
a library with a media player and a service used to listen streaming music from free cloud resources :

- [SoundCloud](https://soundcloud.com)
- [HearThis.@t](https://hearthis.at)
- [Jamendo](https://www.jamendo.com)

## Library structure :

The idea of the library is to provide an <i>easy</i> access to free streaming music. It that way there is a player (MusicPlayer) that plays tracks (TrackInfo) from a cloud resource (TrackInfoProvider). Tracks are searched from resources by a specific queries (ProviderQuery). In other words, TrackInfoProvider produces a list of TrackInfo specified by a ProviderQuery and MusicPlayer plays this tracks.

A MusicPlayer is be also integrated into MusicService. There is a helper (MusicServiceHelper) that can easily start MusicService, load/play/change tracks and respond to new queries.

Core part of the library :

- [MusicPlayer](https://github.com/vfdev-5/miMusicService/blob/master/mimusicservicelib/src/main/java/com/vfdev/mimusicservicelib/core/MusicPlayer.java) a wrapping class on android MediaPlayer class to play streaming music.
- [TrackInfo](https://github.com/vfdev-5/miMusicService/blob/master/mimusicservicelib/src/main/java/com/vfdev/mimusicservicelib/core/TrackInfo.java) a streaming data model class containing info like track title, duration, streaming url, tags, etc
- [TrackInfoProvider](https://github.com/vfdev-5/miMusicService/blob/master/mimusicservicelib/src/main/java/com/vfdev/mimusicservicelib/core/TrackInfoProvider.java) an abstract class to produce TrackInfo's
    - [SoundCloudProvider](https://github.com/vfdev-5/miMusicService/blob/master/mimusicservicelib/src/main/java/com/vfdev/mimusicservicelib/core/SoundCloudProvider.java) an implementation of TrackInfoProvider to produce tracks from [SoundCloud](https://soundcloud.com)
    - [HearThisAtProvider](https://github.com/vfdev-5/miMusicService/blob/master/mimusicservicelib/src/main/java/com/vfdev/mimusicservicelib/core/HearThisAtProvider.java) an implementation of TrackInfoProvider to produce tracks from [HearThis.@t](https://hearthis.at)
    - [JamendoProvider](https://github.com/vfdev-5/miMusicService/blob/master/mimusicservicelib/src/main/java/com/vfdev/mimusicservicelib/core/JamendoProvider.java) an implementation of TrackInfoProvider to produce tracks from [Jamendo](https://www.jamendo.com)
- [ProviderQuery](https://github.com/vfdev-5/miMusicService/blob/master/mimusicservicelib/src/main/java/com/vfdev/mimusicservicelib/core/ProviderQuery.java) represents a query for TrackInfoProvider to select tracks by text and duration


Service and helper :

- [MusicService](https://github.com/vfdev-5/miMusicService/blob/master/mimusicservicelib/src/main/java/com/vfdev/mimusicservicelib/MusicService.java) an android service that embeds MusicPlayer and a list of TrackInfoProviders. It can continuously load and play tracks, change to next/previous track, setup queries etc.
- [MusicServiceHelper](https://github.com/vfdev-5/miMusicService/blob/master/mimusicservicelib/src/main/java/com/vfdev/mimusicservicelib/MusicServiceHelper.java) is a helper that starts MusicService and perform all its useful methods. It also allows to manipulate available TrackInfoProviders.

## Simple usage :
    1) Initialize the singleton MusicServiceHelper with track info providers : SoundCloudProvider, HearThisAtProvider, JamendoProvider, etc in your activity
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
        mMSHelper = new MusicServiceHelper.getInstance().init(this, new SoundCloudProvider(), MainActivity.class);
        //
        // Or for multiple providers :
        // mMSHelper = new MusicServiceHelper.getInstance().init(this, 
        //                      new TrackInfoProvider{ new SoundCloudProvider(), new HearThisAtProvider() },
        //                      MainActivity.class);
        //
        // Or create using string names :
        // int nbOfProviders = 3; // or 1,2,3,4, etc
        // TrackInfoProvider providers = new TrackInfoProvider[nbOfProvider];
        // providers[0] = MusicServiceHelper.createProvider("SoundCloud");
        // providers[1] = MusicServiceHelper.createProvider("HearThisAt");
        // providers[2] = MusicServiceHelper.createProvider("Jamendo");
        // ...
        // mMSHelper = new MusicServiceHelper.getInstance().init(this, providers, MainActivity.class);
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
