package com.android.gesture.activity;

//import org.openintents.hardware.SensorManagerSimulator;
//import org.openintents.provider.Hardware;

import com.android.gesture.R;
import com.android.gesture.control.Andgee;
import com.android.gesture.event.GestureEvent;
import com.android.gesture.event.GestureListener;
import com.android.gesture.event.StateEvent;
import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * @author Maarten 'MrSnowflake' Krijn
 */
public class MainActivity extends Activity implements OnTouchListener {
	protected static final String TAG = "AndgeeTest";
	private static final int LEARN_KEY = KeyEvent.KEYCODE_T;
	private static final int START_KEY = KeyEvent.KEYCODE_SPACE;
	private static final int STOP_KEY = KeyEvent.KEYCODE_ENTER;

	// : start -- added by Kevin
	private static final int LOAD_KEY = KeyEvent.KEYCODE_BACK;
	// : end -- added by Kevin

	public static final String EMULATOR_IMEI = "000000000000000";
	// : start -- added by Kevin
	private TextView showText, lblStatus;
	private Button bt_learn, bt_stop, bt_recognize, bt_load;

	private SensorManager sensorMgr;
	private Andgee andgee;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				showText.setText((String) msg.obj);
				break;
			case 2:
				showText.setText((String) msg.obj);
				break;
			}
		}
	};

	// : end -- added by Kevin

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// if (isEmulator(this)) {
		// Android sensor Manager
		sensorMgr = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);
		/*
		 * } else { // OpenIntents Sensor Emulator! // Before calling any of the
		 * Simulator data, // the Content resolver has to be set !!
		 * Hardware.mContentResolver = getContentResolver();
		 * 
		 * // Link sensor manager to OpenIntents Sensor simulator sensorMgr =
		 * (SensorManager) new SensorManagerSimulator((SensorManager)
		 * getSystemService(SENSOR_SERVICE));
		 * 
		 * SensorManagerSimulator.connectSimulator(); }
		 */

		// : start -- added by Kevin
		bt_recognize = (Button) findViewById(R.id.btn_start);
		bt_learn = (Button) findViewById(R.id.btn_learn);
		bt_stop = (Button) findViewById(R.id.btn_stop);
		bt_load = (Button) findViewById(R.id.btn_load);
		// : end -- added by Kevin

		// : start -- added by Kevin
		// buttonListener listener = new buttonListener();
		bt_recognize.setOnTouchListener(this);
		bt_learn.setOnTouchListener(this);
		bt_stop.setOnTouchListener(this);
		bt_load.setOnTouchListener(this);
		// : end -- added by Kevin
		lblStatus = (TextView) findViewById(R.id.status);

		// : start -- added by Kevin
		showText = (TextView) findViewById(R.id.textview_status);
		// : end -- added by Kevin

		andgee = Andgee.getInstance(sensorMgr);

		// : start -- added by Kevin
		andgee.setHandler(handler);
		// : end -- added by Kevin

		andgee.setRecognitionButton(START_KEY);
		andgee.setCloseGestureButton(STOP_KEY);
		andgee.setTrainButton(LEARN_KEY);

		// : start -- added by Kevin
		andgee.setLoadButton(LOAD_KEY);
		// : end -- added by Kevin

		andgee.addGestureListener(new GestureListener() {
			@Override
			public void gestureReceived(GestureEvent event) {
				Log.i(TAG, "GestureReceived " + event.getId());
				lblStatus.setText("Recognized: " + event.getId()
						+ " Probability: " + event.getProbability());
			}

			@Override
			public void stateReceived(StateEvent event) {
				switch (event.getState()) {
				case StateEvent.STATE_LEARNING:
					Log.i(TAG, "StateReceived learning");
					break;
				case StateEvent.STATE_RECOGNIZING:
					Log.i(TAG, "StateReceived Recognizing");
					break;
				default:

					// : start -- added by Kevin
					lblStatus.setText("This Gesture is Not Recognized!");
					// : end -- added by Kevin

					Log.i(TAG, "StateReceived Unknown " + event.getState());
				}
			}
		});

	}

	// : start -- added by Kevin
	// class buttonListener implements OnTouchListener {
	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// switch (v.getId()) {
	// case R.id.btn_learn:
	// if (event.getAction() == MotionEvent.ACTION_DOWN) {
	// andgee.onKeyDown(new KeyEvent(KeyEvent.ACTION_DOWN,
	// LEARN_KEY));
	// } else if (event.getAction() == MotionEvent.ACTION_UP) {
	// andgee.onKeyUp(new KeyEvent(KeyEvent.ACTION_UP, LEARN_KEY));
	// }
	// break;
	// case R.id.btn_start:
	// if (event.getAction() == MotionEvent.ACTION_DOWN) {
	// andgee.onKeyDown(new KeyEvent(KeyEvent.ACTION_DOWN,
	// START_KEY));
	// } else if (event.getAction() == MotionEvent.ACTION_UP) {
	// andgee.onKeyUp(new KeyEvent(KeyEvent.ACTION_UP, START_KEY));
	// }
	// break;
	// case R.id.btn_stop:
	// if (event.getAction() == MotionEvent.ACTION_DOWN) {
	// andgee.onKeyDown(new KeyEvent(KeyEvent.ACTION_DOWN,
	// STOP_KEY));
	// } else if (event.getAction() == MotionEvent.ACTION_UP) {
	// andgee.onKeyUp(new KeyEvent(KeyEvent.ACTION_UP, STOP_KEY));
	// }
	// break;
	// case R.id.btn_load:
	// if (event.getAction() == MotionEvent.ACTION_DOWN) {
	// andgee.onKeyDown(new KeyEvent(KeyEvent.ACTION_DOWN,
	// LOAD_KEY));
	// } else if (event.getAction() == MotionEvent.ACTION_UP) {
	// andgee.onKeyUp(new KeyEvent(KeyEvent.ACTION_UP, LOAD_KEY));
	// }
	// break;
	//
	// }
	// return false;
	// }
	// }

	// : end -- added by Kevin

	/**
	 * Returns whether the context is running on the android emulator.
	 * 
	 * @param ctx
	 *            The calling context.
	 * @return True: Running on emulator. False: Running on a real device
	 */
	public static boolean isEmulator(Context ctx) {
		TelephonyManager telephonyMgr = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		// to be deleted in final
		// always use simulator when in emulator
		return !telephonyMgr.getDeviceId().equals(EMULATOR_IMEI);
	}

	/**
	 * TODO Zorg voor juiste return waardes van Andgee.onKeyDown() en Up()
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// : start -- added by Kevin
		Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();

		if (keyCode == STOP_KEY)
			lblStatus.setText("Recording ...");
		else if (keyCode == LEARN_KEY)
			lblStatus.setText("Training ...");
		else if (keyCode == START_KEY)
			lblStatus.setText("Cancelling ...");
		else if (keyCode == LOAD_KEY)
			lblStatus.setText("Loading ...");

		// : end -- added by Kevin

		if (keyCode == STOP_KEY || keyCode == LEARN_KEY || keyCode == START_KEY) {
			andgee.onKeyDown(event);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * TODO Zorg voor juiste return waardes
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == STOP_KEY || keyCode == LEARN_KEY || keyCode == START_KEY) {
			andgee.onKeyUp(event);
			lblStatus.setText("");
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_learn:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				andgee.onKeyDown(new KeyEvent(KeyEvent.ACTION_DOWN, LEARN_KEY));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				andgee.onKeyUp(new KeyEvent(KeyEvent.ACTION_UP, LEARN_KEY));
			}
			break;
		case R.id.btn_start:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				andgee.onKeyDown(new KeyEvent(KeyEvent.ACTION_DOWN, START_KEY));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				andgee.onKeyUp(new KeyEvent(KeyEvent.ACTION_UP, START_KEY));
			}
			break;
		case R.id.btn_stop:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				andgee.onKeyDown(new KeyEvent(KeyEvent.ACTION_DOWN, STOP_KEY));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				andgee.onKeyUp(new KeyEvent(KeyEvent.ACTION_UP, STOP_KEY));
			}
			break;
		case R.id.btn_load:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				andgee.onKeyDown(new KeyEvent(KeyEvent.ACTION_DOWN, LOAD_KEY));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				andgee.onKeyUp(new KeyEvent(KeyEvent.ACTION_UP, LOAD_KEY));
			}
			break;

		}
		return false;
	}
}