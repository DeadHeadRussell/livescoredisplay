package edu.cmu.mat.scores.events;

import java.awt.Point;

import edu.cmu.mat.scores.ScoreObject;

public class StartRepeatEvent extends Event {
	@Override
	public Type getType() {
		return Event.Type.START_REPEAT;
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
