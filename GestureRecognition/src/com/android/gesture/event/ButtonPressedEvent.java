/*
 * andgee - accelerometerbased gesture recognition
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

import android.util.Log;
import com.android.gesture.device.*;

/**
 * 
 * This Event would be generated if a button on a wiimote has been
 * pressed by user. It contains the source (wiimote) and an integer
 * representation of which button has been pressed. Please note that
 * there exist enumeration constants in the class, so you don't
 * have to use this integer values directly.
 *
 * @author Benjamin 'BePo' Poppinga
 * @author Maarten 'MrSnowflake' Krijn
 */
public class ButtonPressedEvent extends ActionStartEvent {
	private static final long serialVersionUID = 7337808287647891129L;

	private static final String TAG = "ButtonPressedEvent";
	
	int button;

	/**
	 * Create a WiimoteButtonPressedEvent with the Wiimote source whose
	 * Button has been pressed and the integer representation of the button.
	 * 
	 * @param source
	 * @param button
	 */
	public ButtonPressedEvent(Phone source, int button) {
		super(source);

		this.button=button;
		
		if(source.getRecognitionButton()==button) {
			this.recognitionbutton=true;
		} else if(source.getTrainButton()==button) {
			this.trainbutton=true;
		} else if(source.getCloseGestureButton()==button) {
			this.closegesturebutton=true;
		}
	}
	
	public int getButton() {
		return this.button;
	}
	
}
