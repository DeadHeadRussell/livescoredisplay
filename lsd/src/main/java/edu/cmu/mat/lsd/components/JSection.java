package edu.cmu.mat.lsd.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JPanel;

import edu.cmu.mat.scores.Section;

public class JSection extends JPanel {
	private static final long serialVersionUID = 4193873080878056943L;

	private static Color COLOR_FADE = new Color(255, 255, 255, 150);

	private Section _section;
	private Section _end;
	private Image _image;
	private int _width;
	private int _height;

	private int _start_x;
	private int _start_y;
	private int _end_x;
	private int _end_y;

	public JSection(Section section, Section end) {
		_section = section;
		_end = end;
		_image = Section.GET_IMAGE(section, end);
		//_width = _image.getWidth(this);
		//_height = _image.getHeight(this);

		setBackground(Color.WHITE);
	}

	@Override
	public boolean imageUpdate(Image image, int flags, int x, int y, int w,
			int h) {
		if (image == _image) {
			_width = w;
			_height = h;
		}
		return super.imageUpdate(image, flags, x, y, w, h);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(_width, _height);
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);

		graphics.drawImage(_image, 0, 0, this);

		Point top_left = _section.getTopLeft();
		Point bottom_right = _end.getBottomRight();

		graphics.setColor(COLOR_FADE);
		graphics.fillRect(0, 0, top_left.x - 8, top_left.y);
		graphics.fillRect(bottom_right.x - 8, bottom_right.y, _width, _height);

		return;
	}
/*
	{
		Barline start_bar = _section.getParent();
		System start_system = start_bar.getParent();
		Barline end_bar = null;
		System end_system = null;
		if (_next != null) {
			end_bar = _next.getParent();
			end_system = end_bar.getParent();
		}

		graphics.setColor(COLOR_FADE);

		int start_height = start_system.getBottom() - start_system.getTop();
		_start_x = start_bar.getOffset() - 8;
		_start_y = start_height;
		graphics.fillRect(0, 0, _start_x, _start_y);

		if (end_bar != null) {
			int top = end_system.getTop() - start_system.getTop();
			int width = _width - end_bar.getOffset() + 8;
			int height = end_system.getBottom() - end_system.getTop();
			_end_x = end_bar.getOffset() - 8;
			_end_y = top;
			graphics.fillRect(_end_x, _end_y, width, height);
		}
	}
*/
	public int getStartX() {
		return _start_x;
	}

	public int getStartY() {
		return _start_y;
	}

	public int getEndX() {
		return _end_x;
	}

	public int getEndY() {
		return _end_y;
	}
}
