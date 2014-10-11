package steppschuh.androdiweartest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private MobileApp app;

	private TextView statusText;

	GoogleApiClient mGoogleApiClient;
	private boolean mResolvingError = false;

	Handler callbackHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		app = (MobileApp) getApplication();
		app.setContextActivity(this);

		statusText = (TextView) findViewById(R.id.status);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Wearable.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		mGoogleApiClient.connect();

		callbackHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String result =  msg.getData().getString("result");
				Log.d(MobileApp.TAG, "Request result: " + result);
				statusText.setText(result);

				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(200);
			}
		};
    }

	@Override
	protected void onStart() {
		super.onStart();
		if (!mResolvingError) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onStop() {
		if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
		super.onStop();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(MobileApp.TAG, "Connected to Google Api Service");
		statusText.setText("Connected to Google Api Service");
		Wearable.MessageApi.addListener(mGoogleApiClient, this);
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(MobileApp.TAG, "onConnectionSuspended: " + i);
		statusText.setText("Connection suspended");
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d(MobileApp.TAG, "onConnectionFailed: " + connectionResult.toString());
		statusText.setText("Connection failed");
	}

	@Override
	public void onMessageReceived(MessageEvent messageEvent) {
		Log.d(MobileApp.TAG, "onMessageReceived: " + messageEvent.getPath());
		if (messageEvent.getPath().equals("action")) {
			int currentRemote = messageEvent.getData()[0];
			int currentAction = messageEvent.getData()[1];

			final String debugText = "Remote: " + currentRemote + " Action: " + currentAction;

			sendAction(currentRemote, currentAction);

			Log.d(MobileApp.TAG, debugText);

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					statusText.setText(debugText);
				}
			});
		}
	}

	private void sendAction(int remote, int action) {
		String url = "http://hackzurich.beta.scapp.io/";
		switch (remote) {
			case 0: {
				url += "arduino/toggle";
				break;
			}
			case 1: {
				if (action == 0) {
					url += "pc/next";
				} else {
					url += "pc/prev";
				}
				break;
			}
			case 2: {
				if (action == 0) {
					url += "car/fast";
				} else {
					url += "car/slow";
				}
				break;
			}
			default: {
				url += remote + "/" + action;
				break;
			}
		}
		getWebRequest(url, callbackHandler);
	}

	private static void getWebRequest(final String url, Handler handler) {
		final Handler mHandler = handler;
		(new Thread() {
			@Override
			public void run() {
				try {
					Log.d(MobileApp.TAG, "Requesting " + url);
					String result = getRequest(url);

					Bundle data = new Bundle();
					data.putString("result", result);
					Message message = new Message();
					message.setData(data);
					mHandler.sendMessage(message);
				} catch (Exception ex) {
					mHandler.sendEmptyMessage(0);
				}
			}
		}).start();
	}

	private static String getRequest(String myurl) throws IOException {
		InputStream is = null;
		String result = null;

		try {
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();

			result = inputStreamToString(is);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return result;
	}

	public static String inputStreamToString(InputStream stream) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			total.append(line);
		}
		return new String(total);
	}

}
