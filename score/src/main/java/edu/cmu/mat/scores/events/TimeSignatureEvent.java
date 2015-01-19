package edu.cmu.mat.scores.events;

import java.awt.Point;

import edu.cmu.mat.scores.ScoreObject;

public class TimeSignatureEvent extends Event {
	private int _numerator;
	private int _denomenator;

	public TimeSignatureEvent(int numerator, int denomenator) {
		_numerator = numerator;
		_denomenator = denomenator;
	}

	@Override
	public Type getType() {
		return Event.Type.TIME_SIGNATURE;
	}

	public void move(Point distance, ScoreObject intersect) {
		// TODO Auto-generated method stub

	}

	public void setActive(Point location) {
		// TODO Auto-generated method stub

	}

	public void setInactive() {
		// TODO Auto-generated method stub

	}

	public ScoreObject getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public void normalize() {
		// TODO Auto-generated method stub

	}

	public void deleteChild(ScoreObject child) {
		// TODO Auto-generated method stub

	}

	public void delete() {

	}

}
