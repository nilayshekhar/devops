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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest {

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
  }

  @Test
  public void testRegisterPageLoads() {
    driver.get("http://localhost:3000/register");
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Create Account')]")));
    wait.until(ExpectedConditions.visibilityOfElementLocated(
      By.xpath("//*[contains(text(), 'Sign up to start booking appointments')]")));
  }

  @Test
  public void testRegistrationFunctionality() {
    driver.get("http://localhost:3000/register");

    driver.findElement(By.name("name")).sendKeys("Test User");
    testEmail = "newuser_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    driver.findElement(By.name("email")).sendKeys(testEmail);
    driver.findElement(By.name("password")).sendKeys("newpassword");
    driver.findElement(By.cssSelector("button[type='submit']")).click();

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Login')]")));

    assertTrue(driver.getPageSource().contains("Login"), "User should be redirected to Login after registration");
  }

  @Test
  public void testLoginPageLoads() {
    driver.get("http://localhost:3000/login");
    assertTrue(driver.getPageSource().contains("Login"), "Login page should contain 'Login'");
  }

  @AfterEach
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }
}