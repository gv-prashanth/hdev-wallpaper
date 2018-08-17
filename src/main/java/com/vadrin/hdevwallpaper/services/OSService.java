package com.vadrin.hdevwallpaper.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

@Service
public class OSService {

	private static final String USER32 = "user32";
	private static final String TEMPFILENAME = "hdev-wallpaper";

	public void setWallpaper(BufferedImage image) throws IOException {
		File tempFile = File.createTempFile(TEMPFILENAME, ".jpg");
		ImageIO.write(image, "jpg",  tempFile);
		User32.INSTANCE.SystemParametersInfo(0x0014, 0, tempFile.getAbsolutePath(), 1);
		tempFile.delete();
	}

	public static interface User32 extends Library {
		User32 INSTANCE = (User32) Native.loadLibrary(USER32, User32.class, W32APIOptions.DEFAULT_OPTIONS);

		boolean SystemParametersInfo(int one, int two, String s, int three);
	}

}
