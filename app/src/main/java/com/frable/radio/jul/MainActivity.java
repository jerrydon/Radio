package com.frable.radio.jul;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import utilities.CircleTransform;
import utilities.Utilities;
import io.fabric.sdk.android.Fabric;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import utilities.Constants;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.OnFragmentInteractionListener {

    private ProgressBar progressBar;
    private PlayerService playerService;
    //    RadioManager mRadioManager = RadioManager.with(this);
    private Intent startServiceIntent;
    private BroadcastReceiver bReciver;
    private ImageView playButton;
    private AudioManager audioManager;
    private TextView tvChannelTitle,tvArtistName;
    private DrawerLayout drawerLayout;
    //    private NavigationView navigationView;
    InterstitialAd mInterstitialAd;
    private FirebaseAnalytics mFirebaseAnalytics;
    SeekBar seekBar;
    long playduration;
    ImageView imvBanner;

    @Override
    public void onFragmentInteraction(int position) {

    }


    public class SettingsContentObserver extends ContentObserver {

        public SettingsContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d("volumeControl", "Settings change detected");
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            seekBar.setProgress(currentVolume);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(getApplicationContext(), getString(R.string.appID));
        loadBannerAdds();
        initilizeIntersitialAdd();
        requestNewInterstitial();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "AIzaSyCN_W2g76UwVBxHmpWDOAjrrMiX9-Q5a6E");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Radio Jul");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

/**SETTING UP EVENT TO LISTERN TO THE VOLUME KEY PRESS AND CHANGE THE SEEKBAR IN APP*/
        SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver(new Handler());
        this.getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        playButton = (ImageView) findViewById(R.id.playButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        seekBar = (SeekBar) findViewById(R.id.music_seek);
        ImageView imvShareToAllApps = (ImageView) findViewById(R.id.imv_share_to_all_apps);
        imvBanner = (ImageView) findViewById(R.id.imv_banner2);
        ImageView imvHeaderBanner = (ImageView) findViewById(R.id.profile_image);
//        Picasso.with(MainActivity.this).load(R.mipmap.ic_launcher).transform(new CircleTransform()).into(imvHeaderBanner);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar.setProgress(currentVolume);
        tvChannelTitle = (TextView) findViewById(R.id.tv_title);
        tvArtistName = (TextView) findViewById(R.id.tv_artist);


        imvShareToAllApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToAll();
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Intent bReciver = new Intent();
                bReciver.setAction("completedAsync");
                sendBroadcast(bReciver);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Fabric.with(MainActivity.this, new Crashlytics());
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        /**
         * SEEKBAR for volume control
         * */
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        startServiceIntent = new Intent(MainActivity.this, PlayerService.class);
        bindService(startServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        startService(startServiceIntent);

        /**Reciver to dismiss the loader*/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.TAG_BRECIVER);
        bReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!intent.getStringExtra(Constants.TAG_CONNECT_CHECK).equals(Constants.CONNECTED)) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        };
        registerReceiver(bReciver, intentFilter);

        /**
         * Reciver to check the connection and change the icon
         * */
        IntentFilter intentFilterForConnection = new IntentFilter();
        intentFilterForConnection.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        BroadcastReceiver bReciverCONNECTIONCHECK = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (null != playerService && !Utilities.isConnectingToInternet(MainActivity.this) && playerService.isRadioPlaying()) {
                    playButton.setImageResource(R.mipmap.ic_play_btn);
                    playerService.stopRadio();
                    Utilities.showToast(MainActivity.this, getString(R.string.connectionLost));
                }
            }
        };
        registerReceiver(bReciverCONNECTIONCHECK, intentFilterForConnection);

        IntentFilter changeIconOnOnDestroy = new IntentFilter();
        changeIconOnOnDestroy.addAction("iconChangeOnOnDestroy");
        BroadcastReceiver bReciverChangeIconOnOnDestroy = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                View view = MainActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                if (playerService != null && playerService.isRadioPlaying()) {
                    playButton.setImageResource(R.mipmap.ic_pause_btn);
                } else {
                    playButton.setImageResource(R.mipmap.ic_play_btn);
                }
            }
        };
        registerReceiver(bReciverChangeIconOnOnDestroy, changeIconOnOnDestroy);


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.isConnectingToInternet(MainActivity.this)) {
                    boolean isRadioPlaying = playerService.isRadioPlaying();
                    if (isRadioPlaying) {
                        playerService.stopRadio();
                        playButton.setImageResource(R.mipmap.ic_play_btn);

                    } else {
                        new SongsDetailsAsync(false).execute();
                        progressBar.setVisibility(View.VISIBLE);
                        playButton.setImageResource(R.mipmap.ic_pause_btn);
                        playerService.startRadio();
                    }
                } else {
                    Utilities.showToast(MainActivity.this, getString(R.string.noConnection));
                }
            }
        });

        new SongsDetailsAsync(true).execute();


//        if (new SongsDetailsAsync().getStatus()== AsyncTask.Status.FINISHED) {
//            Timer t = new Timer();
//            t.scheduleAtFixedRate(new TimerTask() {
//                                      @Override
//                                      public void run() {
//
//                                          Log.d("playDuration",playduration+"");
//                                          new SongsDetailsAsync().execute(songsDetails);
//
//                                      }
//                                  },
//                    0, playduration);
//        }

//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//
//            }
//        }, 0, 1000);
        if (!Utilities.isConnectingToInternet(MainActivity.this)) {
            Utilities.showToast(MainActivity.this, getString(R.string.noConnection));
        }
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!playerService.isRadioPlaying()) {
//                    progressBar.setVisibility(View.VISIBLE);
//                    playButton.setImageResource(R.mipmap.ic_pause_btn);
//                    playerService.startRadio();
//                }
//            }
//        }, 1000);

    }


    /**
     * loading and initilising the banner and initisting adds
     */
    private void initilizeIntersitialAdd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.intersitialAdd));
        // TODO: 10/13/16 Demo id for testing
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void loadBannerAdds() {
        //        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");//demo
        final AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdLoaded() {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_view_container);
//                Animation slide = null;
//                slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
//                        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                        0.0f, Animation.RELATIVE_TO_SELF, 5.0f);
                //                slide.setDuration(400);
//                slide.setFillAfter(true);
//                slide.setFillEnabled(true);
//                linearLayout.startAnimation(slide);
                mAdView.setVisibility(View.VISIBLE);
//                Animation bottomUp = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottom_top);
//                mAdView.startAnimation(bottomUp);
            }
        });
    }

    private void showInterstitialAd(long delay) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (playerService.isRadioPlaying()) {
                    mInterstitialAd.show();
                }
                new SongsDetailsAsync(true).execute();
            }
        }, delay);
    }

    private void shareToAll() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Radio Jul");
        share.putExtra(Intent.EXTRA_TEXT, "https://mobile.twitter.com/radio_jul");
        startActivity(Intent.createChooser(share, "Share via!"));
    }

    class SongsDetailsAsync extends AsyncTask<String, Void, String> {

        boolean loadAdd;

        public SongsDetailsAsync(boolean loadAdd) {
            this.loadAdd = loadAdd;
        }

        private NodeList nodelist;
        private NodeList nodelistForTracks;

        @Override
        protected String doInBackground(String... Url) {
            try {
                URL url = new URL(Constants.SONGS_DETAILS);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                nodelist = doc.getElementsByTagName("track");
                nodelistForTracks = doc.getElementsByTagName("tracks");
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            String radioPlayerUrl = "";

            if (null != nodelistForTracks) {

                for (int i = 0; i < nodelistForTracks.getLength(); i++) {
                    Node node = nodelistForTracks.item(i);
                    Element eElement = (Element) node;
                    radioPlayerUrl = getNode("radurl", eElement);
                }
            }
            if (null != nodelist) {
                for (int i = 0; i < nodelist.getLength(); i++) {
                    Node node = nodelist.item(i);
                    Element eElement = (Element) node;
                    String title, cover,artist = null;
                    title = getNode("title", eElement);
                    cover = getNode("cover", eElement);
                    artist = getNode("artists", eElement);


                    playduration = Long.parseLong(getNode("playduration", eElement));
                    Picasso.with(MainActivity.this)
                            .load(cover)
                            .placeholder(R.mipmap.ic_afro_beats)
                            .error(R.mipmap.ic_afro_beats)
                            .into(imvBanner);
                    tvChannelTitle.setText(title);
                    tvArtistName.setText(artist);
                }
                Log.d("postDelay", playduration + "");
                if (loadAdd) {
                    showInterstitialAd(playduration / 2);
                }
            }
            Constants.setRadioPlayerUrl(radioPlayerUrl);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!playerService.isRadioPlaying()) {
                        progressBar.setVisibility(View.VISIBLE);
                        playButton.setImageResource(R.mipmap.ic_pause_btn);
                        playerService.startRadio();
                    }
                }
            }, 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        if (mgr != null) {
//            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = new Intent();
        i.setAction("iconChangeOnOnDestroy");
        sendBroadcast(i);
        if (null != playerService) {
            playerService.getmRadioManager();
        }
//        if (playerService == null) {
//           playerService=new PlayerService();
//
//            if (playerService != null && playerService.isRadioPlaying()) {
//                playButton.setImageResource(R.mipmap.ic_pause_btn);
//            } else {
//                playButton.setImageResource(R.mipmap.ic_play_btn);
//            }
//        }else{
//            if (playerService.isRadioPlaying()) {
//                playButton.setImageResource(R.mipmap.ic_pause_btn);
//            } else {
//                playButton.setImageResource(R.mipmap.ic_play_btn);
//            }
//
//        }
    }

    /**
     * connect to the service
     */
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.MusicBinder binder = (PlayerService.MusicBinder) service;
            playerService = binder.getService();
            playerService.getmRadioManager();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("Service_checker", "Failed to Connected to service");
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
//            finish();
        }
    }


    // getNode function
    private static String getNode(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        if (mgr != null) {
//            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
//        }
    }

}
