package edu.cmu.mat.scores.events;

import java.awt.Point;

import edu.cmu.mat.scores.Barline;
import edu.cmu.mat.scores.ScoreObject;
import edu.cmu.mat.scores.Section;

public class RepeatEndEvent extends Event {
	private Barline _parent;
	private Section _section;
	private RepeatStartEvent _start;

	public RepeatEndEvent(RepeatStartEvent start) {
		_start = start;
	}

	@Override
	public Type getType() {
		return Event.Type.REPEAT_END;
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
		// TODO Auto-generated method stub

	}

	public int getState() {
		return 0;
	}

	public RepeatStartEvent getStart() {
		return _start;
	}

}
