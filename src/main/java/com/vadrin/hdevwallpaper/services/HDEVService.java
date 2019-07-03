package com.vadrin.hdevwallpaper.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HDEVService {

	private WebDriver driver;

	@Value("${com.vadrin.hdev-wallpaper.timeout}")
	private int timeout;
	@Value("${com.vadrin.hdev-wallpaper.hdevStreamUrl}")
	private String hdevStreamUrl;
	@Value("${com.vadrin.hdev-wallpaper.screenWidth}")
	private int screenWidth;
	@Value("${com.vadrin.hdev-wallpaper.screenHeight}")
	private int screenHeight;
	@Value("${com.vadrin.hdev-wallpaper.geckoLocation}")
	private String geckoLocation;

	// private static final String GECKO_LOC =
	// "classpath:execs/geckodriver.exe";
	private static final String GECKO_DRIVER = "webdriver.gecko.driver";
	private static final String COMMANDLINE_HEADLESS = "--headless";
	private static final String TRIGGER_TEXT = "ISS HD Earth Viewing Experiment";

	private void openBrowser() throws FileNotFoundException {
		FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions(COMMANDLINE_HEADLESS);
		File geckoDriver = new File(geckoLocation);
		System.setProperty(GECKO_DRIVER, geckoDriver.getAbsolutePath());
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);
		driver = new FirefoxDriver(firefoxOptions);
		Dimension d = new Dimension(screenWidth / 2, screenHeight / 2);
		driver.manage().window().setSize(d);
	}

	private void openWebsite() {
		driver.get(hdevStreamUrl);
		(new WebDriverWait(driver, timeout)).until(
				ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), '" + TRIGGER_TEXT + "')]")));
		driver.findElement(By.xpath("//*[contains(text(), '" + TRIGGER_TEXT + "')]")).click();
	}

	public BufferedImage takeScreenshot() throws IOException {
		byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		ByteArrayInputStream bis = new ByteArrayInputStream(screenshot);
		return ImageIO.read(bis);
	}

	@PostConstruct
	public void initializeService() throws FileNotFoundException {
		openBrowser();
		openWebsite();
	}

}
