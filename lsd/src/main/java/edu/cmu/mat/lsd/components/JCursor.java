package edu.cmu.mat.lsd.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JPanel;

import edu.cmu.mat.scores.Arrangement;

public class JCursor extends JPanel {
	private static final long serialVersionUID = -3771280671369537552L;

	private JComponent _parent;
	private int _x;
	private int _y;

	public JCursor() {
		resetTime();
	}

	public JCursor(JComponent parent, Arrangement arrangement) {
		_parent = parent;
	}

	public void resetTime() {
		_x = _y = -1;
	}

	public void setPosition(int x, int y) {
		_x = x;
		_y = y;
	}

	@Override
	public Dimension getPreferredSize() {
		return _parent.getSize();
	}

	@Override
	public void paint(Graphics graphics) {
		if (_x == -1 || _y == -1) {
			return;
		}
		graphics.setColor(Color.RED);
		graphics.fillOval(_x, _y, 5, 5);
		graphics.setColor(Color.BLACK);
		graphics.drawOval(_x, _y, 5, 5);
	}
}
