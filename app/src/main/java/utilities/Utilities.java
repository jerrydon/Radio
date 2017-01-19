package utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.frable.radio.jul.MainActivity;
import com.frable.radio.jul.R;

/**
 * Created by Jayaraj on 9/22/16.
 */
public class Utilities {

    public static Bitmap convertingDrawableToBitmap(Context context){

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
         return  largeIcon;
    }


    /**
     * Check for internet connection
     *
     * @return true if available else false
     */
    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }
    public static void showToast(Context context,String message){

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }
}
