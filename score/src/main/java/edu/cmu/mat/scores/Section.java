package edu.cmu.mat.scores;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import com.google.gson.annotations.Expose;

import edu.cmu.mat.scores.events.Event;
import edu.cmu.mat.scores.events.SectionEndEvent;
import edu.cmu.mat.scores.events.SectionStartEvent;

public class Section {
	private Score _score;

	@Expose
	private String _name = "";
	@Expose
	private int _start_index;
	@Expose
	private int _end_index;

	private Barline _start;
	private Barline _end;
	private int _state = NOT_ACTIVE;

	public static final int NOT_ACTIVE = 0;
	public static final int ACTIVE = 1;

	public Section() {
		// This is required for Gson to deserialze default values.
	}

	public Section(Score score, Barline start, Barline end) {
		_score = score;
		_start = start;
		_end = end;
		normalize();
	}

	public Section(Score score, Section other) {
		_score = score;
		_name = other._name;
		_start_index = other._start_index;
		_end_index = other._end_index;

		List<Barline> start_barlines = _score.getStartBarlines();
		List<Barline> end_barlines = _score.getEndBarlines();
		_start = start_barlines.get(_start_index);
		_end = end_barlines.get(_end_index);
	}

	public void normalize() {
		_start_index = _score.getStartBarlines().indexOf(_start);
		_end_index = _score.getEndBarlines().indexOf(_end);
	}

	public SectionStartEvent getStartEvent() {
		for (Event event : getStart().getEvents()) {
			if (event.getType() == Event.Type.SECTION_START) {
				SectionStartEvent startEvent = (SectionStartEvent) event;
				if (startEvent.getSection() == this) {
					return startEvent;
				}
			}
		}
		return null;
	}

	public SectionEndEvent getEndEvent() {
		for (Event event : getEnd().getEvents()) {
			if (event.getType() == Event.Type.SECTION_END) {
				SectionEndEvent endEvent = (SectionEndEvent) event;
				if (endEvent.getSection() == this) {
					return endEvent;
				}
			}
		}
		return null;
	}

	public Section setName(String name) {
		_name = name;
		return this;
	}

	public void setState(int state) {
		_state = state;
	}

	public void setActive(Point location) {
		setState(ACTIVE);
	}

	public void setInactive() {
		setState(NOT_ACTIVE);
	}

	public Barline getStart() {
		return _start;
	}

	public Barline getEnd() {
		return _end;
	}

	public String getName() {
		return _name;
	}

	public int getState() {
		return _state;
	}

	public java.awt.Image getImage() {
		Page start_page = _start.getParent().getParent();
		Page end_page = _end.getParent().getParent();

		int top = _start.getParent().getTop();
		int bottom = _end.getParent().getBottom();

		if (start_page == end_page) {
			return start_page.getImage().crop(top, bottom);
		} else {
			Score score = start_page.getParent();
			List<Page> pages = score.getPages();
			int start_index = pages.indexOf(start_page);
			int end_index = pages.indexOf(end_page);

			List<Page> sectionPages = pages.subList(start_index, end_index + 1);
			return Image.MERGE(sectionPages, top, bottom);
		}
	}

	public void deleteChild(ScoreObject child) {
		// Does nothing.
	}

	public Rectangle getTopRectangle() {
		System start_system = _start.getParent();
		int width = _start.getOffset();
		int height = start_system.getBottom() - start_system.getTop();
		return new Rectangle(0, 0, width, height);
	}

	public Rectangle getBottomRectangle() {
		System end_system = _end.getParent();
		Page start_page = _start.getParent().getParent();
		Page end_page = end_system.getParent();

		int x = _end.getOffset();
		int y = end_system.getTop();
		if (start_page == end_page) {
			y -= _start.getParent().getTop();
		}

		int width = end_page.getImage().getImage().getWidth(null) - x;
		int height = end_system.getBottom() - end_system.getTop();

		return new Rectangle(x, y, width, height);
	}

	public void delete() {
		_start.getParent().getParent().getParent().removeSection(this);
	}

	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	public void move(Object object, Barline intersect) {
		// TODO Auto-generated method stub
		
	}
}
