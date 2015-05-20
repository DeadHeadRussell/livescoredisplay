package edu.cmu.mat.scores;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Image {
	private BufferedImage _originalImage;
	private java.awt.Image _resizedImage;

	public static int DIMENSION_WIDTH = 0;
	public static int DIMENSION_HEIGHT = 1;

	public Image(BufferedImage image) {
		_originalImage = image;

		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage coloured = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		coloured.getGraphics().setColor(Color.WHITE);
		coloured.getGraphics().fillRect(0, 0, width, height);
		coloured.getGraphics().drawImage(image, 0, 0, width, height, 0, 0,
				width, height, null);

		_resizedImage = coloured;
	}

	public void resize(int size, int dimension) {
		int width;
		int height;
		double factor;

		if (dimension == DIMENSION_WIDTH) {
			if (_resizedImage.getWidth(null) == size) {
				return;
			}

			width = size;
			factor = width / ((double) _originalImage.getWidth());
			height = (int) (_originalImage.getHeight() * factor);
		} else {
			if (_resizedImage.getHeight(null) == size) {
				return;
			}

			height = size;
			factor = height / ((double) _originalImage.getHeight());
			width = (int) (_originalImage.getWidth() * factor);
		}

		_resizedImage = _originalImage.getScaledInstance(width, height,
				BufferedImage.SCALE_SMOOTH);

		BufferedImage coloured = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		coloured.getGraphics().setColor(Color.WHITE);
		coloured.getGraphics().fillRect(0, 0, width, height);
		coloured.getGraphics().drawImage(_resizedImage, 0, 0, width, height, 0,
				0, width, height, null);

		_resizedImage = coloured;
	}

	public java.awt.Image getImage() {
		return _resizedImage;
	}

	public java.awt.Image crop(int top, int bottom) {
		int width = _resizedImage.getWidth(null);
		int height = _resizedImage.getHeight(null);

		top = Math.max(0, top);
		bottom = Math.min(height, bottom);

		// XXX: Minus 8 is a hack since the coordinates are off between the
		// image and the frame that the image is displayed in in notation mode.
		top = Math.max(top - 8, 0);
		bottom = Math.max(bottom, 0);

		int cropHeight = bottom - top;

		BufferedImage cropped = new BufferedImage(width, cropHeight,
				BufferedImage.TYPE_INT_ARGB);
		cropped.getGraphics().drawImage(_resizedImage, 0, 0, width, cropHeight,
				0, top, width, bottom, null);
		return cropped;

	}

	public static java.awt.Image MERGE(List<Page> pages, int top, int bottom) {
		List<java.awt.Image> images = new ArrayList<java.awt.Image>(
				pages.size());
		int height = 0;

		for (int i = 0; i < pages.size(); i++) {
			Page page = pages.get(i);
			List<System> systems = page.getSystems();

			int page_top = top;
			if (i > 0) {
				page_top = systems.get(0).getTop();
			}

			int page_bottom = bottom;
			if (i < pages.size() - 1) {
				page_bottom = systems.get(systems.size() - 1).getBottom();
			}

			images.add(page.getImage().crop(page_top, page_bottom));

			height += page_bottom - page_top;
		}

		int width = images.get(0).getWidth(null);

		BufferedImage merged = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		int y = 0;
		for (java.awt.Image image : images) {
			int image_height = image.getHeight(null);
			merged.getGraphics().drawImage(image, 0, y, width,
					y + image_height, 0, 0, width, image_height, null);
			y += image_height;
		}
		return merged;
	}
}
