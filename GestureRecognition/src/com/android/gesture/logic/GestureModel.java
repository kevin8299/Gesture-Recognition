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

package com.android.gesture.logic;

import java.util.Vector;

import com.android.gesture.event.AccelerationEvent;

import android.util.Log;

/** 
 * This Class units a Quantizer-Component and an Model-Component.
 * In this implementation a k-mean-algorithm for quantization and
 * a hidden markov model as instance for the model has been used.
 * 
 * @author Benjamin 'BePo' Poppinga
 */
public class GestureModel {
	
	private static final String TAG = "GestureModel";

	/** The number of states the hidden markov model consists of */
	private int states;
	
	/** The number of observations for the hmm and k-mean */
	private int observations;
	
	/** The id representation of this model */
	private int id;

	/** The quantization component */
	private Quantizer quantizer;
	
	/** The statistical model, hidden markov model */
	//: start -- added by Kevin
	public HMM markovmodell;
	//: end -- added by Kevin
	
	/** The default probability of this gesturemodel,
	 * needed for the bayes classifier */
	private double defaultprobability;

	
	/** Creates a Unit (Quantizer&Model).
	 * 
	 * @param id
	 *  int representation of a gesture "name"/class.
	 */
	public GestureModel(int id) {
		this.id=id;
		this.states=8; // n=8 states empirical value
		this.observations=14; // k=14 observations empirical value
		this.markovmodell = new HMM(states, observations); // init model
		this.quantizer = new Quantizer(states); // init quantizer
	}

	/**
	 * Trains the model to a set of motion-sequences, representing
	 * different evaluations of a gesture
	 * 
	 * @param trainsequence	a vector of gestures
	 */
	public void train(Vector<Gesture> trainsequence) {
		// summarize all vectors from the different gestures in one
		// gesture called sum.
		double maxacc=0;
		double minacc=0;
		Gesture sum = new Gesture();
		
		for(int i=0; i<trainsequence.size(); i++) {
			Vector<AccelerationEvent> t = trainsequence.elementAt(i).getData();

			// add the max and min acceleration, we later get the average
			maxacc+=trainsequence.elementAt(i).getMaxAcceleration();
			minacc+=trainsequence.elementAt(i).getMinAcceleration();
			
			// transfer every single accelerationevent of each gesture to
			// the new gesture sum
			for(int j=0; j<trainsequence.elementAt(i).getData().size(); j++) {
				sum.add(t.elementAt(j));
			}
			
		}
		
		// get the average and set it to the sum gesture
		sum.setMaxAcceleration(maxacc/trainsequence.size());
		sum.setMinAcceleration(minacc/trainsequence.size());
		
		// train the centeroids of the quantizer with this master gesture sum.
		this.quantizer.trainCenteroids(sum);
		
		// convert gesture vector to a sequence of discrete values
		Vector<int[]> seqs = new Vector<int[]>();
		for(int i=0; i<trainsequence.size(); i++) {
			seqs.add(this.quantizer.getObservationSequence(trainsequence.elementAt(i)));
		}
		
		// train the markov model with this derived discrete sequences
		this.markovmodell.train(seqs);
		
		// set the default probability for use with the bayes classifier
		this.setDefaultProbability(trainsequence);
	}

	/** 
	 * Returns the probability that a gesture matches to this
	 * gesture model.
	 * 
	 * @param gesture a gesture to test.
	 * @return probability that the gesture belongs to this gesture
	 * model.
	 */
	public double matches(Gesture gesture) {
		int[] sequence = quantizer.getObservationSequence(gesture);
		return this.markovmodell.getProbability(sequence);
	}

	/**
	 * For debug purposes or very technical interested people. :)
	 */
	public void printMap() {
		Log.i(TAG, "Gesture "+this.id+" Quantizer-Map:");
		this.quantizer.printMap();
	}

	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id=id;
	}
	
	/**
	 * Since the bayes classifier needs a model probability for
	 * each model this has to be set once after training. As model
	 * probability the average probability value has been choosen.
	 * 
	 * TODO: try lowest or highest model probability as alternative
	 * 
	 * @param defsequence the vector of training sequences.
	 */
	private void setDefaultProbability(Vector<Gesture> defsequence) {
		double prob=0;
		for(int i=0; i<defsequence.size(); i++) {
			prob+=this.matches(defsequence.elementAt(i));
		}
		
		this.defaultprobability=(prob)/defsequence.size();
	}
	
	/** 
	 * Returns the model probability for bayes.
	 * 
	 * @return
	 * 		the model probability
	 */
	public double getDefaultProbability() {
		return this.defaultprobability;
	}

}
