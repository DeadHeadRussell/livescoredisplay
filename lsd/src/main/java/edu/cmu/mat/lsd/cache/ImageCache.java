package edu.cmu.mat.lsd.cache;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.cmu.mat.scores.Page;

public class ImageCache {

	private File _library;
	
	public ImageCache(File path) {
		_library = path;
	}
	
	public BufferedImage find(Page page, int size) {
		String scoreName = page.getParent().getName();
		int index = page.getParent().getPages().indexOf(page);
		String fileName = Integer.toString(index+1)+"_"+Integer.toString(size)+".png";
		File image = new File(_library.getAbsolutePath()+File.separator
				+scoreName+File.separator+"cache"+File.separator+fileName);
		if (image.exists()) {
			try {
				return ImageIO.read(image);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void save(Page page, BufferedImage img, int size) {
		String scoreName = page.getParent().getName();
		int index = page.getParent().getPages().indexOf(page);
		String fileName = Integer.toString(index+1)+"_"+Integer.toString(size)+".png";
		
		File cache = new File(_library.getAbsolutePath()+File.separator
				+scoreName+File.separator+"cache");
		if (!cache.exists()) {
			cache.mkdir();
		}
		
		File image = new File(cache.getAbsolutePath() + File.separator + fileName);
		try {
			ImageIO.write(img, "png", image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
