package edu.cmu.mat.scores.events;

import java.awt.Point;

import edu.cmu.mat.scores.Barline;
import edu.cmu.mat.scores.Repeat;
import edu.cmu.mat.scores.ScoreObject;

public class RepeatEndEvent extends Event {
	private Barline _parent;
	private Repeat _repeat;

	public RepeatEndEvent(Barline parent, Repeat repeat) {
		_parent = parent;
		_repeat = repeat;
	}

	public Repeat getRepeat() {
		return _repeat;
	}

	@Override
	public Type getType() {
		return Event.Type.REPEAT_END;
	}

	public ScoreObject move(Point distance, ScoreObject intersect) {
		if (intersect != null && intersect.getClass() == Barline.class
				&& intersect != _parent) {
			_repeat.move(null, (Barline) intersect);
			return _repeat.getEndEvent();
		}
		return null;
	}

	public void setActive(Point location) {
		_repeat.setActive(location);
	}

	public void setInactive() {
		_repeat.setInactive();
	}

	public boolean isActive() {
		return _repeat.isActive();
	}

	public ScoreObject getParent() {
		return _parent;
	}

	public void normalize() {
	}

	public void deleteChild(ScoreObject child) {
	}

	public void delete() {
		_repeat.delete();
	}
}
