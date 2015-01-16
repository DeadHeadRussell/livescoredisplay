package edu.cmu.mat.lsd.tools;

import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import edu.cmu.mat.lsd.Model;
import edu.cmu.mat.scores.Barline;
import edu.cmu.mat.scores.Page;

public class NewSectionTool extends Tool {
	private Model _model = null;
	private Barline _barline = null;
	private Barline _start_barline = null;

	public NewSectionTool(Model model) {
		_model = model;
	}

	public boolean mouseClicked(Page page, MouseEvent event) {
		if (_start_barline == null) {
			_start_barline = Tool.GetLeftBarline(page, event.getPoint());
			return true;
		}

		Barline end_barline = Tool.GetRightBarline(page, event.getPoint());
		if (end_barline != _start_barline && end_barline != null) {
			String name = JOptionPane.showInputDialog("Section Name");
			if (name != null && !name.equals("")) {
				_model.addSection(name, _start_barline, end_barline);
			}
			end_barline.setInactive();
			_start_barline.setInactive();
			_start_barline = null;

			if (_barline != null) {
				_barline.setInactive();
				_barline = null;
			}
			return true;
		}
		return false;
	}

	public boolean mousePressed(Page page, MouseEvent event) {
		return false;
	}

	public boolean mouseReleased(Page page, MouseEvent event) {
		return false;
	}

	public boolean mouseDragged(Page page, MouseEvent event) {
		return false;
	}

	public boolean mouseMoved(Page page, MouseEvent event) {
		return mouseEntered(page, event);
	}

	public boolean mouseEntered(Page page, MouseEvent event) {
		if (_barline != null && _barline != _start_barline) {
			_barline.setInactive();
		}

		_barline = Tool.GetLeftBarline(page, event.getPoint());

		if (_barline != null) {
			_barline.setActive(null);
		}

		return true;
	}

	public boolean mouseExited(Page page, MouseEvent event) {
		if (_barline != null) {
			_barline.setInactive();
			_barline = null;
		}
		return true;
	}
}
