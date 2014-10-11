package steppschuh.androdiweartest;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

public class MobileApp extends Application {

	public static final String TAG = "MobileApp";

	private boolean isInitialized = false;
	private Activity contextActivity;

	public void initialize() {
		Log.d(TAG, "Initializing mobile app");
		isInitialized = true;
	}

	public Activity getContextActivity() {
		return contextActivity;
	}

	public void setContextActivity(Activity contextActivity) {
		this.contextActivity = contextActivity;
		if (!isInitialized) {
			initialize();
		}
	}
}
