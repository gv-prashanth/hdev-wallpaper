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
	@Value("${com.vadrin.hdev-wallpaper.useJNA}")
	private boolean useJNA;

	private static final Logger log = LoggerFactory.getLogger(HDEVController.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private static final String TEMPFILENAME = "hdev-wallpaper";

	private ProcessBuilder processBuilder = new ProcessBuilder();
	
	public File constructFile(BufferedImage screenshotJpg) throws IOException {
		log.info("Constructing file at {}", dateFormat.format(new Date()));
		File outputfile = File.createTempFile(TEMPFILENAME, "." + "jpg");
		ImageIO.write(screenshotJpg, "jpg", outputfile);
		log.info("Finished Constructing file at {}", dateFormat.format(new Date()));
		return outputfile;
	}

	public void setWallpaper(File outputfile) throws InterruptedException, IOException {
		log.info("Attempting to refresh wallpaper at {}", dateFormat.format(new Date()));
		if(useJNA) {
			Wallpaper.change(outputfile);
		}else {
			processBuilder.command(wallpaperChangerLocation, outputfile.getAbsolutePath(), "3");
			Process process = processBuilder.start();
			int exitCode = process.waitFor();
			if(exitCode != 0)
				throw new IOException();
		}
		log.info("Completed setting new wallpaper at {}", dateFormat.format(new Date()));
	}
	
	@Async
	public void deleteFile(File outputfile) throws IOException {
		long endTime = System.currentTimeMillis() + 300; // 1 second
		while(System.currentTimeMillis() < endTime)
			;
		boolean deleteCode = outputfile.delete();
		if(!deleteCode)
			throw new IOException();
	}

}
