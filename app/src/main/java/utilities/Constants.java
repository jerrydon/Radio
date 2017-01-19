package utilities;

/**
 * Created by Jayaraj on 9/6/16.
 */
public class Constants {
    public static final String CONNECTED = "connected";
    public static final String STARTED = "started";
    public static final String STOPED = "stoped";
    public static final String TAG_CONNECT_CHECK = "isConnectedToRadio";
    public static final String TAG_BRECIVER = "radioPause/play";
    public static final String TAG_BRECIVER_CONNECTION = "checkingTheinternetConnection";
    public static final String TAG_BRECIVER_SONG_DURATION = "bReciverSongDuration";
    public static String RADIO_API = "d87e8a9b-c450-4a54-8c43-5b485a4f6ebb";
    public static final String RADIO_JUL_URL = "http://streaming.radio.co/s965f098b5/listen";
    public static String RADIO_JUL_GUID = "198801fa-861d-47ad-b007-f7a731922044";
    public static String RADIO_GRAUDR_PLAYER = "http://listen.radionomy.com/radiogradur1";
    public static String RADIO_GRAUDR_GUID = "738e6a3e-ce6c-4b4b-ab61-06a8478c848f";
    public static String PNL_RADIO_PLAYER = "http://listen.radionomy.com/pnlradio";
    public static String PNL_RADIO_GUID = "a1c2e85e-c11b-434a-8db8-24385a523b6a";
    public static String RAP_FR_RADIO_PLAYER = "http://listen.radionomy.com/rapfrradio";
    public static String RAP_FR_RADIO_GUID = "2deaf0fc-9c1b-4c8b-8c1b-0d868b43181d";
    public static String AFRO_BEATS_RADIO_PLAYER = "http://listen.radionomy.com/afrobeatradio";
    public static String AFRO_BEATS_GUID = "d81e9dae-fbcd-4d32-8684-d5505c62f988";
    public static final String RECENT_PLAYED_LIST = "http://api.radionomy.com/tracklist.cfm?radiouid=" + AFRO_BEATS_GUID + "&apikey=" + RADIO_API + "&amount=10&type=xml&cover=yes";
    public static final String TOP_TRACKS = "http://api.radionomy.com/toptracks.cfm?radiouid=" + AFRO_BEATS_GUID + "&apikey=" + RADIO_API + "&amount=20&days=2&type=xml&cover=yes";
    public static final String SONGS_DETAILS = "http://api.radionomy.com/currentsong.cfm?radiouid=" + AFRO_BEATS_GUID + "&apikey=" + RADIO_API + "&callmeback=yes&type=xml&cover=yes&previous=yes";
    public static String radioPlayerUrl;
    public static String getRadioPlayerUrl() {
        return radioPlayerUrl;
    }
    public static void setRadioPlayerUrl(String radioPlayerUrl) {
        Constants.radioPlayerUrl = radioPlayerUrl;
    }
}
