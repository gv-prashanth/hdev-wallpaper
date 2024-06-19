package com.vadrin.hdevwallpaper.controllers;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.vadrin.hdevwallpaper.services.HDEVService;
import com.vadrin.hdevwallpaper.services.ISSService;
import com.vadrin.hdevwallpaper.services.ImageService;
import com.vadrin.hdevwallpaper.services.OSService;

@Component
public class HDEVController implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(HDEVController.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	OSService osService;

	@Autowired
	ImageService imageService;

	@Autowired
	HDEVService hdevService;

	@Autowired
	ISSService issService;

	@Value("${com.vadrin.hdev-wallpaper.cropPixels}")
	private int cropPixels;
	@Value("${com.vadrin.hdev-wallpaper.issScale}")
	private double issScale;
	@Value("${com.vadrin.hdev-wallpaper.showMap}")
	private boolean showMap;

	@Override
	public void run(String... args) {
		log.info("Starting hdev-wallpaper at {}", dateFormat.format(new Date()));
		try {
			while (true) {
				log.info("Starting to update wallpaper at {}", dateFormat.format(new Date()));
				BufferedImage hdevServiceScreenshotSmall = hdevService.takeScreenshot();
				BufferedImage hdevServiceCropped = imageService.cropImage(hdevServiceScreenshotSmall,
						new Rectangle(cropPixels, 0, (hdevServiceScreenshotSmall.getWidth() - 2 * cropPixels),
								hdevServiceScreenshotSmall.getHeight()));
				BufferedImage screenshotJpg = null;
				if(showMap) {
					BufferedImage issServiceScreenshot = imageService.resizeImage(issService.takeScreenshot(), issScale);
					BufferedImage screenshotPng = imageService.overlapImages(issServiceScreenshot, hdevServiceCropped);
					screenshotJpg = imageService.convertPngToJpg(screenshotPng);
				}else {
					screenshotJpg = imageService.convertPngToJpg(hdevServiceCropped);
				}
				log.info("Attempting to update wallpaper at {}", dateFormat.format(new Date()));
				osService.setWallpaper(screenshotJpg);
				log.info("Completed setting new wallpaper at {}", dateFormat.format(new Date()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception setting new wallpaper at {}", dateFormat.format(new Date()));
		}
	}

}
