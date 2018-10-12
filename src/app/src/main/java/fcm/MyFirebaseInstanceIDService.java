package fcm;

/**
 * Created by sergio on 7/4/16.
 */
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIDService";

    /**
     Se llama cada vez que el token se actualiza
     */

    @Override
    public void onTokenRefresh() {
        //Obtengo el token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendTokenToServer(refreshedToken);
    }


    //TODO: Enviar el token al third-party server
    private void sendTokenToServer(String token) {
        // Add custom implementation, as needed.
    }
}