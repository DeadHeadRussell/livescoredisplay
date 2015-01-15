package edu.cmu.mat.scores.events;

import java.awt.Point;

import edu.cmu.mat.scores.Barline;
import edu.cmu.mat.scores.ScoreObject;

public class SectionEndEvent extends Event {
	private Barline _parent;
	
	public SectionEndEvent(Barline parent) {
		_parent = parent;
	}
	
	@Override
	public Type getType() {
		return Event.Type.SECTION_END;
	}

	@Override
	public void move(Point distance, ScoreObject intersect) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteChild(ScoreObject child) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActive(Point location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInactive() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ScoreObject getParent() {
		return _parent;
	}

	@Override
	public void normalize() {
		// TODO Auto-generated method stub
		
	}
}
