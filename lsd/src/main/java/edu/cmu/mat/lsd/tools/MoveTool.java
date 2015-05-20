package edu.cmu.mat.lsd.tools;

import java.awt.Point;
import java.awt.event.MouseEvent;

import edu.cmu.mat.scores.Page;
import edu.cmu.mat.scores.ScoreObject;

public class MoveTool extends Tool {
	private Page _page = null;
	private ScoreObject _scoreObject = null;
	private Point _last = null;

	public boolean mouseClicked(Page page, MouseEvent event) {
		return false;
	}

	public boolean mousePressed(Page page, MouseEvent event) {
		if (page == _page) {
			_last = event.getPoint();
		}
		return false;
	}

	public boolean mouseReleased(Page page, MouseEvent event) {
		if (page == _page) {
			boolean result = mouseDragged(page, event);
			if (_scoreObject != null) {
				_scoreObject.getParent().normalize();
			}
			_last = null;
			return result;
		}
		return false;
	}

	public boolean mouseDragged(Page page, MouseEvent event) {
		if (page == _page && _scoreObject != null) {
			Point current = event.getPoint();
			Point distance = new Point(current.x - _last.x, current.y - _last.y);
			_last = event.getPoint();

			if (distance.x != 0 || distance.y != 0) {
				ScoreObject newObject = _scoreObject.move(distance,
						Tool.GetIntersectedScoreObject(page, event.getPoint()));

				if (newObject != null) {
					_scoreObject = newObject;
					_scoreObject.setActive(event.getPoint());
				}

				return true;
			}
		}
		return false;
	}

	public boolean mouseMoved(Page page, MouseEvent event) {
		if (page == _page) {
			if (_scoreObject != null) {
				_scoreObject.setInactive();
			}

			_scoreObject = GetIntersectedScoreObject(page, event.getPoint());

			if (_scoreObject != null) {
				_scoreObject.setActive(event.getPoint());
			}

			return true;
		}
		return mouseEntered(page, event);
	}

	public boolean mouseEntered(Page page, MouseEvent event) {
		if (_page != page && _last == null) {
			mouseExited(page, event);
		}

		_page = page;

		return mouseMoved(page, event);
	}

	public boolean mouseExited(Page page, MouseEvent event) {
		if (_page != null && _last == null) {
			_page = null;
			if (_scoreObject != null) {
				_scoreObject.setInactive();
			}
			_scoreObject = null;
			return true;
		}
		return false;
	}
}
