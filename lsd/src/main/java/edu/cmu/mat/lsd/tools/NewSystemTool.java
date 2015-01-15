package edu.cmu.mat.lsd.tools;

import java.awt.event.MouseEvent;

import edu.cmu.mat.scores.Page;
import edu.cmu.mat.scores.System;

public class NewSystemTool extends Tool {
	private Page _page = null;
	private System _system = null;

	public boolean mouseClicked(Page page, MouseEvent event) {
		return false;
	}

	public boolean mousePressed(Page page, MouseEvent event) {
		return false;
	}

	public boolean mouseReleased(Page page, MouseEvent event) {
		if (page == _page) {
			_system.setBottom(event.getY());
			_system = new System(_page, event.getY() - 16, event.getY());
			_page.addSystem(_system);
			return true;
		}
		return false;
	}

	public boolean mouseDragged(Page page, MouseEvent event) {
		if (page == _page) {
			_system.setBottom(event.getY());
			return true;
		}
		return false;
	}

	public boolean mouseMoved(Page page, MouseEvent event) {
		if (page == _page) {
			_system.setTop(event.getY() - 16);
			_system.setBottom(event.getY());
			return true;
		}
		return mouseEntered(page, event);
	}

	public boolean mouseEntered(Page page, MouseEvent event) {
		if (_page != page) {
			mouseExited(_page, event);
		}

		_page = page;
		_system = new System(_page, 0, 0);
		_page.addSystem(_system);

		return mouseMoved(page, event);
	}

	public boolean mouseExited(Page page, MouseEvent event) {
		if (_page == page && _page != null) {
			_page.getSystems().remove(_system);
			_system = null;
			_page = null;
			return true;
		}
		return false;
	}
}
