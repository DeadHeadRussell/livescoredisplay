package edu.cmu.mat.scores;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.annotations.Expose;

public class System implements ScoreObject {
	private Page _parent;
	@Expose
	private int _top;
	@Expose
	private int _bottom;
	private int _state = NONE_ACTIVE;
	@Expose
	private List<Barline> _barlines;

	public static final int NONE_ACTIVE = 0;
	public static final int TOP_ACTIVE = 1;
	public static final int BOTTOM_ACTIVE = 2;
	public static final int ALL_ACTIVE = 3;

	public System(Page parent, int top, int bottom) {
		this(parent, top, bottom, new ArrayList<Barline>());
	}

	public System(Page parent, int top, int bottom, List<Barline> barlines) {
		_parent = parent;
		_top = top;
		_bottom = bottom;
		_barlines = barlines;
	}

	public System(Page parent, System other, Score score) {
		this(parent, other.getTop(), other.getBottom());

		for (Barline barline : other.getBarlines()) {
			addBarline(new Barline(this, barline, score));
		}
	}

	public void setTop(int top) {
		_top = top;
		if (_top + 16 > _bottom) {
			_bottom = _top + 16;
		}
	}

	public void setBottom(int bottom) {
		_bottom = bottom;
		if (_bottom - 16 < _top) {
			_top = _bottom - 16;
		}
	}

	public void setState(int state) {
		_state = state;
	}

	public void move(Point distance, ScoreObject intersect) {
		if (_state == BOTTOM_ACTIVE || _state == ALL_ACTIVE) {
			_bottom += distance.y;
		}
		if (_state == TOP_ACTIVE || _state == ALL_ACTIVE) {
			_top += distance.y;
		}
	}

	public void setActive(Point location) {
		setState(intersectsLine(location.y));
	}

	public void setInactive() {
		setState(NONE_ACTIVE);
	}

	public void deleteChild(ScoreObject child) {
		_barlines.remove(child);
	}

	public void normalize() {
		Collections.sort(_barlines, new Comparator<Barline>() {
			public int compare(Barline bar1, Barline bar2) {
				return bar1.getOffset() - bar2.getOffset();
			}
		});
	}

	public void addBarline(Barline barline) {
		if (_barlines == null) {
			_barlines = new ArrayList<Barline>();
		}
		_barlines.add(barline);
	}

	public int getTop() {
		return _top;
	}

	public int getBottom() {
		return _bottom;
	}

	public int getState() {
		return _state;
	}

	public List<Barline> getBarlines() {
		return _barlines;
	}

	public boolean intersects(int y) {
		return y >= _top && y <= _bottom;
	}

	public int intersectsLine(int y) {
		if (y >= _top && y <= _top + 16) {
			return TOP_ACTIVE;
		}
		if (y >= _bottom - 3 && y <= _bottom + 3) {
			return BOTTOM_ACTIVE;
		}
		if (y > _top + 16 && y < _bottom - 3) {
			return ALL_ACTIVE;
		}
		return NONE_ACTIVE;
	}

	public Page getParent() {
		return _parent;
	}
}
