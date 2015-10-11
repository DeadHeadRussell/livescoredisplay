package edu.cmu.mat.lsd.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.List;

import javax.swing.JPanel;

import edu.cmu.mat.scores.Block;
import edu.cmu.mat.scores.Page;
import edu.cmu.mat.scores.Score;
import edu.cmu.mat.scores.System;

public class JBlock extends JPanel implements ImageObserver{
	private static final long serialVersionUID = -4507069873908072713L;
	
	private Block _block;
	//private Image _image;
	private static int _width;
	private static int _height;

	public JBlock(int blockHeight) {
		_height = blockHeight;
		setBackground(Color.WHITE);
	}
	
	public void setWidth(int width) {
		_width = width;
		this.setSize(_width, _height);
	}
	
	public void setBlock(Block block) {
		_block = block;
		//_image = _block.getImage();
		//_width = _image.getWidth(this);
		//this.setSize(new Dimension(_width, _height));
	}
	
	public int getHeight() {
		return _height;
	}
	
	// Not very sure what the method does, adapted from JSection.java
//	@Override
//	public boolean imageUpdate(Image image, int flags, int x, int y, int w,
//			int h) {
//		if (image == _image) {
//			_width = w;
//			_height = h;
//			this.setSize(new Dimension(_width, _height));
//		}
//		return super.imageUpdate(image, flags, x, y, w, h);
//	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(_width, _height);
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);

		//graphics.drawImage(_image, 0, 0, this);
		//graphics.drawImage(_image, 0, 0, 50, 50, Color.WHITE, this);
		//graphics.drawImage(_image, 100, 100, 200, 200, 0, 50,  50, 100, Color.WHITE, this);
		
		System startSystem = _block.getStartSystem();
		System endSystem = _block.getEndSystem();
		
		Page startPage = startSystem.getParent();
		Page endPage = endSystem.getParent();
		
		Score score = startPage.getParent();
		List<Page> pages = score.getPages();
		int startIndex = pages.indexOf(startPage);
		int endIndex = pages.indexOf(endPage);
		//java.lang.System.out.format("startI: %d, endI: %d\n", startIndex, endIndex);
		//List<Page> sectionPages = pages.subList(startIndex, endIndex + 1);

		int top = (int) startSystem.getAbsoluteTop();
		int bottom = (int) endSystem.getAbsoluteBottom();
		int height = 0;
		for (int i = startIndex; i < endIndex+1; i++) {
			Page page = pages.get(i);
			List<System> systems = page.getSystems();
			/*
			BufferedImage image = page.getImage().getOriginalImage();
			//Get current GraphicsConfiguration
            GraphicsConfiguration graphicsConfiguration 
                    = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();
             
            //Create a Compatible BufferedImage
            BufferedImage bufferedImage 
                    = graphicsConfiguration.createCompatibleImage(
                    image.getWidth(), 
                    image.getHeight());
            bufferedImage.getGraphics().setColor(Color.WHITE);
            bufferedImage.getGraphics().drawImage(image, 0, 0,  null);
            */
			BufferedImage image = page.getImage().getOriginalImage();
            
			int pageTop = top;
			if (i > 0) {
				pageTop = (int) systems.get(0).getAbsoluteTop();
			}

			int pageBottom = bottom;
			if (i < endIndex - startIndex) {
				pageBottom = (int) systems.get(systems.size() - 1).getAbsoluteBottom();
			}
			java.lang.System.out.println("Image height is" + image.getHeight());
			java.lang.System.out.println("Image width is" + image.getWidth());
			int sx1 = 0;
			int sy1 = pageTop;
			int sx2 = image.getWidth();
			int sy2 = pageBottom;
			java.lang.System.out.println("source: " + sx1 + " " + sy1 + " " + sx2 + " "+ sy2);
			int dx1 = 0;
			int dy1 = height;
			int dx2 = _width;
			int dy2 = height + (int) ((pageBottom - pageTop) * 1.0 * dx2 / sx2); // dx2 / sx2 is factor ratio
			//long start = java.lang.System.currentTimeMillis();
			java.lang.System.out.println("dest: " + dx1 + " " + dy1 + " " + dx2 + " "+ dy2);
			graphics.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, Color.WHITE, null);
			//long end = java.lang.System.currentTimeMillis();
			//long diff = end - start;
			//java.lang.System.out.println("Difference is " + diff);
			height = dy2;
		}
		
		
		return;
	}
}
