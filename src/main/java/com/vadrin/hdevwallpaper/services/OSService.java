package com.vadrin.hdevwallpaper.services;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OSService {

	@Value("${com.vadrin.hdev-wallpaper.wallpaperChangerLocation}")
	private String wallpaperChangerLocation;

	public int setWallpaper(File imageFile) throws InterruptedException, IOException {
		Process process = new ProcessBuilder(wallpaperChangerLocation, imageFile.getAbsolutePath(), "3").start();
		int exitCode = process.waitFor();
		return exitCode;
	}

}
