package utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.frable.radio.jul.PlayerService;

public class OutGoingCallReciver extends BroadcastReceiver {
    public OutGoingCallReciver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction ().equals("android.intent.action.PHONE_STATE")) {
            Log.d("callControl", "onIncomingCallEnded");
            PlayerService playerService=new PlayerService();
            if(playerService.isRadioPlaying()){
                playerService.stopRadio();
            }
        }
    }
}
