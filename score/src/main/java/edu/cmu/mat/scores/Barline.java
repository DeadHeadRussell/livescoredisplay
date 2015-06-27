package edu.cmu.mat.scores;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;

import edu.cmu.mat.scores.events.Event;

public class Barline implements ScoreObject {
	private System _parent;
	private int _state = NOT_ACTIVE;
	@Expose
	private double _offset;
	@Expose
	private List<Event> _events;

	private int _currH, _origH, _origW;
	
	public static final int NOT_ACTIVE = 0;
	public static final int ACTIVE = 1;

	public Barline(System parent, int offset) {
		this(parent, offset, new LinkedList<Event>());
	}

	public Barline(System parent, int offset, List<Event> events) {
		_currH = parent.getParent().getParent().getCurrentHeight();
		_origH = parent.getParent().getParent().getOriginalHeight();
		_origW = parent.getParent().getImage().getImage().getWidth();
		
		_parent = parent;
		_offset = ((double) offset) * _origH / _currH;
		_events = events;
	}

	public Barline(System parent, Barline other, Score score) {
		//this(parent, other.getOffset());
		_currH = parent.getParent().getParent().getCurrentHeight();
		_origH = parent.getParent().getParent().getOriginalHeight();
		_origW = parent.getParent().getImage().getImage().getWidth();
		
		_parent = parent;
		_offset = other.getAbsoluteOffset();
		_events = new LinkedList<Event>();
	}

	public void setCurrentHeight(int height) {
		_currH = height;
	}
	
	public void setOffset(int offset) {
		_offset = ((double) offset) * _origH / _currH;
	}

	public void setState(int state) {
		_state = state;
	}

	public void addEvent(Event event) {
		_events.add(event);
	}

	public void move(Point distance, ScoreObject intersect) {
		_offset += ((double) distance.x) * _origH / _currH;
		
		if (_offset < 0) _offset = 0.0;
		else if (_offset > _origW) _offset = (double)_origW;
	}
	

	public void setActive(Point location) {
		setState(ACTIVE);
	}

	public void setInactive() {
		setState(NOT_ACTIVE);
	}

	public void delete() {
		for (Event event : _events) {
			event.delete();
		}
	}

	public void deleteChild(ScoreObject child) {
		if (_events.remove(child)) {
			child.delete();
		}
	}
	
	public int getAbsoluteOffset() {
		return (int) _offset;
	}

	public int getOffset() {
		return (int) offset();
	}
	
	// Calculations are done in double format inside
	private double offset() {
		return _offset * _currH / _origH;
	}

	public int getState() {
		return _state;
	}

	public List<Event> getEvents() {
		return _events;
	}

	public boolean intersects(int x) {
		return (x >= offset() - 2 && x <= offset() + 2);
	}

	public System getParent() {
		return _parent;
	}

	public void normalize() {
		// Does nothing.
	}

	public boolean isLeft(int x) {
		return x > offset();
	}

	public boolean isRight(int x) {
		return x < offset();
	}
}
