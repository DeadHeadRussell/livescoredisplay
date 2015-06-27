package edu.cmu.mat.lsd.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import javax.swing.JPanel;

import edu.cmu.mat.scores.Block;

public class JBlock extends JPanel implements ImageObserver{
	private static final long serialVersionUID = -4507069873908072713L;
	
	private Block _block;
	private Image _image;
	private static int _width;
	private static int _height;

	public JBlock(int blockHeight) {
		_height = blockHeight;
		setBackground(Color.WHITE);
	}
	
	public void setBlock(Block block) {
		_block = block;
		_image = _block.getImage();
		_width = _image.getWidth(this);
		this.setSize(new Dimension(_width, _height));
	}
	
	public int getHeight() {
		return _height;
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

		return;
	}
}
