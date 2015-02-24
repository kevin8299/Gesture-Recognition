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
public class DirectionalEquivalenceFilter extends Filter {
	
	private double sensivity;
	private double[] reference;

	public DirectionalEquivalenceFilter() {
		super();
		this.reset();
	}
	
	public void reset() {
		this.sensivity=0.2;
		this.reference = new double[] {0.0, 0.0, 0.0};
	}
	
	public double[] filterAlgorithm(double[] vector) {
		if(vector[0]<reference[0]-this.sensivity ||
		   vector[0]>reference[0]+this.sensivity ||
		   vector[1]<reference[1]-this.sensivity ||
		   vector[1]>reference[1]+this.sensivity ||
		   vector[2]<reference[2]-this.sensivity ||
		   vector[2]>reference[2]+this.sensivity) {
			this.reference=vector;
			return vector;
		} else {
			return null;
		}
	}
	
	public void setSensivity(double sensivity) {
		this.sensivity=sensivity;
	}
	
	public double getSensivity() {
		return this.sensivity;
	}

}
