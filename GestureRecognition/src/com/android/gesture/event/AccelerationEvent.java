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

import com.android.gesture.device.*;

/**
 * This Event would be generated if an acceleration has been detected.
 * It contains information about the force applied to the device in each
 * direction (x, y, z). Further it contains the absolute value of this
 * vector and the source which generated this event (wiimote).
 * 
 * @author Benjamin 'BePo' Poppinga
 * @author Maarten 'MrSnowflake' Krijn
 */
public class AccelerationEvent extends EventObject {
	private static final long serialVersionUID = -1147369331907877956L;
	
	double X, Y, Z;
	double absvalue;
	Phone phone;
	
	/**
	 * Create a WiimoteAccelerationEvent with a specific source,
	 * all the three acceleration values and the calculated absolute
	 * value.
	 * 
	 * @param source The source which has been accelerated (Wiimote).
	 * @param X The value of acceleration in the x direction.
	 * @param Y The value of acceleration in the y direction.
	 * @param Z The value of acceleration in the z direction.
	 * @param absvalue The absolute value of this acceleration vector.
	 */
	public AccelerationEvent(Phone source, double X, double Y, double Z, double absvalue) {
		super(source);
		this.phone=source;
		this.X=X;
		this.Y=Y;
		this.Z=Z;
		this.absvalue=absvalue;
	}
	
	public Phone getSource() {
		return phone;
	}

	public double getX() {
		return X;
	}

	public double getY() {
		return Y;
	}

	public double getZ() {
		return Z;
	}
	
	public double getAbsValue() {
		return absvalue;
	}
}
