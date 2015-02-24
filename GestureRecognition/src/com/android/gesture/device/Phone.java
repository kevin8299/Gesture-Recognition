/*
 * andgee - accelerometer based gesture recognition
 * Copyright (C) 2007, 2008 Benjamin Poppinga
 * Copyright (C) 2008 Maarten Krijn 
 * 
 * Developed at University of Oldenburg
 * Contact: benjamin.poppinga@informatik.uni-oldenburg.de
 *
 * This file is part of wiigee (v1.1).
 * 
 * This file got adapted to work with Android devices by
 * Maarten Krijn (mrsnowflake@gmail.com)
 *
 * andgee is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.android.gesture.device;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import com.android.gesture.activity.MainActivity;
import com.android.gesture.event.*;
import com.android.gesture.event.SensorListener;
import com.android.gesture.filter.DirectionalEquivalenceFilter;
import com.android.gesture.filter.Filter;
import com.android.gesture.filter.IdleStateFilter;
import com.android.gesture.filter.MotionDetectFilter;
import com.android.gesture.logic.AccelerationStreamAnalyzer;
import android.hardware.*;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

/**
 * @author Benjamin 'BePo' Poppinga
 * @author Maarten 'MrSnowflake' Krijn
 * 
 */
@SuppressLint("NewApi")
public class Phone implements SensorEventListener {
	private static final String TAG = "Phone";

	// : start -- added by Kevin
	private Handler mHandler;
	
	//This paths are predefined, which are used to debug HMM model for retrieving data.
	//Sorry for being awkward
	String filePath = "/mnt/sdcard/myData";
	//String filePath = Environment.getExternalStorageDirectory().getPath();
	String[] filePathsR = { 
			filePath + "/r1", 
			filePath + "/r2",
			filePath + "/r3", 
			filePath + "/r4",
			filePath + "/r5" };
	
	String[] filePathsL = { 
			filePath + "/l1",
			filePath + "/l2",
			filePath + "/l3", 
			filePath + "/l4",
			filePath + "/l5" };
	
	String[] filePathsU = { 
			filePath + "/u1", 
			filePath + "/u2",
			filePath + "/u3", 
			filePath + "/u4",
			filePath + "/u5" };
	
	String[] filePathsD = { 
			filePath + "/d1", 
			filePath + "/d2",
			filePath + "/d3",
			filePath + "/d4",
			filePath + "/d5" };

	/*
	 * String[] filePathsR={"r1","r2","r3","r4","r5"}; String[]
	 * filePathsL={"l1", "l2", "l3", "l4", "l5"}; String[] filePathsU={"u1",
	 * "u2", "u3", "u4", "u5"}; String[] filePathsD={"d1", "d2", "d3", "d4",
	 * "d5"};
	 */

	// : end -- added by Kevin

	public static final int MOTION = 0;

	// Filters, can filter the data stream
	Vector<Filter> filters = new Vector<Filter>();

	// Listeners, receive generated events
	Vector<SensorListener> wiimotelistener = new Vector<SensorListener>();
	AccelerationStreamAnalyzer analyzer = new AccelerationStreamAnalyzer();

	private boolean accelerationenabled;
	private SensorManager sensorMgr;

	private int recognitionbutton;
	private int trainbutton;
	private int closegesturebutton;

	// : start -- added by Kevin
	private int loadbutton;
	float[] accelerometerValue = { 0, 0, 0 }, magneticFieldValue = { 0, 0, 0 };
	List<Float> xList = new ArrayList<Float>();
	List<Float> yList = new ArrayList<Float>();
	List<Float> zList = new ArrayList<Float>();

	List<Float> xAllList = new ArrayList<Float>();
	List<Float> yAllList = new ArrayList<Float>();
	List<Float> zAllList = new ArrayList<Float>();

	double last = 0;
	boolean analyze = false;

	// : start -- added by Kevin

	public Phone(SensorManager sensorMgr) {
		// 'Calibrate'
		this.x0 = 0;
		this.y0 = -SensorManager.STANDARD_GRAVITY;
		this.z0 = 0;
		this.x1 = SensorManager.STANDARD_GRAVITY;
		this.y1 = 0;
		this.z1 = SensorManager.STANDARD_GRAVITY;

		this.sensorMgr = sensorMgr;
		this.enableAccelerationSensors();
		this.addFilter(new IdleStateFilter());
		this.addFilter(new MotionDetectFilter(this));
		this.addFilter(new DirectionalEquivalenceFilter());
		this.addWiimoteListener(this.analyzer);

		// if (sensorMgr.registerListener(this,
		// sensorMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)))

		// : start -- added by Kevin
		File file =new File(filePath);    
		if(!file.exists() && !file.isDirectory())      
		    file.mkdir();     
		
		if (sensorMgr.registerListener(this,
				sensorMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				SensorManager.SENSOR_DELAY_FASTEST))
			Log.e(TAG, "Could not register SensorListener");
			
		// : end -- added by Kevin

		
	}

	private void enableAccelerationSensors() {
		this.accelerationenabled = true;
	}

	/**
	 * Adds a Filter for processing the acceleration values.
	 * 
	 * @param filter
	 *            The Filter instance.
	 */
	public void addFilter(Filter filter) {
		this.filters.add(filter);
		Log.i(TAG, "Filter added...");
	}

	/**
	 * Resets all the filters, which are resetable. Sometimes they have to be
	 * resettet if a new gesture starts.
	 */
	public void resetFilters() {
		for (int i = 0; i < this.filters.size(); i++) {
			this.filters.elementAt(i).reset();
		}
	}

	public int getRecognitionButton() {
		return this.recognitionbutton;
	}

	public void setRecognitionButton(int b) {
		this.recognitionbutton = b;
	}

	public int getTrainButton() {
		return this.trainbutton;
	}

	public void setTrainButton(int b) {
		this.trainbutton = b;
	}

	public int getCloseGestureButton() {
		return this.closegesturebutton;
	}

	public void setCloseGestureButton(int b) {
		this.closegesturebutton = b;
	}

	// : start -- added by Kevin
	public void setLoadButton(int b) {
		this.loadbutton = b;
	}

	boolean flagStart = false;
	boolean flagEnd = false;

	float energyA(List<Float> in) {
		float o = 0f;
		int len = in.size();
		float sum = 0f;
		for (int i = 0; i < len; i++) {
			sum += Math.abs(in.get(i));
		}
		o = sum / len;
		return o;
	}

	float energyW(List<Float> in) {
		float o = 0f;
		int len = in.size();

		List<Float> a = new ArrayList<Float>();

		float sum = 0f;
		for (int i = 0; i < len - 1; i++) {
			a.add(in.get(i + 1) - in.get(i));
		}
		for (int i = 0; i < len - 1; i++) {
			sum += Math.abs(a.get(i));
		}
		o = sum / (len - 1);
		return o;
	}

	List<Float> Smooth(List<Float> in) {
		List<Float> out = new ArrayList<Float>();
		int n = in.size();
		float z0 = (69 * in.get(0) + 4 * in.get(1) - 6 * in.get(2) + 4 * in.get(3) - in.get(4)) / 70f;
		out.add(z0);
		
		float z1 = (2 * in.get(0) + 27 * in.get(1) + 12 * in.get(2) - 8 * in.get(3) + 2 * in.get(4)) / 35f;
		out.add(z1);
		
		for (int i = 2; i < n - 2; i++) {
			float zi = (-3 * in.get(i - 2) + 12 * in.get(i - 1) + 17 * in.get(i) + 12 * in.get(i + 1) + (-3) * in.get(i + 2)) / 35f;
			out.add(zi);
		}

		float zn1 = (2 * in.get(n - 5) + (-8) * in.get(n - 4) + 12 * in.get(n - 3) + 27 * in.get(n - 2) + 2 * in.get(n - 1)) / 35f;
		out.add(zn1);

		float zn = ((-1) * in.get(n - 5) + 4 * in.get(n - 4) + (-6) * in.get(n - 3) + 4 * in.get(n - 2) + 69 * in.get(n - 1)) / 70f;
		out.add(zn);

		return out;
	}

	ArrayList<ArrayList<Double>> readFile(String filePath) {
		ArrayList<ArrayList<Double>> out = new ArrayList<ArrayList<Double>>();
		int count = 0;
		BufferedReader in;
		ArrayList<Double> xList = new ArrayList<Double>();
		ArrayList<Double> yList = new ArrayList<Double>();
		ArrayList<Double> zList = new ArrayList<Double>();
		try {
			in = new BufferedReader(new FileReader(filePath), 4096);
			String str = "";
			while ((str = in.readLine()) != null) {
				String[] dataAll = str.split(" ");
				xList.add(Double.parseDouble(dataAll[0]));
				yList.add(Double.parseDouble(dataAll[1]));
				zList.add(Double.parseDouble(dataAll[2]));
			}
			out.add(xList);
			out.add(yList);
			out.add(zList);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}

	public void trains(String[] filePaths) {
		for (String filePath : filePaths) {
			ArrayList<ArrayList<Double>> d = readFile(filePath);
			int sz = d.get(0).size();
			for (int i = 0; i < sz; i++) {
				this.fireAccelerationEvent(new double[] { d.get(0).get(i),
						d.get(1).get(i), d.get(2).get(i) });
			}
			this.analyzer.formDataK();
		}
		this.analyzer.trainK();
	}

	// : end -- added by Kevin

	/**
	 * Adds an WiimoteListener to the wiimote. Everytime an action on the
	 * wiimote is performed the WiimoteListener would receive an event of this
	 * action.
	 * 
	 */
	public void addWiimoteListener(SensorListener listener) {
		this.wiimotelistener.add(listener);
		Log.i(TAG, "WiimoteListener added...");
	}

	/**
	 * Adds a GestureListener to the wiimote. Everytime a gesture is performed
	 * the GestureListener would receive an event of this gesture.
	 */
	public void addGestureListener(GestureListener listener) {
		this.analyzer.addGestureListener(listener);
		Log.i(TAG, "GestureListener added...");
	}

	public AccelerationStreamAnalyzer getAccelerationStreamAnalyzer() {
		return this.analyzer;
	}

	public boolean accelerationEnabled() {
		return this.accelerationenabled;
	}

	/**
	 * Fires an acceleration event.
	 * 
	 * @param x
	 *            Acceleration in x direction
	 * @param y
	 *            Acceleration in y direction
	 * @param z
	 *            Acceleration in z direction
	 */
	public void fireAccelerationEvent(double[] vector) {
		for (int i = 0; i < this.filters.size(); i++) {
			vector = this.filters.get(i).filter(vector);
			// cannot return here if null, because of time-dependent filters
		}

		// don't need to create an event if filtered away
		if (vector != null) {
			// calculate the absolute value for the accelerationevent
			double absvalue = Math.sqrt((vector[0] * vector[0])
					+ (vector[1] * vector[1]) + (vector[2] * vector[2]));

			AccelerationEvent w = new AccelerationEvent(this, vector[0],
					vector[1], vector[2], absvalue);
			for (int i = 0; i < this.wiimotelistener.size(); i++) {
				this.wiimotelistener.get(i).accelerationReceivedK(w);
			}
		}

	} // fireaccelerationevent

	/**
	 * Fires a motion start event.
	 */
	public void fireMotionStartEvent() {
		MotionStartEvent w = new MotionStartEvent(this);
		for (int i = 0; i < this.wiimotelistener.size(); i++) {
			this.wiimotelistener.get(i).motionStartReceived(w);
		}
	}

	/**
	 * Fires a motion stop event.
	 */
	public void fireMotionStopEvent() {
		MotionStopEvent w = new MotionStopEvent(this);
		for (int i = 0; i < this.wiimotelistener.size(); i++) {
			this.wiimotelistener.get(i).motionStopReceived(w);
		}
	}

	public void onAccuracyChanged(int arg0, int arg1) {
	}

	public void onSensorChangedOri(int sensor, float[] values) {
		double x, y, z;
		float xraw, yraw, zraw;

		// if the wiimote is sending acceleration data...
		if (this.accelerationEnabled()
				&& (sensor & SensorManager.SENSOR_ACCELEROMETER) != 0) {
			/*
			 * calculation of acceleration vectors starts here. further
			 * information about normation exist in the public papers or the
			 * various www-sources.
			 */
			xraw = values[SensorManager.DATA_X];
			yraw = values[SensorManager.DATA_Y];
			zraw = values[SensorManager.DATA_Z];

			x = (double) (xraw - x0) / (double) (x1 - x0);
			y = (double) (yraw - y0) / (double) (y1 - y0);
			z = (double) (zraw - z0) / (double) (z1 - z0);

			// try to fire event, there could be filters added to the
			// wiimote class which may prevents from firing.
			this.fireAccelerationEvent(new double[] { x, y, z });
		}
	}

	// : start -- added by Kevin
	public void onKeyDownOri(KeyEvent event) {
		if (!(this.lastevent instanceof ButtonPressedEvent)) {
			this.fireButtonPressedEvent(event.getKeyCode());
			this.lastevent = new ButtonPressedEvent(this, event.getKeyCode());
		}
	}

	boolean ff = false;

	public void onKeyDown(KeyEvent event) {
		switch (event.getKeyCode()) {
		
		case KeyEvent.KEYCODE_SPACE: // StartKey
			ff = true;
			this.sendMessage("Gesture is recording");
			break;

		case KeyEvent.KEYCODE_ENTER: // StopKey
			this.sendMessage("Recording is cancelling");
			ff = false;
			break;
			
		case KeyEvent.KEYCODE_T: // LearnKey
			ff = false;
			this.sendMessage("Model Training is beginning");
			trains(filePathsR);
			trains(filePathsL);
			trains(filePathsU);
			trains(filePathsD);
			this.sendMessage("Model Training is over");
			break;
			
		case KeyEvent.KEYCODE_BACK: // LoadKey
			this.sendMessage("Loading hmm is running");
			ff = false;
			this.sendMessage("Loading hmm is over");
			break;
		default:
			this.sendMessage("Nothing is entered");
		}
	}

	// : end -- added by Kevin

	public void onKeyUp(KeyEvent event) {
		if (!(this.lastevent instanceof ButtonReleasedEvent)) {
			this.fireButtonReleasedEvent();
			this.lastevent = new ButtonReleasedEvent(this);
		}
	}

	/**
	 * Fires a button pressed event.
	 * 
	 * @param button
	 *            Integer value of the pressed button.
	 */
	public void fireButtonPressedEvent(int button) {
		ButtonPressedEvent w = new ButtonPressedEvent(this, button);
		for (int i = 0; i < this.wiimotelistener.size(); i++) {
			this.wiimotelistener.get(i).buttonPressReceived(w);
		}

		if (w.isRecognitionInitEvent() || w.isTrainInitEvent()) {
			this.resetFilters();
		}
	}

	/**
	 * Fires a button released event.
	 */
	public void fireButtonReleasedEvent() {
		ButtonReleasedEvent w = new ButtonReleasedEvent(this);
		for (int i = 0; i < this.wiimotelistener.size(); i++) {
			this.wiimotelistener.get(i).buttonReleaseReceived(w);
		}
	}

	private double x0, x1, y0, y1, z0, z1;
	private EventObject lastevent;

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	// : start -- added by Kevin
	int ii = 0;

	@Override
	public void onSensorChanged(SensorEvent se) {
		// TODO Auto-generated method stub
		float[] data = se.values;
		switch (se.sensor.getType()) {
		case Sensor.TYPE_LINEAR_ACCELERATION:
			if (true) {// analyzer.trainingFinished
				xList.add(data[0]);
				yList.add(data[1]);
				zList.add(data[2]);
				// Log.i(TAG,"=====");
				if (xList.size() == 10) {
					// List<Float> xSmoothed = Smooth(xList);
					// List<Float> ySmoothed = Smooth(yList);
					// List<Float> zSmoothed = Smooth(zList);
					float xen = energyA(xList);
					float yen = energyA(yList);
					float zen = energyA(zList);
					if (xen + yen + zen > 8) {
						Log.i(TAG, "trigger is detected  " + xen + " " +yen + " " +zen );
						flagStart = true;
						for (int j = 0; j < 10; j++) {
							xAllList.add(xList.get(j));
							yAllList.add(yList.get(j));
							zAllList.add(zList.get(j));
						}
					} else {
						if (flagStart) {
							flagEnd = true;
						}
					}
					if (flagEnd) {// &&xAllList.size()>=20
						if (ff) {
							Log.i(TAG, "Data is recording ii:" + ii);
							String s = "";
							if (ii > 19)
								return;

							if (ii < 5)
								s = filePathsR[ii];

							else {
								if (ii < 10)
									s = filePathsL[ii - 5];
								else {
									if (ii < 15)
										s = filePathsU[ii - 10];
									else
										s = filePathsD[ii - 15];
								}
							}
							ii++;
							try {
								Log.i(TAG, "Data is recording DataLength:" + xAllList.size());
								// File ext =
								// android.os.Environment.getExternalStorageDirectory();
								FileOutputStream outStream = new FileOutputStream(new File(s));

								for (int i = 0; i < xAllList.size(); i++) {
									String sInput = xAllList.get(i) + " "
											+ yAllList.get(i) + " "
											+ zAllList.get(i) + "\n";
									byte[] b = sInput.getBytes();
									outStream.write(b);
								}
								outStream.close();
								this.sendMessage("Gesture is written to :" + s);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							xAllList.clear();
							yAllList.clear();
							zAllList.clear();
							flagStart = false;
							flagEnd = false;
						} else {
							this.analyze = true;
							for (int i = 0; i < xAllList.size(); i++) {
								this.fireAccelerationEvent(new double[] {
										xAllList.get(i), yAllList.get(i),
										zAllList.get(i) });
							}
							this.analyzer.analyzeK();
							this.analyze = false;
							xAllList.clear();
							yAllList.clear();
							zAllList.clear();
							flagStart = false;
							flagEnd = false;
						}
					}
					xList.clear();
					yList.clear();
					zList.clear();
				}
			} else {
				this.fireAccelerationEvent(new double[] { data[0], data[1],
						data[2] });
			}
			break;
		}
	}

	public void sendMessage(String msg) {
		if (mHandler != null) {
			Message message = new Message();
			Message.obtain();
			message.obj = msg;
			message.what = 1;
			mHandler.sendMessage(message);
		}
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}
	// : end -- added by Kevin
}
