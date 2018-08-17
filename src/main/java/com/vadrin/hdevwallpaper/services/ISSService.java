package com.vadrin.hdevwallpaper.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
public class ISSService {

	private WebDriver driver;

	@Value("${com.vadrin.hdev-wallpaper.timeout}")
	private int timeout;

	@Value("${com.vadrin.hdev-wallpaper.issUrl}")
	private String issUrl;

	private static final String GECKO_LOC = "classpath:execs/geckodriver.exe";
	private static final String GECKO_DRIVER = "webdriver.gecko.driver";
	private static final String COMMANDLINE_HEADLESS = "--headless";
	private static final int CACHE_FREQUENCY = 30000; // ms

	private void openBrowser() throws FileNotFoundException {
		FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions(COMMANDLINE_HEADLESS);
		File geckoDriver = ResourceUtils.getFile(GECKO_LOC);
		System.setProperty(GECKO_DRIVER, geckoDriver.getAbsolutePath());
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);
		driver = new FirefoxDriver(firefoxOptions);
	}

	private void openWebsite() {
		driver.get(issUrl);
		(new WebDriverWait(driver, timeout))
				.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"isst_map\"]")));
	}

	@Cacheable(value = "issScreenshot")
	public BufferedImage takeScreenshot() throws IOException {
		WebElement element = driver.findElement(By.xpath("//*[@id=\"isst_map\"]"));
		return getScreenshotOfElement(element);
	}

	@CacheEvict(value = "issScreenshot", allEntries = true)
	@Scheduled(fixedDelay = CACHE_FREQUENCY)
	public void refreshScreenshotCache() {
		// This method will remove all 'screenshots' from cache.
	}

	private BufferedImage getScreenshotOfElement(WebElement ele) throws IOException {
		BufferedImage eleScreenshot;
		// Get entire page screenshot
		byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		ByteArrayInputStream bis = new ByteArrayInputStream(screenshot);
		BufferedImage fullImg = ImageIO.read(bis);
		// Get width and height of the element
		int eleWidth = ele.getSize().getWidth();
		int eleHeight = ele.getSize().getHeight();
		// Get the location of element on the page
		Point point = ele.getLocation();
		// Crop the entire page screenshot to get only element screenshot
		eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
		return eleScreenshot;
	}

	@PostConstruct
	public void initializeService() throws FileNotFoundException {
		openBrowser();
		openWebsite();
	}

}
