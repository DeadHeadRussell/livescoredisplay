package edu.cmu.mat.scores;

import java.awt.image.BufferedImage;

public class Image {
	private BufferedImage _originalImage;
	private java.awt.Image _resizedImage;

	public static int DIMENSION_WIDTH = 0;
	public static int DIMENSION_HEIGHT = 1;

	public Image(BufferedImage image) {
		_originalImage = image;
		_resizedImage = image.getScaledInstance(image.getWidth(),
				image.getHeight(), BufferedImage.SCALE_SMOOTH);

	}

	public void resize(int size, int dimension) {
		int width;
		int height;
		double factor;

		if (dimension == DIMENSION_WIDTH) {
			width = size;
			factor = width / ((double) _originalImage.getWidth());
			height = (int) (_originalImage.getHeight() * factor);
		} else {
			height = size;
			factor = height / ((double) _originalImage.getHeight());
			width = (int) (_originalImage.getWidth() * factor);
		}

		_resizedImage = _originalImage.getScaledInstance(width, height,
				BufferedImage.SCALE_SMOOTH);
	}

	public java.awt.Image getImage() {
		return _resizedImage;
	}
}
