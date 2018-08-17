package com.vadrin.hdevwallpaper.controllers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

	WebDriver driver;

	private static final int MAX_WAIT_TIME = 5000;

	@Override
	public void run(String... args) throws Exception {
		log.info("Starting hdev-wallpaper at {}", dateFormat.format(new Date()));
		while (true) {
			log.info("Attempting to update wallpaper at {}", dateFormat.format(new Date()));
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			imageService.convertPngToJpg(screenshot);
			//imageService.resizeImage(screenshot);
			osService.setWallpaper(screenshot);
			screenshot.delete();
			log.info("Completed setting new wallpaper at {}", dateFormat.format(new Date()));
		}
	}

	private void waitTillBrowserLoads() {
		driver.get("https://www.ustream.tv/embed/17074538?html5ui");
		(new WebDriverWait(driver, MAX_WAIT_TIME)).until(ExpectedConditions.textToBePresentInElementLocated(
				By.xpath("//*[@id=\"playScreen\"]/div[2]"), "ISS HD Earth Viewing Experiment"));
		driver.findElement(By.xpath("//*[@id=\"playScreen\"]/div[1]")).click();
	}

	public HDEVController() {
		super();
		FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions("--headless");
		System.setProperty("webdriver.gecko.driver",
				"D:\\Personal\\stsworkspace\\hdev-wallpaper\\src\\main\\resources\\execs\\geckodriver.exe");
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);
		driver = new FirefoxDriver(firefoxOptions);
		waitTillBrowserLoads();
	}

}
