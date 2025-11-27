package com.appointment.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HomePageTest {

  private WebDriver driver;

  @BeforeEach
  public void setUp() {
    String browser = System.getProperty("browser", "chrome");

    if (browser.equalsIgnoreCase("chrome")) {
      WebDriverManager.chromedriver().setup();
      driver = new ChromeDriver();
    } else if (browser.equalsIgnoreCase("firefox")) {
      WebDriverManager.firefoxdriver().setup();
      driver = new FirefoxDriver();
    } else {
      throw new IllegalArgumentException("Browser not supported: " + browser);
    }

    driver.manage().window().maximize();
  }

  @Test
  public void testHomePageLoads() {
    driver.get("http://localhost:3000/");
    String title = driver.getTitle();
    assertTrue(title != null && !title.isEmpty(), "Home page title should not be empty");
  }

  @AfterEach
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }
}
