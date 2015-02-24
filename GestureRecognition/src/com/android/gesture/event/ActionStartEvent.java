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

import java.util.EventObject;

import com.android.gesture.device.Phone;

/** 
 * @author Benjamin 'BePo' Poppinga
 * @author Maarten 'MrSnowflake' Krijn
 */
public class ActionStartEvent extends EventObject {
	private static final long serialVersionUID = -7591960263125063979L;

	protected Phone phone;
	protected boolean trainbutton;
	protected boolean recognitionbutton;
	protected boolean closegesturebutton;
	
	public ActionStartEvent(Phone source) {
		super(source);
		this.phone=source;
	}
	
	public Phone getSource() {
		return this.phone;
	}
	
	/**
	 * Is true if this button press has been done by the
	 * individual defined RecognitionButton which has to be
	 * set during initialization of a Wiimote.
	 * 
	 * @return Is this button press initiated by the recognition button.
	 * @see device.Wiimote#setRecognitionButton(int) setRecognitionButton()
	 */
	public boolean isRecognitionInitEvent() {
		return this.recognitionbutton;
	}
	
	/**
	 * Is true if this button press has been done by the
	 * individual defined TrainButton which has to be
	 * set during initialization of a Wiimote.
	 * 
	 * @return Is this button pres initiated by the training button.
	 * @see device.Wiimote#setTrainButton(int) setTrainButton()
	 */
	public boolean isTrainInitEvent() {
		return this.trainbutton;
	}
	
	/**
	 * Is true if this button press has been done by the
	 * individual defined CloseGestureButton which has to be
	 * set during initialization of a Wiimote.
	 * 
	 * @return Is this button press initiated by the close gesture button.
	 * @see device.Wiimote#setCloseGestureButton(int) setCloseGestureButton()
	 */
	public boolean isCloseGestureInitEvent() {
		return this.closegesturebutton;
	}

}
