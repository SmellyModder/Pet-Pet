package net.smelly.petpet;

import java.awt.image.BufferedImage;

/**
 * This class exists to pull of a trick for transparency in the exported gifs.
 * The transparency color for the gif export is black, so to have black colors in images work the image is brightened.
 * This uses slight multi-threading for faster performance, especially with larger images.
 * @author Luke Tonon
 * TODO: Possibly extend the usability of this class.
 */
public final class ThreadedImageBrightener implements Runnable {
	private final BufferedImage bufferedImage;
	private final int brightnessIncrease;

	public ThreadedImageBrightener(BufferedImage bufferedImage, float brightnessIncrease) {
		this.bufferedImage = bufferedImage;
		this.brightnessIncrease = ((int) (brightnessIncrease * 100.0F)) * 8;
	}

	@Override
	public void run() {
		try {
			int bottomHeight = this.bufferedImage.getHeight() / 2;

			ColorProcessor topProcessor = new ColorProcessor(this.bufferedImage, "Top", this.brightnessIncrease,0, 0, this.bufferedImage.getWidth(), bottomHeight);
			topProcessor.start();
			topProcessor.join();

			ColorProcessor bottomProcessor = new ColorProcessor(this.bufferedImage, "Bottom", this.brightnessIncrease,0, bottomHeight, this.bufferedImage.getWidth(), this.bufferedImage.getHeight());
			bottomProcessor.start();
			bottomProcessor.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static class ColorProcessor extends Thread {
		private final BufferedImage bufferedImage;
		private final int brightness;
		private final int minWidth, minHeight;
		private final int maxWidth, maxHeight;

		public ColorProcessor(BufferedImage bufferedImage, String name, int brightness, int minWidth, int minHeight, int maxWidth, int maxHeight) {
			super("PP-ColorProcessor-" + name);
			this.bufferedImage = bufferedImage;
			this.brightness = brightness;
			this.minWidth = minWidth;
			this.minHeight = minHeight;
			this.maxWidth = maxWidth;
			this.maxHeight = maxHeight;
		}

		@Override
		public void run() {
			int brightness = this.brightness;
			for (int x = this.minWidth; x < this.maxWidth; x++) {
				for (int y = this.minHeight; y < this.maxHeight; y++) {
					int rgb = this.bufferedImage.getRGB(x, y);
					int r = ((rgb >> 16) & 0xFF);
					int g = ((rgb >> 8) & 0xFF);
					int b = (rgb & 0xFF);
					this.bufferedImage.setRGB(x, y, (((rgb >> 24) & 0xFF) << 24) | ((r + (255 - r) / brightness) << 16) | ((g + (255 - g) / brightness) << 8) | (b + (255 - b) / brightness));
				}
			}
		}
	}
}
