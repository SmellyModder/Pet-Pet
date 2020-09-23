package net.smelly.petpet;

import java.awt.image.BufferedImage;

/**
 * This class exists to pull of a trick for transparency in the exported gifs.
 * The transparency color for the gif export only works as white (for now) so this is used to very very lightly darken an image.
 * This uses slight multi-threading for faster performance, especially with larger images.
 * @author Luke Tonon
 */
public final class ThreadedImageDarkener implements Runnable {
	private final BufferedImage bufferedImage;

	public ThreadedImageDarkener(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
	}

	@Override
	public void run() {
		try {
			int bottomHeight = this.bufferedImage.getHeight() / 2;

			ColorProcessor topProcessor = new ColorProcessor(this.bufferedImage, "Top", 0, 0, this.bufferedImage.getWidth(), bottomHeight);
			topProcessor.start();
			topProcessor.join();

			ColorProcessor bottomProcessor = new ColorProcessor(this.bufferedImage, "Bottom", 0, bottomHeight, this.bufferedImage.getWidth(), this.bufferedImage.getHeight());
			bottomProcessor.start();
			bottomProcessor.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static class ColorProcessor extends Thread {
		private static final double DARKEN_STRENGTH = 0.99D;
		private final BufferedImage bufferedImage;
		private final int minWidth, minHeight;
		private final int maxWidth, maxHeight;

		public ColorProcessor(BufferedImage bufferedImage, String name, int minWidth, int minHeight, int maxWidth, int maxHeight) {
			super("PP-ColorProcessor-" + name);
			this.bufferedImage = bufferedImage;
			this.minWidth = minWidth;
			this.minHeight = minHeight;
			this.maxWidth = maxWidth;
			this.maxHeight = maxHeight;
		}

		@Override
		public void run() {
			for (int x = this.minWidth; x < this.maxWidth; x++) {
				for (int y = this.minHeight; y < this.maxHeight; y++) {
					int rgb = this.bufferedImage.getRGB(x, y);
					int r = (int) (((rgb >> 16) & 0xFF) * DARKEN_STRENGTH);
					int g = (int) (((rgb >> 8) & 0xFF) * DARKEN_STRENGTH);
					int b = (int) ((rgb & 0xFF) * DARKEN_STRENGTH);
					this.bufferedImage.setRGB(x, y, (((rgb >> 24) & 0xFF) << 24) | (r << 16) | (g << 8) | b);
				}
			}
		}
	}
}
