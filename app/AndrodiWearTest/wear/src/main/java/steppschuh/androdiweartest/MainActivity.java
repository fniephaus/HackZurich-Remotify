package steppschuh.androdiweartest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private WearApp app;

	private ImageView actionButton;
	private TextView titleText;
	private TextView subTitleText;
	private ImageView remoteBackground;
	private ImageView remoteBackgroundBlur;
	private ImageView remoteIcon;
	private RelativeLayout actionButtonSmall;

	private GestureDetector mDetector;
	private static final int SWIPE_MIN_DISTANCE = 100;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;

	private static final int DEFAULT_ACTION = 0;
	private static final int SPECIAL_ACTION = 1;

	private int currentRemote = 0;
	private int currentAction = DEFAULT_ACTION;

	private ArrayList<Remote> remotes;

	Node node; // the connected device to send the message to
	GoogleApiClient mGoogleApiClient;
	private static final int SPEECH_REQUEST_CODE = 0;
	private boolean mResolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		initialize();
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
	protected void onActivityResult(int requestCode, int resultCode,
									Intent data) {
		if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
			List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0);
			Log.d(WearApp.TAG, "Spoken text: " + spokenText);

			if (spokenText.contains("next")) {
				setRemote(1);
				currentAction = DEFAULT_ACTION;
			} else if (spokenText.contains("previous") || spokenText.contains("last")) {
				setRemote(1);
				currentAction = SPECIAL_ACTION;
			} else if (spokenText.contains("light") || spokenText.contains("lights")) {
				setRemote(0);
				currentAction = DEFAULT_ACTION;
			}

			sendAction();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initialize() {
		app = (WearApp) getApplication();
		app.setContextActivity(this);

		remotes = new ArrayList<Remote>();

		Remote arduinoRemote = new Remote();
		arduinoRemote.setId(0);
		arduinoRemote.setBackground(getResources().getDrawable(R.drawable.back_arduino));
		arduinoRemote.setBackgroundBlur(getResources().getDrawable(R.drawable.back_arduino_blur));
		arduinoRemote.setIcon(getResources().getDrawable(R.drawable.icon_arduino));
		arduinoRemote.setTitle("Arduino");
		remotes.add(arduinoRemote);

		Remote pcRemote = new Remote();
		pcRemote.setId(1);
		pcRemote.setBackground(getResources().getDrawable(R.drawable.back_music));
		pcRemote.setBackgroundBlur(getResources().getDrawable(R.drawable.back_music_blur));
		pcRemote.setIcon(getResources().getDrawable(R.drawable.navigation_next_item));
		pcRemote.setTitle("Media");
		remotes.add(pcRemote);

		Remote carRemote = new Remote();
		carRemote.setId(2);
		carRemote.setBackground(getResources().getDrawable(R.drawable.back_car));
		carRemote.setBackgroundBlur(getResources().getDrawable(R.drawable.back_car_blur));
		carRemote.setIcon(getResources().getDrawable(R.drawable.icon_car));
		carRemote.setTitle("Car");
		remotes.add(carRemote);

		setupUi();

		connectGoogleService();

		(new Thread() {
			@Override
			public void run() {

			}
		}).run();
	}

	private void connectGoogleService() {
		Log.d(WearApp.TAG, "Connecting to Google API Service");
		mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addApi(Wearable.API)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.build();

		mGoogleApiClient.connect();
	}

	private void setupUi() {
		final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(WatchViewStub stub) {
				actionButton = (ImageView) stub.findViewById(R.id.content_background);
				actionButtonSmall = (RelativeLayout) stub.findViewById(R.id.container_content_small);
				actionButtonSmall.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						currentAction = SPECIAL_ACTION;
						sendAction();
						UiHelper.pressAnimation(actionButtonSmall);
					}
				});
				remoteBackground = (ImageView) stub.findViewById(R.id.image_background);
				remoteBackgroundBlur = (ImageView) stub.findViewById(R.id.image_background_blur);
				remoteIcon = (ImageView) stub.findViewById(R.id.content_icon);
				titleText = (TextView) stub.findViewById(R.id.box_title);
				subTitleText = (TextView) stub.findViewById(R.id.box_subtitle);

				initialize();
			}
		});

		// Configure a gesture detector
		mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			public void onLongPress(MotionEvent ev) {
				Log.d(WearApp.TAG, "onLongPress");
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				Log.d(WearApp.TAG, "onFling " + velocityX + "|" + velocityY);

				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					onRightToLeft();
					return true;
				}
				else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					onLeftToRight();
					return true;
				}

				if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					onBottomToTop();
					return true;
				}
				else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					onTopToBottom();
					return true;
				}
				return false;

			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				Log.d(WearApp.TAG, "onDoubleTap");
				requestVioceCommand();
				return super.onDoubleTap(e);
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				Log.d(WearApp.TAG, "onSingleTapConfirmed");
				if (currentRemote != 3) {
					currentAction = DEFAULT_ACTION;
					sendAction();
				}
				return super.onSingleTapConfirmed(e);
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				UiHelper.pressAnimation(actionButton);
				UiHelper.pressAnimation(remoteIcon);
				return super.onSingleTapUp(e);
			}
		});

	}

	private void requestVioceCommand() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		// Start the activity, the intent will be populated with the speech text
		startActivityForResult(intent, SPEECH_REQUEST_CODE);
	}

	private void setRemote(int index) {
		try {
			final Remote newRemote = remotes.get(index);

			Log.d(WearApp.TAG, "Remote set to " + newRemote.getTitle());

			titleText.setText("Controlling " + newRemote.getTitle());

			Handler showNewUiHandler = new Handler();
			Runnable showNewUiRunnable = new Runnable() {
				@Override
				public void run() {
					remoteBackground.setImageDrawable(newRemote.getBackground());
					UiHelper.fadeIn(remoteBackground, UiHelper.DEFAULT_DURATION * 4);

					remoteIcon.setImageDrawable(newRemote.getIcon());
					UiHelper.fadeIn(remoteIcon, UiHelper.DEFAULT_DURATION);
					UiHelper.fadeInRotating(remoteIcon, UiHelper.DEFAULT_DURATION);
				}
			};
			UiHelper.fadeOut(remoteBackground, UiHelper.DEFAULT_DURATION);
			UiHelper.fadeOutRotating(remoteIcon, UiHelper.DEFAULT_DURATION);

			showNewUiHandler.postDelayed(showNewUiRunnable, UiHelper.DEFAULT_DURATION + 50);

			Runnable prepareNextAnimationRunnable = new Runnable() {
				@Override
				public void run() {
					remoteBackgroundBlur.setImageDrawable(newRemote.getBackgroundBlur());
				}
			};
			showNewUiHandler.postDelayed(prepareNextAnimationRunnable, (UiHelper.DEFAULT_DURATION * 4) + 100);

			if (index == 1) {
				UiHelper.fadeIn(actionButtonSmall, UiHelper.DEFAULT_DURATION);
			} else {
				UiHelper.fadeOut(actionButtonSmall, UiHelper.DEFAULT_DURATION);
			}

			currentRemote = index;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void sendAction() {
		Log.d(WearApp.TAG, "sendAction");

		if (mGoogleApiClient.isConnected() && node != null) {

			String message = "action";
			byte[] data = {(byte) currentRemote, (byte) currentAction};

			PendingResult pendingResult = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), message, data);

			pendingResult.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
				@Override
				public void onResult(MessageApi.SendMessageResult sendMessageResult) {
					Log.d(WearApp.TAG, "onResult " + sendMessageResult.getRequestId());
				}
			});
		} else {
			Log.e(WearApp.TAG, "Not connected");
		}
	}

	private void setConnectedNode() {
		PendingResult pendingResult = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
		pendingResult.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
			@Override
			public void onResult(final NodeApi.GetConnectedNodesResult result) {
				ArrayList<Node> results= new ArrayList<Node>();
				for (Node connectedNode : result.getNodes()) {
					results.add(connectedNode);
				}
				if (results.size() > 0) {
					Log.d(WearApp.TAG, "Connected node set");
					node = results.get(0);
					subTitleText.setText("Connected");
				} else {
					Log.d(WearApp.TAG, "No connected node available");
					node = null;
					subTitleText.setText("Disconnected");
				}
			}
		});
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(WearApp.TAG, "Connected to Google Api Service");
		setConnectedNode();
		setRemote(currentRemote);
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(WearApp.TAG, "onConnectionSuspended: " + i);
		subTitleText.setText("Suspended");
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d(WearApp.TAG, "onConnectionFailed: " + connectionResult.toString());
		try {
			subTitleText.setText("Disconnected");
		} catch (Exception ex) {

		}
	}

	@Override
	public void onMessageReceived(MessageEvent messageEvent) {
		Log.d(WearApp.TAG, "onMessageReceived: " + messageEvent.getPath());

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		//Log.d(WearApp.TAG, "onTouchEvent");
		if (currentRemote == 2) {
			// Track onDown & onUp events
			switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					Log.d(WearApp.TAG, "onDown");
					currentAction = DEFAULT_ACTION;
					sendAction();
					break;
				}
				case MotionEvent.ACTION_UP: {
					Log.d(WearApp.TAG, "onUp");
					currentAction = SPECIAL_ACTION;
					sendAction();
					break;
				}
			}
		}

		return mDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
	}

	public void onRightToLeft() {
		Log.d(WearApp.TAG, "onRightToLeft");
	}

	public void onLeftToRight() {
		Log.d(WearApp.TAG, "onLeftToRight");
		finish();
	}

	public void onBottomToTop() {
		Log.d(WearApp.TAG, "onBottomToTop");
		if (currentRemote < 1) {
			currentRemote = remotes.size() - 1;
		} else {
			currentRemote -= 1;
		}
		setRemote(currentRemote);
	}

	public void onTopToBottom() {
		Log.d(WearApp.TAG, "onTopToBottom");
		if (currentRemote >= remotes.size() - 1) {
			currentRemote = 0;
		} else {
			currentRemote += 1;
		}
		setRemote(currentRemote);
	}
}
