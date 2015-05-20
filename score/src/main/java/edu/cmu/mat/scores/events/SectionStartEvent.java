package edu.cmu.mat.scores.events;

import java.awt.Point;

import edu.cmu.mat.scores.Barline;
import edu.cmu.mat.scores.ScoreObject;
import edu.cmu.mat.scores.Section;

public class SectionStartEvent extends Event {
	private Barline _parent;
	private Section _section;

	public SectionStartEvent(Barline parent, Section section) {
		_parent = parent;
		_section = section;
	}

	public Section getSection() {
		return _section;
	}

	@Override
	public Type getType() {
		return Event.Type.SECTION_START;
	}

	@Override
	public ScoreObject move(Point distance, ScoreObject intersect) {
		if (intersect != null && intersect.getClass() == Barline.class
				&& intersect != _parent) {
			_section.move((Barline) intersect, null);
			return _section.getStartEvent();
		}
		return null;
	}

	@Override
	public void deleteChild(ScoreObject child) {
	}

	@Override
	public void setActive(Point location) {
		_section.setActive(location);
	}

	@Override
	public void setInactive() {
		_section.setInactive();
	}

	@Override
	public boolean isActive() {
		return _section.isActive();
	}

	@Override
	public ScoreObject getParent() {
		return _parent;
	}

	@Override
	public void normalize() {
	}

	@Override
	public void delete() {
		_section.delete();
	}
}
