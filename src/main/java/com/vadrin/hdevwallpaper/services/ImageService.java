package com.vadrin.hdevwallpaper.services;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

	private static final String TEMPFILENAME = "hdev-wallpaper";
	private static final int XSTART = 10;
	private static final int YSTART = 50;
	private static final float TITLEFONTSIZE = 30f;
	private static final float DESCRIPTIONFONTSIZE = 15f;
	private static final int OFFSET = 60;

	@Value("${com.vadrin.hdev-wallpaper.screenwidth:640}")
	private int SCREEN_WIDTH;
	@Value("${com.vadrin.hdev-wallpaper.screenheight:480}")
	private int SCREEN_HEIGHT;

	public BufferedImage resizeImage(BufferedImage originalImage) {
		double scaleW = (double) SCREEN_WIDTH / (double) originalImage.getWidth();
		double scaleH = (double) SCREEN_HEIGHT / (double) originalImage.getHeight();
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		double scale = scaleW < scaleH ? scaleW : scaleH;
		BufferedImage resizedImage = new BufferedImage((int) (originalImage.getWidth() * scale),
				(int) (originalImage.getHeight() * scale), type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
		g.dispose();
		return resizedImage;
	}
	
	public void resizeImage(File inputImage) throws IOException{
		BufferedImage image = resizeImage(ImageIO.read(inputImage));
		ImageIO.write(image, FilenameUtils.getExtension(inputImage.getName()), inputImage);
	}
	
	public void convertPngToJpg(File scrFile) throws IOException{
		BufferedImage image = ImageIO.read(scrFile);
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
		ImageIO.write(result, "jpg", scrFile);
	}

}
