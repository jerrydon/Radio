package com.frable.radio.jul;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import utilities.Constants;
import co.mobiwise.library.RadioListener;
import co.mobiwise.library.RadioManager;

public class PlayerService extends Service implements RadioListener {
    private RadioManager mRadioManager = RadioManager.with(this);
    private final IBinder binder = new MusicBinder();

    public PlayerService() {
    }

    Intent bReciver = new Intent();

    @Override
    public void onRadioConnected() {
        Log.d("radioCheck", "radioConnected");
        bReciver.setAction(Constants.TAG_BRECIVER);
        bReciver.putExtra(Constants.TAG_CONNECT_CHECK, Constants.CONNECTED);
        sendBroadcast(bReciver);
    }


    @Override
    public void onRadioStarted() {
        bReciver.setAction("radioPause/play");
        bReciver.putExtra(Constants.TAG_CONNECT_CHECK, Constants.STARTED);
        sendBroadcast(bReciver);
    }

    @Override
    public void onRadioStopped() {
        bReciver.setAction("radioPause/play");
        bReciver.putExtra(Constants.TAG_CONNECT_CHECK, Constants.STOPED);
        sendBroadcast(bReciver);
    }

    @Override
    public void onMetaDataReceived(String s, String s1) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Invoke it #onCreate
        mRadioManager.registerListener(this);
        //Enables notification or you can disable it giving "false" parameter
//        mRadioManager.enableNotification(true);
//        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        if (mgr != null) {
//            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mRadioManager.connect();
    }

    public void getmRadioManager() {
        Log.d("demoView", mRadioManager + "");
         mRadioManager.enableNotification(false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        if(mgr != null) {
//            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//        }
        return binder;
    }

    public class MusicBinder extends Binder {
        PlayerService getService() {
            Log.d("MyService", "MusicBinder callback called");
            return PlayerService.this;
        }
    }

    public void startRadio() {
        mRadioManager.startRadio(Constants.AFRO_BEATS_RADIO_PLAYER);
    }

    public void stopRadio() {
        mRadioManager.stopRadio();
    }

    public boolean isRadioPlaying() {

        return mRadioManager.isPlaying();
    }

    public RadioManager radio() {
        return RadioManager.with(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRadioManager.stopRadio();
        mRadioManager.disconnect();
    }
//    PhoneStateListener phoneStateListener1 = new PhoneStateListener() {
//        @Override
//        public void onCallStateChanged(int state, String incomingNumber) {
//            if (state == TelephonyManager.CALL_STATE_RINGING) {
//                //Incoming call: Pause music
//                if (isRadioPlaying()) {
//                    stopRadio();
//                }
//            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
//
//
//            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
//                if (isRadioPlaying()) {
//                    stopRadio();
//                }
//
//            }
//            super.onCallStateChanged(state, incomingNumber);
//        }
//    };
    @Override
    public void onTaskRemoved(Intent rootIntent) {
         stopSelf();
        super.onTaskRemoved(rootIntent);
    }
}
