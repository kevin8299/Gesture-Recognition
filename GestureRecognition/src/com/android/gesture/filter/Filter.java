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

/** 
 * @author Benjamin 'BePo' Poppinga
 */
public abstract class Filter {

	public Filter() {
		// nothing, but should be called via SUPER.
	}
	
	public void reset() {
		// reset filter, if necessary.
	}
	
	/***
	 * The actual called method to filter anything. It checks if the vector is
	 * already set to NULL by another filter and won't process it anymore. If it's
	 * not NULL it would be forwarded to the actual implemented method - filterAlgorithm(). 
	 * @param vector The acceleration vector, encoding: 0/x, 1/y, 2/z
	 * @return a new, filtered acceleration vector, encoded the same way
	 */
	public double[] filter(double[] vector) {
		if(vector==null) {
			return null;
		} else {
			return filterAlgorithm(vector);
		}
	}
	
	/***
	 * A filter receives a triple of acceleration values within the variable 'vector'.
	 * It's encoded as vector[0]=x, vector[1]=y, vector[2]=z. This is not an object since the
	 * processing of the filter should be really fast, since every acceleration of the wiimote
	 * passes the filter.
	 * @param vector
	 * @param absvalue
	 * @return
	 */
	abstract public double[] filterAlgorithm(double[] vector);
	
}
