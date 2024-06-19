package com.vadrin.hdevwallpaper.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.vadrin.hdevwallpaper.controllers.HDEVController;

@Service
public class OSService {

	@Value("${com.vadrin.hdev-wallpaper.wallpaperChangerLocation}")
	private String wallpaperChangerLocation;

	private static final String TEMPFILENAME = "hdev-wallpaper";

	private static final Logger log = LoggerFactory.getLogger(HDEVController.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Async
	public void setWallpaper(BufferedImage image) throws InterruptedException, IOException {
		log.info("filecreate start at {}", dateFormat.format(new Date()));
		File outputfile = File.createTempFile(TEMPFILENAME, "." + "jpg");
		log.info("filecreate end at {}", dateFormat.format(new Date()));
		ImageIO.write(image, "jpg", outputfile);
		log.info("image write at {}", dateFormat.format(new Date()));
		Process process = new ProcessBuilder(wallpaperChangerLocation, outputfile.getAbsolutePath(), "3").start();
		log.info("process construct at {}", dateFormat.format(new Date()));
		int exitCode = process.waitFor();
		log.info("process exitCode at {}", dateFormat.format(new Date()));
		if(exitCode != 0)
			throw new IOException();
		boolean deleteCode = outputfile.delete();
		if(!deleteCode)
			throw new IOException();
	}

}
