package fcm;

/**
 * Created by sergio on 7/4/16.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.demo.testpushnotification.AppController;
import com.demo.testpushnotification.Constants;
import com.demo.testpushnotification.MainActivity;
import com.demo.testpushnotification.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
       Aca llegan los push notifications
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(AppController.getInstance().isAppIsInBackground())
            if(remoteMessage.getNotification()!=null)
                sendNotification(remoteMessage.getNotification().getBody());
            else{
                sendNotification(remoteMessage.getData().get("body"));
            }
        else{
            final String msg = remoteMessage.getNotification().getBody();
            sendBroadCast(this,msg);

            /*
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    Toast.makeText(AppController.getInstance().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            };
            handler.sendEmptyMessage(0);
            */
        }
    }


    /**
        Se envia el msg a la barra de notificaciones
     */
    private void sendNotification(String messageBody) {
        Log.e("APPXX","SEND NOTIF");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.RECEIVED_PUSH, true);
        intent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.viola2);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /**
     Se envia el msg al MainActivity
     */
    private void sendBroadCast(Context context, String msg) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.RECEIVED_PUSH);
        broadcastIntent.putExtra("body", msg);
        context.sendBroadcast(broadcastIntent);
    }
}