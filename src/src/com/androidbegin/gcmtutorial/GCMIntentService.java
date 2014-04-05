package com.androidbegin.gcmtutorial;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.androidbegin.gcmtutorial.ServerUtilities;

import static com.androidbegin.gcmtutorial.CommonUtilities.SENDER_ID;
import static com.androidbegin.gcmtutorial.CommonUtilities.TAG;
import static com.androidbegin.gcmtutorial.CommonUtilities.displayMessage;



public class GCMIntentService extends GCMBaseIntentService {

	// Use your PROJECT ID from Google API into SENDER_ID

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {

		Log.i(TAG, "onRegistered: registrationId=" + registrationId);
        displayMessage(context, getString(R.string.gcm_registered,registrationId));
		ServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {

		Log.i(TAG, "onUnregistered: registrationId=" + registrationId);
        displayMessage(context, getString(R.string.gcm_unregistered));		
		ServerUtilities.unregister(context, registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent data) {
		String message;
		// Message from PHP server
		message = data.getStringExtra("message");
        displayMessage(context, message);
        Log.e(TAG, "MENSSAGE " + message);
        
		// Open a new activity called GCMMessageView
		Intent intent = new Intent(this, GCMMessageView.class);
		// Pass data to the new activity
		intent.putExtra("message", message);
		// Starts the activity on notification click
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// Create the notification with a notification builder
		Notification notification = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis())
				.setContentTitle("Android GCM Tutorial")
				.setContentText(message).setContentIntent(pIntent)
				.getNotification();
		// Remove the notification on click
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(R.string.app_name, notification);

		{
			// Wake Android Device when notification received
			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			final PowerManager.WakeLock mWakelock = pm.newWakeLock(
					PowerManager.FULL_WAKE_LOCK
							| PowerManager.ACQUIRE_CAUSES_WAKEUP, "GCM_PUSH");
			mWakelock.acquire();

			// Timer before putting Android Device to sleep mode.
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {
				public void run() {
					mWakelock.release();
				}
			};
			timer.schedule(task, 5000);
		}

	}

	@Override
	protected void onError(Context arg0, String errorId) {

		Log.e(TAG, "onError: errorId=" + errorId);
        displayMessage(arg0, getString(R.string.gcm_error, errorId));
	}
}