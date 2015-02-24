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

import java.util.EventListener;

/**
 * 
 * This is the GestureListener interface which has to be implemented
 * by any application which should receive recognized gestures.
 * 
 * @author Benjamin 'BePo' Poppinga
 * @author Maarten 'MrSnowflake' Krijn
 */
public interface GestureListener extends EventListener {

	/**
	 * This method would be called if a gesture has been recognized.
	 * 
	 * @param event The GestureEvent containing information about
	 * the recognized gesture.
	 */
	public abstract void gestureReceived(GestureEvent event);
	
	/**
	 * This method would be called if the gesture-recognizing system
	 * switched the state from recognition to training or otherwise.
	 * 
	 * @param event The StateEvent containing information about the
	 * state-change.
	 */
	public abstract void stateReceived(StateEvent event);
}
