package edu.cmu.mat.lsd.frames;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import edu.cmu.mat.lsd.Controller;
import edu.cmu.mat.lsd.ControllerListener;
import edu.cmu.mat.lsd.Model;
import edu.cmu.mat.lsd.menus.DisplayMenuBar;
import edu.cmu.mat.lsd.panels.DisplayPanel;
import edu.cmu.mat.lsd.panels.NotationPanel;
import edu.cmu.mat.lsd.panels.Panel;
import edu.cmu.mat.lsd.toolbars.DisplayToolbar;
import edu.cmu.mat.lsd.toolbars.NotationToolbar;
import edu.cmu.mat.lsd.toolbars.Toolbar;

public class Window implements ControllerListener {
	private Model _model;
	private Controller _controller;
	private Panel _notation = null;
	private Panel _display = null;
	private Toolbar _toolbar = null;
	private JFrame _frame = new JFrame();
	private int _previous_view = -1;

	public Window(Model model, Controller controller) {
		_model = model;
		_controller = controller;
		_controller.addListener(this);

		_frame.setTitle("Live Score Display");
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		DisplayMenuBar menu = new DisplayMenuBar(model);
		_controller.addListener(menu);
		_frame.setJMenuBar(menu);

		_notation = new NotationPanel(model);
		_display = new DisplayPanel(model);
		_controller.addListener(_notation);
		_controller.addListener(_display);

		_frame.add(_notation.getContainer());

		_frame.addComponentListener(new ComponentMoveListener());
		_frame.addWindowListener(new WindowCloseListener());

		onUpdateView();
		onUpdateLibraryPath();

		_frame.setVisible(true);
	}

	public void onUpdateLibraryPath() {
		if (_model.hasSize()) {
			_frame.setBounds(_model.getWindowX(), _model.getWindowY(),
					_model.getWindowWidth(), _model.getWindowHeight());
		} else {
			_frame.setBounds(100, 100, 800, 600);
		}
		_frame.repaint();
		_frame.revalidate();
	}

	public void onUpdateView() {
		int view = _model.getCurrentView();

		String constraint = getToolbarConstraint();
		if (_toolbar != null) {
			java.awt.Window c = SwingUtilities.getWindowAncestor(_toolbar
					.getToolbar());
			if (!c.equals(_frame)) {
				c.dispose();
			}
			c.remove(_toolbar.getToolbar());
		}

		if (isNewView(view)) {
			if (_toolbar instanceof ControllerListener) {
				_controller.removeListener((ControllerListener) _toolbar);
			}

			if (view == Model.VIEW_NOTATION) {
				_toolbar = new NotationToolbar(_model);
				_controller.addListener((NotationToolbar) _toolbar);
				_frame.remove(_display.getContainer());
				_frame.add(_notation.getContainer());
			} else if (view == Model.VIEW_DISPLAY) {
				_toolbar = new DisplayToolbar(_model);
				_frame.remove(_notation.getContainer());
				_frame.add(_display.getContainer());
			}
		}

		if (constraint.equals(BorderLayout.LINE_START)) {
			_toolbar.getToolbar().setOrientation(JToolBar.VERTICAL);
		}

		_frame.add(_toolbar.getToolbar(), constraint);
		_frame.revalidate();
		_previous_view = view;
	}

	public void onUpdateModel() {
	}

	public void onUpdateScore() {
	}

	public void onUpdateTool() {
	}

	public void onProgramQuit() {
	}

	private boolean isNewView(int view) {
		return _previous_view != view;
	}

	private String getToolbarConstraint() {
		String constraint = BorderLayout.PAGE_START;

		if (_toolbar != null) {
			JToolBar toolbar = _toolbar.getToolbar();
			int orientation = toolbar.getOrientation();
			if (orientation == JToolBar.HORIZONTAL) {
				constraint = BorderLayout.PAGE_START;
			} else if (orientation == JToolBar.VERTICAL) {
				constraint = BorderLayout.LINE_START;
			}
		}

		return constraint;
	}

	private class ComponentMoveListener implements ComponentListener {
		public void componentShown(ComponentEvent e) {
		}

		public void componentResized(ComponentEvent e) {
			_model.setWindowWidth(e.getComponent().getWidth());
			_model.setWindowHeight(e.getComponent().getHeight());
		}

		public void componentMoved(ComponentEvent e) {
			_model.setWindowX(e.getComponent().getX());
			_model.setWindowY(e.getComponent().getY());
		}

		public void componentHidden(ComponentEvent e) {
		}
	}

	private class WindowCloseListener implements WindowListener {
		public void windowActivated(WindowEvent e) {
		}

		public void windowClosed(WindowEvent e) {
		}

		public void windowClosing(WindowEvent e) {
			_model.quit();
		}

		public void windowDeactivated(WindowEvent e) {
		}

		public void windowDeiconified(WindowEvent e) {
		}

		public void windowIconified(WindowEvent e) {
		}

		public void windowOpened(WindowEvent e) {
		}
	}
}
