package com.vadrin.hdevwallpaper.services;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class ImageService {

	private Graphics graphics;

	public BufferedImage resizeImage(BufferedImage originalImage, double scale) {
		BufferedImage resizedImage = new BufferedImage((int) (originalImage.getWidth() * scale),
				(int) (originalImage.getHeight() * scale), originalImage.getType());
		graphics = resizedImage.createGraphics();
		graphics.drawImage(originalImage, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
		graphics.dispose();
		return resizedImage;
	}

	public BufferedImage convertPngToJpg(BufferedImage image) throws IOException {
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
		return result;
	}

	public BufferedImage cropImage(BufferedImage src, Rectangle rect) {
		BufferedImage dest = src.getSubimage(rect.x, rect.y, rect.width, rect.height);
		return dest;
	}

	public BufferedImage overlapImages(BufferedImage forground, BufferedImage background) {
		graphics = background.getGraphics();
		graphics.drawImage(forground, background.getWidth() - forground.getWidth(),
				background.getHeight() - forground.getHeight(), null);
		graphics.dispose();
		return background;
	}

}
