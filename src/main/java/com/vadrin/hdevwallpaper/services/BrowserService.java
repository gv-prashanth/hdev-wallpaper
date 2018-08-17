package com.vadrin.hdevwallpaper.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
public class BrowserService {

	private WebDriver driver;

	private static final int MAX_WAIT_TIME = 5000;

	private void openBrowser() throws FileNotFoundException {
		FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions("--headless");
		File geckoDriver = ResourceUtils.getFile("classpath:execs/geckodriver.exe");
		System.setProperty("webdriver.gecko.driver", geckoDriver.getAbsolutePath());
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);
		driver = new FirefoxDriver(firefoxOptions);
	}

	private void openWebsite() {
		driver.get("https://www.ustream.tv/embed/17074538?html5ui");
		(new WebDriverWait(driver, MAX_WAIT_TIME)).until(ExpectedConditions.textToBePresentInElementLocated(
				By.xpath("//*[@id=\"playScreen\"]/div[2]"), "ISS HD Earth Viewing Experiment"));
		driver.findElement(By.xpath("//*[@id=\"playScreen\"]/div[1]")).click();
	}

	public BufferedImage takePageScreenshot() throws IOException {
		byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		ByteArrayInputStream bis = new ByteArrayInputStream(screenshot);
		return ImageIO.read(bis);
	}

	public BrowserService() throws FileNotFoundException {
		super();
		openBrowser();
		openWebsite();
	}

}
