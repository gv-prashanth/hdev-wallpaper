package com.vadrin.hdevwallpaper.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HDEVService {

	private WebDriver driver;

	@Value("${com.vadrin.hdev-wallpaper.hdevStreamUrl}")
	private String hdevStreamUrl;
	@Value("${com.vadrin.hdev-wallpaper.screenWidth}")
	private int screenWidth;
	@Value("${com.vadrin.hdev-wallpaper.screenHeight}")
	private int screenHeight;
	@Value("${com.vadrin.hdev-wallpaper.scale}")
	private int scale;
	@Value("${com.vadrin.hdev-wallpaper.browserDriverLocation}")
	private String browserDriverLocation;

	private static final String BROWSER_DRIVER = "webdriver.chrome.driver";
	private static final String COMMANDLINE_HEADLESS = "--headless";

	private void openBrowser() {
		String chromeDriverPath = browserDriverLocation ;
		System.setProperty(BROWSER_DRIVER, chromeDriverPath);
		ChromeOptions options = new ChromeOptions();
		options.addArguments(COMMANDLINE_HEADLESS, "--window-size="+(screenWidth/scale)+","+(screenHeight/scale),"--ignore-certificate-errors", "--mute-audio");  
		driver = new ChromeDriver(options);
		Dimension d = new Dimension(screenWidth / scale, screenHeight / scale);
		driver.manage().window().setSize(d);
	}

	private void openWebsite() {
		driver.get(hdevStreamUrl);
		driver.findElement(By.xpath("/html/body")).click();
	}

	public BufferedImage takeScreenshot() throws IOException {
		byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		ByteArrayInputStream bis = new ByteArrayInputStream(screenshot);
		return ImageIO.read(bis);
	}

	@PostConstruct
	public void initializeService() {
		openBrowser();
		openWebsite();
	}

}
