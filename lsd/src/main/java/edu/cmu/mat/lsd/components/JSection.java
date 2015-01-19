package edu.cmu.mat.lsd.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import javax.swing.JPanel;

import edu.cmu.mat.scores.Section;

public class JSection extends JPanel implements ImageObserver {
	private static final long serialVersionUID = 4193873080878056943L;

	private static Color COLOR_FADE = new Color(255, 255, 255, 150);

	private Section _section;
	private Image _image;
	private int _width;
	private int _height;

	public JSection(Section section) {
		_section = section;
		_image = _section.getImage();
		_width = _image.getWidth(this);
		_height = _image.getHeight(this);
		this.setSize(new Dimension(_width, _height));

		setBackground(Color.WHITE);
	}

	@Override
	public boolean imageUpdate(Image image, int flags, int x, int y, int w,
			int h) {
		if (image == _image) {
			_width = w;
			_height = h;
			this.setSize(new Dimension(_width, _height));
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

		Rectangle top = _section.getTopRectangle();
		Rectangle bottom = _section.getBottomRectangle();
		graphics.setColor(COLOR_FADE);
		graphics.fillRect(top.x, top.y, top.width - 8, top.height);
		graphics.fillRect(bottom.x - 8, bottom.y, bottom.width, bottom.height);

		return;
	}
}
