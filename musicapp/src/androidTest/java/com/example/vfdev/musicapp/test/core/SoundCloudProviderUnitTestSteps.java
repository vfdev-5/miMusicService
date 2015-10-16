package com.example.vfdev.musicapp.test.core;

import android.content.Context;
import android.test.AndroidTestCase;

import com.vfdev.mimusicservicelib.core.ProviderQuery;
import com.vfdev.mimusicservicelib.core.RestApiJsonProvider;
import com.vfdev.mimusicservicelib.core.SoundCloudProvider;
import com.vfdev.mimusicservicelib.core.TrackInfo;
import com.vfdev.mimusicservicelib.core.TrackInfoProvider;

import java.lang.reflect.Method;
import java.util.List;

import cucumber.api.CucumberOptions;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

@CucumberOptions(
        format = {"pretty","html:/data/data/com.example.vfdev.musicapp/html",
                "json:/data/data/com.example.vfdev.musicapp/jreport"},
        features = "features/core")
public class SoundCloudProviderUnitTestSteps extends AndroidTestCase {

    private SoundCloudProvider mProvider;

    public SoundCloudProviderUnitTestSteps() {
        super();
    }

    @Before
    public void before() {
        mProvider = new SoundCloudProvider();
    }

    @Given("^a SoundCloudProvider instance")
    public void given_a_SoundCloudProvider_instance() {
        assertTrue(mProvider != null);
    }

    @Then("^test data parsing")
    public void then_test_data_parsing() {

        Method parseResponseMethod = null;
        try {
            parseResponseMethod = RestApiJsonProvider.class.getDeclaredMethod("parseResponse", String.class);
            parseResponseMethod.setAccessible(true);
        } catch (Exception e) {
            // Nothing to catch
        }
        assertTrue(parseResponseMethod != null);

        String data = generateData();
        try {
            TrackInfoProvider.Result r = (TrackInfoProvider.Result) parseResponseMethod.invoke(mProvider, data);
            assertTrue(r.code == TrackInfoProvider.OK);
            assertTrue(r.tracks.size() == 3);

            checkTrack0(r.tracks.get(0));
            checkTrack1(r.tracks.get(1));
            checkTrack2(r.tracks.get(2));

        } catch (Exception e) {
            // Nothing to catch
            assertTrue(false);
        }
    }

    @Then("^test query request setup")
    public void then_test_query_request_setup() {

        Method setupRequestMethod = null;
        try {
            setupRequestMethod = SoundCloudProvider.class.getDeclaredMethod("setupRequest", int.class, boolean.class);
            setupRequestMethod.setAccessible(true);
        } catch (Exception e) {
            // Nothing to catch
        }
        assertTrue(setupRequestMethod != null);

        ProviderQuery query = new ProviderQuery();

        String r = "";
        try {
            query.text = "trance asot";
            mProvider.setQuery(query);
            r = (String) setupRequestMethod.invoke(mProvider, 5, false);
            assertTrue(r.equals(request1()));

            query.text = "\'armin van buuren\' asot";
            query.durationMin = 300000;
            mProvider.setQuery(query);
            r = (String) setupRequestMethod.invoke(mProvider, 5, false);
            assertTrue(r.equals(request2()));

            query.text = "алла пугачева";
            query.durationMin = -1;
            query.durationMax = 600000;
            mProvider.setQuery(query);
            r = (String) setupRequestMethod.invoke(mProvider, 5, false);
            assertTrue(r.equals(request3()));

            query.text = "Ca$h Ca$h";
            query.durationMin = 300000;
            query.durationMax = 600000;
            mProvider.setQuery(query);
            r = (String) setupRequestMethod.invoke(mProvider, 5, false);
            assertTrue(r.equals(request4()));

        } catch (Exception e) {
            // Nothing to catch
            assertTrue(false);
        }


    }

    // --------- Private methods

    private String request1() {
        return "http://api.soundcloud.com/tracks.json?q=trance+asot&limit=5&client_id=1abbcf4f4c91b04bb5591fe5a9f60821";
    }

    private String request2() {
        return "http://api.soundcloud.com/tracks.json?q=%27armin+van+buuren%27+asot&limit=5&duration[from]=300000&client_id=1abbcf4f4c91b04bb5591fe5a9f60821";
    }

    private String request3() {
        return "http://api.soundcloud.com/tracks.json?q=%D0%B0%D0%BB%D0%BB%D0%B0+%D0%BF%D1%83%D0%B3%D0%B0%D1%87%D0%B5%D0%B2%D0%B0&limit=5&duration[to]=600000&client_id=1abbcf4f4c91b04bb5591fe5a9f60821";
    }

    private String request4() {
        return "http://api.soundcloud.com/tracks.json?q=Ca%24h+Ca%24h&limit=5&duration[from]=300000&duration[to]=600000&client_id=1abbcf4f4c91b04bb5591fe5a9f60821";
    }


    private void checkTrack0(TrackInfo t) {
        assertTrue(t.id.equals("84394300"));
        assertTrue(t.duration == 265925);
        assertTrue(t.title.equals("Armin van Buuren feat. Trevor Guthrie - This Is What It Feels Like (W&W Remix)"));
        assertTrue(t.artist.equals("WandW"));
        assertTrue(t.resourceUrl.equals("https://soundcloud.com/wandw/armin-van-buuren-feat-trevor"));
        assertTrue(t.artworkUrl.equals("https://i1.sndcdn.com/artworks-000043579250-lw3mez-large.jpg"));
        assertTrue(t.streamUrl.equals("https://api.soundcloud.com/tracks/84394300/stream?client_id=1abbcf4f4c91b04bb5591fe5a9f60821"));
        assertTrue(t.tags.equals("\"Armin van Buuren\" \"Trevor Guthrie\" \"This Is What It Feels Like\" W&W Remix \"A State Of Trance\" Armada ASOT"));
    }

    private void checkTrack1(TrackInfo t) {
        assertTrue(t.id.equals("104939991"));
        assertTrue(t.duration == 174460);
        assertTrue(t.title.equals("OneRepublic - If I Lose Myself (Dash Berlin Remix)"));
        assertTrue(t.artist.equals("dashberlin"));
        assertTrue(t.resourceUrl.equals("https://soundcloud.com/dashberlin/one-republic-if-i-lose-myself"));
        assertTrue(t.artworkUrl.equals("https://i1.sndcdn.com/artworks-000055032144-edgk5h-large.jpg"));
        assertTrue(t.streamUrl.equals("https://api.soundcloud.com/tracks/104939991/stream?client_id=1abbcf4f4c91b04bb5591fe5a9f60821"));
        assertTrue(t.tags.equals("OneRepublic \"Dash Berlin\" \"If I Lose Myself\" Remix EDM Progressive Trance House DJ \"Electronic Dance Music\" ASOT \"A State Of Trance\" Dance \"Electronic Dance\" \"Electronic Pop\" Electro Electronic \"Dance Music\" Music Rework"));
    }

    private void checkTrack2(TrackInfo t) {
        assertTrue(t.id.equals("84911849"));
        assertTrue(t.duration == 3559830);
        assertTrue(t.title.equals("W&W - Live at Ultra Music Festival Miami @ A State Of Trance 600 Stage"));
        assertTrue(t.artist.equals("WandW"));
        assertTrue(t.resourceUrl.equals("https://soundcloud.com/wandw/w-w-live-at-ultra-music"));
        assertTrue(t.artworkUrl.equals("https://i1.sndcdn.com/artworks-000043866165-58l05b-large.jpg"));
        assertTrue(t.streamUrl.equals("https://api.soundcloud.com/tracks/84911849/stream?client_id=1abbcf4f4c91b04bb5591fe5a9f60821"));
        assertTrue(t.tags.equals("W&W \"Ultra Music Festival\" \"A State Of Trance\" 600 \"Armin van Buuren\" ASOT UMF"));
    }


    private String generateData() {
        return "[{\"download_url\":null,\"key_signature\":\"\",\"user_favorite\":false,\"likes_count\":152388,\"release\":\"\",\"attachments_uri\":\"https://api.soundcloud.com/tracks/84394300/attachments\",\"waveform_url\":\"https://w1.sndcdn.com/Po4DX6SqoAMb_m.png\",\"purchase_url\":\"http://www.beatport.com/track/this-is-what-it-feels-like-feat-trevor-guthrie-w-and-w-remix/4276028\",\"video_url\":null,\"streamable\":true,\"artwork_url\":\"https://i1.sndcdn.com/artworks-000043579250-lw3mez-large.jpg\",\"comment_count\":5277,\"commentable\":true,\"description\":\"Vote W&W in this years DJMag top 100!\\nhttps://top100djsvote.djmag.com\\n\\nOUT NOW: http://btprt.dj/17hmiRm\\n\\nHere's our remix for Armin van Buuren feat. Trevor Guthrie - This Is What It Feels Like. Coming out the 5th of April!\",\"download_count\":0,\"downloadable\":false,\"embeddable_by\":\"all\",\"favoritings_count\":152388,\"genre\":\"Progressive House\",\"isrc\":\"\",\"label_id\":null,\"label_name\":\"\",\"license\":\"all-rights-reserved\",\"original_content_size\":46895308,\"original_format\":\"wav\",\"playback_count\":9934953,\"purchase_title\":null,\"release_day\":null,\"release_month\":null,\"release_year\":null,\"reposts_count\":36691,\"state\":\"finished\",\"tag_list\":\"\\\"Armin van Buuren\\\" \\\"Trevor Guthrie\\\" \\\"This Is What It Feels Like\\\" W&W Remix \\\"A State Of Trance\\\" Armada ASOT\",\"track_type\":\"remix\",\"user\":{\"avatar_url\":\"https://i1.sndcdn.com/avatars-000125621971-n2quhk-large.jpg\",\"id\":630356,\"kind\":\"user\",\"permalink_url\":\"http://soundcloud.com/wandw\",\"uri\":\"https://api.soundcloud.com/users/630356\",\"username\":\"WandW\",\"permalink\":\"wandw\",\"last_modified\":\"2015/08/31 12:23:58 +0000\"},\"bpm\":null,\"user_playback_count\":null,\"id\":84394300,\"kind\":\"track\",\"created_at\":\"2013/03/22 10:58:48 +0000\",\"last_modified\":\"2015/10/10 16:03:16 +0000\",\"permalink\":\"armin-van-buuren-feat-trevor\",\"permalink_url\":\"https://soundcloud.com/wandw/armin-van-buuren-feat-trevor\",\"title\":\"Armin van Buuren feat. Trevor Guthrie - This Is What It Feels Like (W&W Remix)\",\"duration\":265925,\"sharing\":\"public\",\"stream_url\":\"https://api.soundcloud.com/tracks/84394300/stream\",\"uri\":\"https://api.soundcloud.com/tracks/84394300\",\"user_id\":630356,\"policy\":\"ALLOW\",\"monetization_model\":\"NOT_APPLICABLE\"},{\"download_url\":null,\"key_signature\":\"\",\"user_favorite\":false,\"likes_count\":53281,\"release\":\"\",\"attachments_uri\":\"https://api.soundcloud.com/tracks/104939991/attachments\",\"waveform_url\":\"https://w1.sndcdn.com/Dqg6yM9nWVGt_m.png\",\"purchase_url\":null,\"video_url\":\"http://www.youtube.com/watch?v=tB-sLxvQ_ZM&feature=youtu.be\",\"streamable\":true,\"artwork_url\":\"https://i1.sndcdn.com/artworks-000055032144-edgk5h-large.jpg\",\"comment_count\":1689,\"commentable\":true,\"description\":\"OneRepublic - If I Lose Myself (Dash Berlin Remix)\\n\\nBuy OneRepublic - Native\\non iTunes: http://tinyurl.com/kboqwov\\n\\nFollow us:\\nhttp://onerepublic.com\\nhttp://www.facebook.com/OneRepublic\\nhttp://twitter.com/OneRepublic\\nhttp://www.youtube.com/user/OneRepublic\",\"download_count\":0,\"downloadable\":false,\"embeddable_by\":\"all\",\"favoritings_count\":53281,\"genre\":\"Progressive Trance\",\"isrc\":\"\",\"label_id\":null,\"label_name\":\"\",\"license\":\"all-rights-reserved\",\"original_content_size\":30759806,\"original_format\":\"aiff\",\"playback_count\":3662976,\"purchase_title\":null,\"release_day\":null,\"release_month\":null,\"release_year\":null,\"reposts_count\":13017,\"state\":\"finished\",\"tag_list\":\"OneRepublic \\\"Dash Berlin\\\" \\\"If I Lose Myself\\\" Remix EDM Progressive Trance House DJ \\\"Electronic Dance Music\\\" ASOT \\\"A State Of Trance\\\" Dance \\\"Electronic Dance\\\" \\\"Electronic Pop\\\" Electro Electronic \\\"Dance Music\\\" Music Rework\",\"track_type\":\"remix\",\"user\":{\"avatar_url\":\"https://i1.sndcdn.com/avatars-000118752604-iji3wl-large.jpg\",\"id\":1057056,\"kind\":\"user\",\"permalink_url\":\"http://soundcloud.com/dashberlin\",\"uri\":\"https://api.soundcloud.com/users/1057056\",\"username\":\"dashberlin\",\"permalink\":\"dashberlin\",\"last_modified\":\"2015/09/18 11:09:58 +0000\"},\"bpm\":null,\"user_playback_count\":null,\"id\":104939991,\"kind\":\"track\",\"created_at\":\"2013/08/11 12:59:47 +0000\",\"last_modified\":\"2015/10/13 23:28:10 +0000\",\"permalink\":\"one-republic-if-i-lose-myself\",\"permalink_url\":\"https://soundcloud.com/dashberlin/one-republic-if-i-lose-myself\",\"title\":\"OneRepublic - If I Lose Myself (Dash Berlin Remix)\",\"duration\":174460,\"sharing\":\"public\",\"stream_url\":\"https://api.soundcloud.com/tracks/104939991/stream\",\"uri\":\"https://api.soundcloud.com/tracks/104939991\",\"user_id\":1057056,\"policy\":\"ALLOW\",\"monetization_model\":\"NOT_APPLICABLE\"},{\"download_url\":null,\"key_signature\":\"\",\"user_favorite\":false,\"likes_count\":17149,\"release\":\"\",\"attachments_uri\":\"https://api.soundcloud.com/tracks/84911849/attachments\",\"waveform_url\":\"https://w1.sndcdn.com/qmSJ9JSqe3oA_m.png\",\"purchase_url\":null,\"video_url\":null,\"streamable\":true,\"artwork_url\":\"https://i1.sndcdn.com/artworks-000043866165-58l05b-large.jpg\",\"comment_count\":909,\"commentable\":true,\"description\":\"This is our set recorded live from Ultra Music Festival in Miami at the A State Of Trance 600 stage. There's a lot of new exclusive material in here, hope you enjoy :)\",\"download_count\":0,\"downloadable\":false,\"embeddable_by\":\"all\",\"favoritings_count\":17149,\"genre\":\"Progressive House / Trance\",\"isrc\":\"\",\"label_id\":null,\"label_name\":\"\",\"license\":\"all-rights-reserved\",\"original_content_size\":113908263,\"original_format\":\"raw\",\"playback_count\":741691,\"purchase_title\":null,\"release_day\":null,\"release_month\":null,\"release_year\":null,\"reposts_count\":3551,\"state\":\"finished\",\"tag_list\":\"W&W \\\"Ultra Music Festival\\\" \\\"A State Of Trance\\\" 600 \\\"Armin van Buuren\\\" ASOT UMF\",\"track_type\":\"live\",\"user\":{\"avatar_url\":\"https://i1.sndcdn.com/avatars-000125621971-n2quhk-large.jpg\",\"id\":630356,\"kind\":\"user\",\"permalink_url\":\"http://soundcloud.com/wandw\",\"uri\":\"https://api.soundcloud.com/users/630356\",\"username\":\"WandW\",\"permalink\":\"wandw\",\"last_modified\":\"2015/08/31 12:23:58 +0000\"},\"bpm\":null,\"user_playback_count\":null,\"id\":84911849,\"kind\":\"track\",\"created_at\":\"2013/03/25 22:34:23 +0000\",\"last_modified\":\"2015/05/05 12:44:48 +0000\",\"permalink\":\"w-w-live-at-ultra-music\",\"permalink_url\":\"https://soundcloud.com/wandw/w-w-live-at-ultra-music\",\"title\":\"W&W - Live at Ultra Music Festival Miami @ A State Of Trance 600 Stage\",\"duration\":3559830,\"sharing\":\"public\",\"stream_url\":\"https://api.soundcloud.com/tracks/84911849/stream\",\"uri\":\"https://api.soundcloud.com/tracks/84911849\",\"user_id\":630356,\"policy\":\"ALLOW\",\"monetization_model\":\"NOT_APPLICABLE\"}]";
    }

}
