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
	private double _top; // absolute values with regard to original image
	@Expose
	private double _bottom;
	private int _state = NONE_ACTIVE;
	@Expose
	private List<Barline> _barlines;

	private int _currH, _origH;
	
	public static final int NONE_ACTIVE = 0;
	public static final int TOP_ACTIVE = 1;
	public static final int BOTTOM_ACTIVE = 2;
	public static final int ALL_ACTIVE = 3;

	public System(Page parent, int top, int bottom) {
		this(parent, top, bottom, new ArrayList<Barline>());
	}

	public System(Page parent, int top, int bottom, List<Barline> barlines) {
		_currH = parent.getParent().getCurrentHeight();
		_origH = parent.getParent().getOriginalHeight();
		
		_parent = parent;
		_top = ((double) top) * _origH / _currH;
		_bottom = ((double) bottom) * _origH / _currH;
		_barlines = barlines;
	}

	public System(Page parent, System other, Score score) {
		//this(parent, other.getTop(), other.getBottom());
		_currH = parent.getParent().getCurrentHeight();
		_origH = parent.getParent().getOriginalHeight();
		
		_parent = parent;
		_top = other.getAbsoluteTop();
		_bottom = other.getAbsoluteBottom();
		_barlines = new ArrayList<Barline>();
		
		for (Barline barline : other.getBarlines()) {
			addBarline(new Barline(this, barline, score));
		}
	}
	
	public void setCurrentHeight(int height) {
		_currH = height;
	}

	public void setTop(int top) {
		_top = ((double) top) * _origH / _currH;
		if (top + 16 > getBottom()) {
			_bottom = ((double) (top + 16)) * _origH / _currH;
		}
	}

	public void setBottom(int bottom) {
		_bottom = ((double) bottom) * _origH / _currH;
		if (bottom - 16 < getTop()) {
			_top = ((double) (bottom - 16)) * _origH / _currH;
		}
	}

	public void setState(int state) {
		_state = state;
	}

	public void move(Point distance, ScoreObject intersect) {
		if (_state == BOTTOM_ACTIVE || _state == ALL_ACTIVE) {
			_bottom += ((double) distance.y) * _origH / _currH;
		}
		if (_state == TOP_ACTIVE || _state == ALL_ACTIVE) {
			_top += ((double) distance.y) * _origH / _currH;
		}
		
		if (_top < 0) setTop(0);
		else if (_top > _origH ) setTop(_currH - 16);
		if (_bottom < 0) setBottom(16);
		else if (_bottom > _origH) setBottom(_currH);
	}


	public void setActive(Point location) {
		setState(intersectsLine(location.y));
	}

	public void setInactive() {
		setState(NONE_ACTIVE);
	}

	public void delete() {
		for (Barline barline : _barlines) {
			barline.delete();
		}
	}

	public void deleteChild(ScoreObject child) {
		if (_barlines.remove(child)) {
			child.delete();
		}
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

	public double getAbsoluteTop() {
		return _top;
	}
	
	public double getAbsoluteBottom() {
		return _bottom;
	}
	
	public int getTop() {
		return (int) top();
	}
	
	private double top() {
		return _top * _currH / _origH;
	}

	public int getBottom() {
		return (int) bottom();
	}
	
	private double bottom() {
		return _bottom * _currH / _origH;
	}
	
	// Not the real height displayed
	public int getInnerHeight() {
		return (int) (bottom() - top());
	}

	public int getState() {
		return _state;
	}

	public List<Barline> getBarlines() {
		return _barlines;
	}

	public boolean intersects(int y) {
		return y >= top() && y <= bottom();
	}

	public int intersectsLine(int y) {
		if (y >= top() && y <= top() + 16) {
			return TOP_ACTIVE;
		}
		if (y >= bottom() - 3 && y <= bottom() + 3) {
			return BOTTOM_ACTIVE;
		}
		if (y > top() + 16 && y < bottom() - 3) {
			return ALL_ACTIVE;
		}
		return NONE_ACTIVE;
	}

	public Page getParent() {
		return _parent;
	}
}
