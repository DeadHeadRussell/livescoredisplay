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
	private int _offset;
	@Expose
	private List<Event> _events;

	public static final int NOT_ACTIVE = 0;
	public static final int ACTIVE = 1;

	public Barline(System parent, int offset) {
		this(parent, offset, new LinkedList<Event>());
	}

	public Barline(System parent, int offset, List<Event> events) {
		_parent = parent;
		_offset = offset;
		_events = events;
	}

	public Barline(System parent, Barline other, Score score) {
		this(parent, other.getOffset());
	}

	public void setOffset(int offset) {
		_offset = offset;
	}

	public void setState(int state) {
		_state = state;
	}

	public void addEvent(Event event) {
		_events.add(event);
	}

	public ScoreObject move(Point distance, ScoreObject intersect) {
		_offset += distance.x;
		return null;
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

	public int getOffset() {
		return _offset;
	}

	public int getState() {
		return _state;
	}

	public List<Event> getEvents() {
		return _events;
	}

	public boolean intersects(int x) {
		return (x >= _offset - 2 && x <= _offset + 2);
	}

	public System getParent() {
		return _parent;
	}

	public void normalize() {
		// Does nothing.
	}

	public boolean isLeft(int x) {
		return x > _offset;
	}

	public boolean isRight(int x) {
		return x < _offset;
	}
}
