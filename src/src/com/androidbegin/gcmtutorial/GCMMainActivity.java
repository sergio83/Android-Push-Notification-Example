package com.androidbegin.gcmtutorial;

import static com.androidbegin.gcmtutorial.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.androidbegin.gcmtutorial.CommonUtilities.EXTRA_MESSAGE;
import static com.androidbegin.gcmtutorial.CommonUtilities.SENDER_ID;
import static com.androidbegin.gcmtutorial.CommonUtilities.SERVER_URL;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;

public class GCMMainActivity extends Activity {

	   TextView mDisplay;
	    AsyncTask<Void, Void, Void> mRegisterTask;

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        mDisplay = (TextView) findViewById(R.id.display);
	        registerReceiver(mHandleMessageReceiver,new IntentFilter(DISPLAY_MESSAGE_ACTION));
	        
	        checkNotNull(SERVER_URL, "SERVER_URL");
	        checkNotNull(SENDER_ID, "SENDER_ID");
	        GCMRegistrar.checkDevice(this);
	        GCMRegistrar.checkManifest(this);
	        
	        final String regId = "";//GCMRegistrar.getRegistrationId(this);
	        
	        if (regId.equals("")) {
	            // ME registro en GCM y obtengo el token
	            GCMRegistrar.register(this, SENDER_ID);
	        } else {
	            // Device is already registered on GCM, check server.
	            if (GCMRegistrar.isRegisteredOnServer(this)) {
	                // Skips registration.
	                mDisplay.append(getString(R.string.already_registered) + "\n");
	            } else {
	                // Try to register again, but not in the UI thread.
	                // It's also necessary to cancel the thread onDestroy(),
	                // hence the use of AsyncTask instead of a raw thread.
	                final Context context = this;
	                mRegisterTask = new AsyncTask<Void, Void, Void>() {

	                    @Override
	                    protected Void doInBackground(Void... params) {
	                        ServerUtilities.register(context, regId);
	                        return null;
	                    }

	                    @Override
	                    protected void onPostExecute(Void result) {
	                        mRegisterTask = null;
	                    }

	                };
	                mRegisterTask.execute(null, null, null);
	            }
	        }
	    }

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.activity_gcmsample, menu);
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch(item.getItemId()) {
	            case R.id.options_clear:
	                mDisplay.setText(null);
	                return true;
	            case R.id.options_exit:
	                finish();
	                return true;
	            default:
	                return super.onOptionsItemSelected(item);
	        }
	    }

	    @Override
	    protected void onDestroy() {
	        if (mRegisterTask != null) {
	            mRegisterTask.cancel(true);
	        }
	        unregisterReceiver(mHandleMessageReceiver);
	        GCMRegistrar.onDestroy(this);
	        super.onDestroy();
	    }

	    private void checkNotNull(Object reference, String name) {
	        if (reference == null) {
	            throw new NullPointerException(
	                    getString(R.string.error_config, name));
	        }
	    }

	    private final BroadcastReceiver mHandleMessageReceiver =
	            new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
	            mDisplay.append(newMessage + "\n");
	        }
	    };
	    
}