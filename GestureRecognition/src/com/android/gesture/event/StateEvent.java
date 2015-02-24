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

package com.android.gesture.event;

import com.android.gesture.logic.AccelerationStreamAnalyzer;

/**
 * 
 * This is a StateEvent, telling the listeners in which state of recognition
 * the tool is:
 * 	1 = training,
 *  2 = recognition
 *  
 * @author Benjamin 'BePo' Poppinga
 * @author Maarten 'MrSnowflake' Krijn
 */

public class StateEvent {
	public static final int STATE_LEARNING = 1;
	public static final int STATE_RECOGNIZING = 2;
	
	int state;
	AccelerationStreamAnalyzer analyzer;
	
	/**
	 * Create a StateEvent.
	 * 
	 * @param source The source of which the state has changed.
	 * @param state The state the source has switched to.
	 */
	public StateEvent(AccelerationStreamAnalyzer source, int state) {
		this.analyzer=source;
		this.state=state;
	}
	
	public int getState() {
		return this.state;
	}
	
	public AccelerationStreamAnalyzer getSource() {
		return this.analyzer;
	}
	
}
