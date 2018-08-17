package com.vadrin.hdevwallpaper.controllers;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.vadrin.hdevwallpaper.services.BrowserService;
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
	BrowserService browserService;

	@Override
	public void run(String... args) throws Exception {
		log.info("Starting hdev-wallpaper at {}", dateFormat.format(new Date()));
		while (true) {
			log.info("Attempting to update wallpaper at {}", dateFormat.format(new Date()));
			BufferedImage screenshotPng = browserService.takePageScreenshot();
			BufferedImage screenshotJpg = imageService.convertPngToJpg(screenshotPng);
			osService.setWallpaper(screenshotJpg);
			log.info("Completed setting new wallpaper at {}", dateFormat.format(new Date()));
		}
	}

}
