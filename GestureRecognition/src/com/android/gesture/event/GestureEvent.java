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
 * This event would be generated if a gesture has been detected.
 * It contains information about the gesture "name" or type,
 * the accelerationstreamanalyzer which generated the event (source)
 * and the probability calculated from the bayes classifier.
 *
 * @author Benjamin 'BePo' Poppinga
 */
public class GestureEvent {

	int id;
	double probability;
	AccelerationStreamAnalyzer analyzer;
	
	/** Create a GestureEvent
	 * 
	 * @param source The Source, which detected the gesture.
	 * @param id A gesture ID for identifying a gesture. 
	 * @param probability The Bayes-Classifier calculated probability.
	 */
	public GestureEvent(AccelerationStreamAnalyzer source, int id, double probability) {
		this.analyzer=source;
		this.id=id;
		this.probability=probability;
	}
	
	public int getId() {
		return this.id;
	}
	
	public double getProbability() {
		return this.probability;
	}
	
	public AccelerationStreamAnalyzer getSource() {
		return this.analyzer;
	}
}
