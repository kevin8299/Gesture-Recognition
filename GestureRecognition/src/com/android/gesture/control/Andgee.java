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

package com.android.gesture.control;

import com.android.gesture.device.Phone;
import com.android.gesture.event.GestureListener;
import com.android.gesture.filter.Filter;

import android.hardware.SensorManager;
import android.util.Log;
import android.view.KeyEvent;
import android.os.Handler;

/** 
 * Singleton
 * @author Benjamin 'BePo' Poppinga
 * @author Maarten 'MrSnowflake' Krijn
 */
public class Andgee {
	private static final String TAG = "Andgee";
	
	private static Andgee instance;
	private static String version = "1.2 alpha";
	private static String releasedate = "20081006";
	
	private SensorManager sensorMgr;
	private Phone phone;
	//private Vector<Wiimote> wiimotes;
	
	//: start -- added by Kevin
	private Handler mHandler;
	//: end -- added by Kevin
	
	private Andgee(SensorManager sensorManager) {
		sensorMgr = sensorManager;
		phone = new Phone(sensorMgr);
	}

	//: start -- added by Kevin
	public void setHandler(Handler mHandler){
		this.mHandler = mHandler;
		phone.setmHandler(mHandler);
	}
	//: end -- added by Kevin
	
	public static Andgee getInstance(SensorManager sensorManager) {
		Log.i(TAG, "This is Andgee version "+version+" ("+releasedate+")");
		Log.i(TAG, "This is an Android adaptation of Wiigee (http://wiigee.sourceforge.net/)");
		Log.i(TAG, "So many thanks to the Wiigee team for their awsome recognition lib!");
		if(instance == null) {
			instance = new Andgee(sensorManager);
			return instance;
		} else {
			return instance;
		}
	}
	
	public void addGestureListener(GestureListener listener) {
		phone.addGestureListener(listener);
	}
	
	public void addFilter(Filter filter) {
		phone.addFilter(filter);
	}
	
	public void onKeyDown(KeyEvent event) {
		phone.onKeyDown(event);
	}
	
	public void onKeyUp(KeyEvent event) {
		phone.onKeyUp(event);
	}
	
	/**
	 * Returns an array of discovered wiimotes.
	 * 
	 * @return Array of discovered wiimotes or null if
	 * none discoverd.
	 */
	public Phone getPhone() {
		return phone;
	}
		
	/**
	 * Sets the Trainbutton for all wiimotes;
	 * 
	 * @param b Button encoding, see static Wiimote values
	 */
	public void setTrainButton(int b) {
		phone.setTrainButton(b);
	}
	
	/**
	 * Sets the Recognitionbutton for all wiimotes;
	 * 
	 * @param b Button encoding, see static Wiimote values
	 */
	public void setRecognitionButton(int b) {
		phone.setRecognitionButton(b);
	}
	
	/**
	 * Sets the CloseGesturebutton for all wiimotes;
	 * 
	 * @param b Button encoding, see static Wiimote values
	 */
	public void setCloseGestureButton(int b) {
		phone.setCloseGestureButton(b);
	}
	
	//: start -- added by Kevin
	public void setLoadButton(int b) {
		phone.setLoadButton(b);
	}
	//: end -- added by Kevin
	
	}
