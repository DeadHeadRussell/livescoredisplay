package edu.cmu.mat.scores.events;

import java.awt.Point;

import edu.cmu.mat.scores.Barline;
import edu.cmu.mat.scores.ScoreObject;
import edu.cmu.mat.scores.Section;

public class SectionStartEvent extends Event {
	private Barline _parent;
	private Section _section;
	private boolean _is_repeat;

	public SectionStartEvent(Barline parent, Section section) {
		_parent = parent;
		_section = section;
	}

	public void setRepeat(boolean repeat) {
		_is_repeat = repeat;
	}

	@Override
	public Type getType() {
		return Event.Type.SECTION_START;
	}

	public Section getSection() {
		return _section;
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

	@Override
	public void delete() {
		_section.delete();
	}

	public boolean isRepeat() {
		return _is_repeat;
	}
}
