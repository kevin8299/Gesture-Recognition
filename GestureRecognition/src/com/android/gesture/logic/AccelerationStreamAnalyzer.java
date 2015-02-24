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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import com.android.gesture.event.*;

/**
 * This class analyzes the WiimoteAccelerationEvents emitted from a Wiimote
 * and further creates and manages the different models for each type
 * of gesture. 
 * 
 * @author Benjamin 'BePo' Poppinga
 */

public class AccelerationStreamAnalyzer implements SensorListener {
	private static final String TAG = "Andgee.AccelerationStreamAnalyzer";

	// Listener
	Vector<GestureListener> listen = new Vector<GestureListener>();
	
	// gesture specific values
	private Gesture current; // current gesture
	private double maxacc;
	private double minacc;
	Vector<GestureModel> gesturemodel; // each gesturetype got its own 
									// gesturemodel in this vector
	
	Vector<Gesture> trainsequence;
	
	// State variables
	private boolean learning, analyzing;
	
	public AccelerationStreamAnalyzer() {
		this.maxacc=Double.MIN_VALUE;
		this.minacc=Double.MAX_VALUE;
		this.learning=false;
		this.analyzing=false;
		this.current=new Gesture();
		this.gesturemodel=new Vector<GestureModel>();
		this.trainsequence=new Vector<Gesture>();
	}

	
	//: start -- added by Kevin
	Gesture testCurrent;
	Vector<Gesture> oldtrainsequence;
	boolean train1Boolean=true;
	public boolean trainingFinished = false ;
	
	public void formDataK(){
		Gesture gesture =new Gesture(this.current);
		this.trainsequence.add(gesture);
		this.current=new Gesture();
	}
	
	public void trainK(){
		gesturemodel.add(new GestureModel(gesturemodel.size()));
		this.gesturemodel.lastElement().train(this.trainsequence);
		String s="";
		Log.i(TAG,"Model parameter output start ");
		for(int i=0;i<this.gesturemodel.lastElement().markovmodell.pi.length;i++){
			double d = this.gesturemodel.lastElement().markovmodell.pi[i];
			s+=d+ " ";
		}
		FileOutputStream outStream;
		try{
			outStream = new FileOutputStream("/mnt/sdcard/myData/pi.txt");
			byte[] b = s.getBytes();
			outStream.write(b);
			outStream.close();
		}catch(FileNotFoundException e){
				e.printStackTrace();
		}catch(IOException e){
				e.printStackTrace();
		}
		
		s="";
		try{
			outStream = new FileOutputStream("/mnt/sdcard/myData/a.txt");
			for(int i = 0;i<8;i++){
				for(int j = 0;j<8;j++){
					double d = this.gesturemodel.lastElement().markovmodell.a[i][j];
					s+=d+" ";
				}

				byte[] b = s.getBytes();
				outStream.write(b);
				b="\n".getBytes();
				outStream.write(b);
				s="";
			}	
			outStream.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}	
		
		
		s="";
		try{
			outStream = new FileOutputStream("/mnt/sdcard/myData/b.txt");
			for(int i = 0;i<8;i++){
				for(int j = 0;j<14;j++){
					double d = this.gesturemodel.lastElement().markovmodell.b[i][j];
					s+=d+" ";
				}
			
				byte[] b = s.getBytes();
				outStream.write(b);
				b="\n".getBytes();
				outStream.write(b);
				s="";
			}	
			outStream.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}	
		
		this.trainsequence = new Vector<Gesture>();
	}
	
	
	public void analyzeK(){
		if(this.current.getCountOfData()>0){
			Log.i(TAG,"Finished reading recognition");
			Gesture gesture = new Gesture(this.current);
			this.analyze(gesture);
			this.current=new Gesture();
		}
	}
	//: end -- added by Kevin
	
	
	/** 
	 * Since this class implements the WiimoteListener this procedure is
	 * necessary. It contains the filtering (directional equivalence filter)
	 * and adds the incoming data to the current motion, we want to train
	 * or recognize.
	 * 
	 * @param event The acceleration event which has to be processed by the
	 * directional equivalence filter and which has to be added to the current
	 * motion in recognition or training process.
	 */
	public void accelerationReceived(AccelerationEvent event) {
		if(this.learning || this.analyzing) {
			if(this.current.getCountOfData()>0) {
				this.current.add(event); // add event to gesture
			} else {
				// new gesture, nothing to do for directional equivalence filter
				this.maxacc=Double.MIN_VALUE;
				this.minacc=Double.MAX_VALUE;
				this.current.add(event);
			}
			
			// (re)calculate max acceleration			
			if(Math.abs(event.getX()) > this.maxacc) {
				this.maxacc=Math.abs(event.getX());
				this.current.setMaxAcceleration(this.maxacc);
			}
			if(Math.abs(event.getY()) > this.maxacc) {
				this.maxacc=Math.abs(event.getY());
				this.current.setMaxAcceleration(this.maxacc);
			}
			if(Math.abs(event.getZ()) > this.maxacc) {
				this.maxacc=Math.abs(event.getZ());
				this.current.setMaxAcceleration(this.maxacc);
			}
			
			// (re)calculate min acceleration
			if(Math.abs(event.getX()) < this.minacc) {
				this.minacc=Math.abs(event.getX());
				this.current.setMinAcceleration(this.minacc);
			}
			if(Math.abs(event.getY()) < this.minacc) {
				this.minacc=Math.abs(event.getY());
				this.current.setMinAcceleration(this.minacc);
			}
			if(Math.abs(event.getZ()) < this.minacc) {
				this.minacc=Math.abs(event.getZ());
				this.current.setMinAcceleration(this.minacc);
			}
		}
		
	}

	/** 
	 * This method is from the WiimoteListener interface. A button press
	 * is used to control the data flow inside the structures. 
	 * 
	 */
	public void buttonPressReceived(ButtonPressedEvent event) {
		this.handleStartEvent(event);
	}

	public void buttonReleaseReceived(ButtonReleasedEvent event) {
		this.handleStopEvent(event);
	}
	
	public void motionStartReceived(MotionStartEvent event) {
		this.handleStartEvent(event);
	}
	
	public void motionStopReceived(MotionStopEvent event) {
		this.handleStopEvent(event);
	}
	
	public void handleStartEvent(ActionStartEvent event) {
		
		// TrainButton = record a gesture for learning
		if((!this.analyzing && !this.learning) && 
			event.isTrainInitEvent()) {
			Log.i(TAG, "Training started!");
			this.learning=true;
			this.fireStateEvent(1);
		}
		
		// RecognitionButton = record a gesture for recognition
		if((!this.analyzing && !this.learning) && 
			event.isRecognitionInitEvent()) {
			Log.i(TAG, "Recognition started!");
			this.analyzing=true;
			this.fireStateEvent(2);
		}
			
		// CloseGestureButton = starts the training of the model with multiple
		// recognized gestures, contained in trainsequence
		if((!this.analyzing && !this.learning) && 
			event.isCloseGestureInitEvent()) {
		
			if(this.trainsequence.size()>0) {
				Log.i(TAG, "Training the model with "+this.trainsequence.size()+" gestures...");
				this.fireStateEvent(1);
				this.learning=true;
				
				gesturemodel.add(new GestureModel(gesturemodel.size()));
				this.gesturemodel.lastElement().train(this.trainsequence);
				
				//: start -- added by Kevin
				if(this.gesturemodel.size()==4)
					trainingFinished=true;
				this.oldtrainsequence=trainsequence;
				//: end -- added by Kevin
				
				// this.gesturemodel.lastElement().printMap(); // debug purpos.
				this.trainsequence=new Vector<Gesture>();
				
				this.learning=false;
			} else {
				Log.i(TAG, "There is nothing to do. Please record some gestures first.");
			}
		}
	}
	
	public void handleStopEvent(ActionStopEvent event) {
		if ((event instanceof ButtonReleasedEvent) && this.learning) { // button release and state=learning, stops learning
			if(this.current.getCountOfData()>0) {
				Log.i(TAG, "Finished recording (training)...");
				Log.i(TAG, "Data: "+this.current.getCountOfData());
				Gesture gesture = new Gesture(this.current);
				this.trainsequence.add(gesture);
				this.current=new Gesture();
				this.learning=false;
			} else {
				Log.i(TAG, "There is no data.");
				Log.i(TAG, "Please train the gesture again.");
				this.learning=false; // ?
			}
		}
		
		else if((event instanceof ButtonReleasedEvent) && this.analyzing) { // button release and state=analyzing, stops analyzing
			if(this.current.getCountOfData()>0) {
				Log.i(TAG, "Finished recording (recognition)...");
				Log.i(TAG, "Compare gesture with "+this.gesturemodel.size()+" other gestures.");
				Gesture gesture = new Gesture(this.current);
				this.analyze(gesture);
				this.testCurrent=this.current;
				this.current=new Gesture();
				this.analyzing=false;
			} else {
				Log.i(TAG, "There is no data.");
				Log.i(TAG, "Please recognize the gesture again.");
				this.analyzing=false; // ?
			}
		}
	}
	
	/** 
	 * This method recognize a specific gesture, given to the procedure.
	 * For classification a bayes classification algorithm is used.
	 * 
	 * @param g	gesture to recognize
	 */
	public void analyze(Gesture g) {
		Log.i(TAG, "Recognizing gesture...");
		
		// Wert im Nenner berechnen, nach Bayes
		double sum = 0;
		for(int i=0; i<this.gesturemodel.size(); i++) {
			sum+=this.gesturemodel.elementAt(i).getDefaultProbability()*
					this.gesturemodel.elementAt(i).matches(g);
		}
		
		int recognized = -1; // which gesture has been recognized
		double recogprob = Integer.MIN_VALUE; // probability of this gesture
		double probgesture = 0; // temporal value for bayes algorithm
		double probmodel = 0; // temporal value for bayes algorithm
		for(int i=0; i<this.gesturemodel.size(); i++) {
			double tmpgesture = this.gesturemodel.elementAt(i).matches(g);
			double tmpmodel = this.gesturemodel.elementAt(i).getDefaultProbability();
			
			if(((tmpmodel*tmpgesture)/sum)>recogprob) {
				probgesture=tmpgesture;
				probmodel=tmpmodel;
				recogprob=((tmpmodel*tmpgesture)/sum);
				recognized=i;
			}
		}
		
		// a gesture could be recognized
		if(recogprob>0 && probmodel>0 && probgesture>0 && sum>0) {
			this.fireGestureEvent(recognized, recogprob);
			Log.i(TAG, "######");
			Log.i(TAG, "Gesture No. "+recognized+" recognized: "+recogprob);
			Log.i(TAG, "######");
		} else {
			// no gesture could be recognized
			this.fireStateEvent(0);
			Log.i(TAG, "######");
			Log.i(TAG, "No gesture recognized.");
			Log.i(TAG, "######");
		}
		
	}
	
	/**
	 * Resets the complete gesturemodel. After reset no gesture is known
	 * to the system.
	 */
	public void reset() {
		if(this.gesturemodel.size()>0) {
			this.gesturemodel.clear();
			Log.i(TAG, "### Model reset ###");
		} else {
			Log.i(TAG, "There doesn't exist any data to reset.");
		}
	}
	
	/** 
	 * Add an GestureListener to receive Gesture/StateEvents.
	 * 
	 * @param g
	 * 	Class which implements GestureListener interface
	 */
	public void addGestureListener(GestureListener g) {
		this.listen.add(g);
	}
	
	private void fireGestureEvent(int id, double probability) {
		GestureEvent w = new GestureEvent(this, id, probability);
		for(int i=0; i<this.listen.size(); i++) {
			this.listen.get(i).gestureReceived(w);
		}
	}
	
	private void fireStateEvent(int state) {
		StateEvent w = new StateEvent(this, state);
		for(int i=0; i<this.listen.size(); i++) {
			this.listen.get(i).stateReceived(w);
		}
	}

	// : start -- added by Kevin
	@Override
	public void accelerationReceivedK(AccelerationEvent event) {
		// TODO Auto-generated method stub
			if(true) {
				if(this.current.getCountOfData()>0) {
					this.current.add(event); // add event to gesture
				} else {
					// new gesture, nothing to do for directional equivalence filter
					this.maxacc=Double.MIN_VALUE;
					this.minacc=Double.MAX_VALUE;
					this.current.add(event);
				}
				
				// (re)calculate max acceleration			
				if(Math.abs(event.getX()) > this.maxacc) {
					this.maxacc=Math.abs(event.getX());
					this.current.setMaxAcceleration(this.maxacc);
				}
				if(Math.abs(event.getY()) > this.maxacc) {
					this.maxacc=Math.abs(event.getY());
					this.current.setMaxAcceleration(this.maxacc);
				}
				if(Math.abs(event.getZ()) > this.maxacc) {
					this.maxacc=Math.abs(event.getZ());
					this.current.setMaxAcceleration(this.maxacc);
				}
				
				// (re)calculate min acceleration
				if(Math.abs(event.getX()) < this.minacc) {
					this.minacc=Math.abs(event.getX());
					this.current.setMinAcceleration(this.minacc);
				}
				if(Math.abs(event.getY()) < this.minacc) {
					this.minacc=Math.abs(event.getY());
					this.current.setMinAcceleration(this.minacc);
				}
				if(Math.abs(event.getZ()) < this.minacc) {
					this.minacc=Math.abs(event.getZ());
					this.current.setMinAcceleration(this.minacc);
				}
			}
			
		}
	// : end -- added by Kevin


}
