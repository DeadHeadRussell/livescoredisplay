package edu.cmu.mat.lsd.tools;

import java.awt.event.MouseEvent;

import edu.cmu.mat.scores.Barline;
import edu.cmu.mat.scores.Page;
import edu.cmu.mat.scores.System;

public class NewBarlineTool extends Tool {
	private Page _page = null;
	private Barline _barline = null;
	private System _system = null;

	public boolean mouseClicked(Page page, MouseEvent event) {
		return false;
	}

	public boolean mousePressed(Page page, MouseEvent event) {
		return false;
	}

	public boolean mouseReleased(Page page, MouseEvent event) {
		if (page == _page && _barline != null) {
			_barline.setOffset(event.getX());
			_barline = new Barline(_system, event.getX());
			_system.addBarline(_barline);
			page.getParent().normalize();
			return true;
		}
		return false;
	}

	public boolean mouseDragged(Page page, MouseEvent event) {
		return mouseMoved(page, event);
	}

	public boolean mouseMoved(Page page, MouseEvent event) {
		if (page == _page) {
			if (_system != null) {
				_system.getBarlines().remove(_barline);
				_barline = null;
			}

			_system = Tool.GetIntersectedSystem(page, event.getPoint());

			if (_system != null) {
				_barline = new Barline(_system, event.getX());
				_system.addBarline(_barline);
			}

			return true;
		}
		return mouseEntered(page, event);
	}

	public boolean mouseEntered(Page page, MouseEvent event) {
		if (_page != page) {
			mouseExited(page, event);
		}

		_page = page;

		return mouseMoved(page, event);
	}

	public boolean mouseExited(Page page, MouseEvent event) {
		if (_page == page) {
			_page = null;

			if (_system != null) {
				_system.getBarlines().remove(_barline);
				_barline = null;
				_system = null;
				return true;
			}
		}

		return false;
	}
}
