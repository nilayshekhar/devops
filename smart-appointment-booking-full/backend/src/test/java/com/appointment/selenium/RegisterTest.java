package com.appointment.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegisterTest {

  private WebDriver driver;
  private WebDriverWait wait;

  private String testEmail;
  private String testPassword;

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
      throw new IllegalArgumentException("Unsupported browser: " + browser);
    }
    driver.manage().window().maximize();
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    testEmail = "registeruser_" + java.util.UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    testPassword = "registerpassword";
  }

  @Test
  public void testRegisterPageLoads() {
    driver.get("http://localhost:3000/register");
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("name")));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
  }

  @Test
  public void testRegistrationFunctionality() {
    driver.get("http://localhost:3000/register");
    driver.findElement(By.name("name")).sendKeys("Register User");
    driver.findElement(By.name("email")).sendKeys(testEmail);
    driver.findElement(By.name("password")).sendKeys(testPassword);
    try {
      driver.findElement(By.name("confirmPassword")).sendKeys(testPassword);
    } catch (Exception ignored) {}
    driver.findElement(By.cssSelector("button[type='submit']")).click();
    wait.until(ExpectedConditions.or(
      ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Login') or contains(text(),'Sign in')]")),
      ExpectedConditions.urlContains("/login")
    ));
    assertTrue(driver.getPageSource().contains("Login") || driver.getCurrentUrl().contains("/login"), "User should be redirected to Login after registration");
  }

  @AfterEach
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }
}
