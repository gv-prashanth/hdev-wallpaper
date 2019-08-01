package com.vadrin.hdevwallpaper.controllers;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

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
	private static final String TEMPFILENAME = "hdev-wallpaper";

	@Autowired
	OSService osService;

	@Autowired
	ImageService imageService;

	@Autowired
	HDEVService hdevService;

	@Autowired
	ISSService issService;

	@Value("${com.vadrin.hdev-wallpaper.cropPixels}")
	private int cropFixels;
	@Value("${com.vadrin.hdev-wallpaper.issScale}")
	private double issScale;

	@Override
	public void run(String... args) {
		log.info("Starting hdev-wallpaper at {}", dateFormat.format(new Date()));
		while (true) {
			try {
				log.info("Attempting to update wallpaper at {}", dateFormat.format(new Date()));
				BufferedImage hdevServiceScreenshotSmall = hdevService.takeScreenshot();
				BufferedImage hdevServiceCropped = imageService.cropImage(hdevServiceScreenshotSmall, new Rectangle(cropFixels, 0,
						(hdevServiceScreenshotSmall.getWidth() - 2 * cropFixels), hdevServiceScreenshotSmall.getHeight()));
				BufferedImage issServiceScreenshot = imageService.resizeImage(issService.takeScreenshot(), issScale);
				BufferedImage screenshotPng = imageService.overlapImages(issServiceScreenshot, hdevServiceCropped);
				BufferedImage screenshotJpg = imageService.convertPngToJpg(screenshotPng);
				File outputfile = File.createTempFile(TEMPFILENAME, "." + "jpg");
				ImageIO.write(screenshotJpg, "jpg", outputfile);
				osService.setWallpaper(outputfile);
				outputfile.delete();
				log.info("Completed setting new wallpaper at {}", dateFormat.format(new Date()));
			}catch(Exception e) {
				log.info("Exception setting new wallpaper at {}", dateFormat.format(new Date()));
			}
		}
	}

	// @Override
	// public void run(String... args) throws Exception {
	// log.info("Starting hdev-wallpaper at {}", dateFormat.format(new Date()));
	// while (true) {
	// log.info("Attempting to update wallpaper at {}", dateFormat.format(new
	// Date()));
	// StopWatch sw = new StopWatch();
	// sw.start("takeScreenshot");
	// BufferedImage hdevServiceScreenshotSmall = hdevService.takeScreenshot();
	// sw.stop();
	// sw.start("resizeImage");
	// BufferedImage hdevServiceScreenshot =
	// imageService.resizeImage(hdevServiceScreenshotSmall);
	// sw.stop();
	// sw.start("cropImage");
	// BufferedImage hdevServiceResize =
	// imageService.cropImage(hdevServiceScreenshot, new Rectangle(cropFixels,
	// 0,
	// (hdevServiceScreenshot.getWidth() - 2 * cropFixels),
	// hdevServiceScreenshot.getHeight()));
	// sw.stop();
	// sw.start("issServiceScreenshot");
	// BufferedImage issServiceScreenshot =
	// imageService.resizeImage(issService.takeScreenshot(), issScale);
	// sw.stop();
	// sw.start("overlapImages");
	// BufferedImage screenshotPng =
	// imageService.overlapImages(issServiceScreenshot, hdevServiceResize);
	// sw.stop();
	// sw.start("convertPngToJpg");
	// BufferedImage screenshotJpg =
	// imageService.convertPngToJpg(screenshotPng);
	// sw.stop();
	// sw.start("setWallpaper");
	// osService.setWallpaper(screenshotJpg);
	// sw.stop();
	// System.out.println("Table describing all tasks performed :\n" +
	// sw.prettyPrint());
	// log.info("Completed setting new wallpaper at {}", dateFormat.format(new
	// Date()));
	// }
	// }

}
