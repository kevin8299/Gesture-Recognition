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

package com.android.gesture.filter;

import com.android.gesture.device.Phone;

/** 
 * @author Benjamin 'BePo' Poppinga
 */
public class MotionDetectFilter extends Filter {

	private int motionchangetime;
	private boolean nowinmotion;
	private long motionstartstamp;
	//private long lastmotionstamp;
	private Phone phone;
	
	/***
	 * Detects wheather the wiimote receives acceleration or not and
	 * raises an event, if the device starts or stops. This is actual a
	 * null filter, not manipulating anything. But looks pretty good in
	 * this datatype since it could be removed easily.
	 * 
	 * @param wiimote The Wiimote object which is controlled by the filter.
	 */
	public MotionDetectFilter(Phone phone) {
		super();
		this.phone=phone;
		this.reset();
	}
	
	public void reset() {
		super.reset();
		this.motionstartstamp=System.currentTimeMillis();
		this.nowinmotion=false;
		this.motionchangetime=190;
	}
	
	public double[] filter(double[] vector) {
		if(this.nowinmotion &&
				(System.currentTimeMillis()-this.motionstartstamp)>=
					this.motionchangetime) {
			this.nowinmotion=false;
			this.phone.fireMotionStopEvent();
		} // fi
		
		return filterAlgorithm(vector);
	}
	
	public double[] filterAlgorithm(double[] vector) {
		if(vector!=null) {
			this.motionstartstamp=System.currentTimeMillis();
			if(!this.nowinmotion) {
				this.nowinmotion=true;
				this.motionstartstamp=System.currentTimeMillis();
				this.phone.fireMotionStartEvent();
			}
		}
		
		return vector;
	}
	
	/**
	 * Defines the time the wiimote has to be in idle state before a new motion change
	 * event appears. The default value 500ms should work well, only change it if you are sure
	 * about what you're doing.
	 * @param time Time in ms
	 */
	public void setMotionChangeTime(int time) {
		this.motionchangetime=time;
	}
	
	public int getMotionChangeTime() {
		return this.motionchangetime;
	}

}
