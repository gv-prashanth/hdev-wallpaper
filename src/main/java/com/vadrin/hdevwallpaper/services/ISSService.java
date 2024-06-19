package com.vadrin.hdevwallpaper.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ISSService {

	private WebDriver driver;

	@Value("${com.vadrin.hdev-wallpaper.issUrl}")
	private String issUrl;
	@Value("${com.vadrin.hdev-wallpaper.browserDriverLocation}")
	private String browserDriverLocation;

	private static final String BROWSER_DRIVER = "webdriver.chrome.driver";
	private static final String COMMANDLINE_HEADLESS = "--headless";
	private static final int CACHE_FREQUENCY = 30000; // ms

	private void openBrowser() {
		String chromeDriverPath = browserDriverLocation ;
		System.setProperty(BROWSER_DRIVER, chromeDriverPath);
		ChromeOptions options = new ChromeOptions();
		options.addArguments(COMMANDLINE_HEADLESS, "--ignore-certificate-errors", "--mute-audio");
		driver = new ChromeDriver(options);
	}

	private void openWebsite() {
		driver.get(issUrl);
		(new WebDriverWait(driver, Duration.ofMillis(10000)))
				.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/canvas")));
	}

	@Cacheable(value = "issScreenshot")
	public BufferedImage takeScreenshot() throws IOException {
		WebElement element = driver.findElement(By.xpath("/html/body/canvas"));
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
	public void initializeService() {
		openBrowser();
		openWebsite();
	}

}
