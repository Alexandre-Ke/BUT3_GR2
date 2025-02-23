package com.iut.banque.test.facade;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestIconeSelenium {
    private WebDriver driver;
    private Map<String, Object> vars;
    private JavascriptExecutor js;

    @Before
    public void setUp() {

        System.setProperty("webdriver.chrome.driver", "C:\\Users\\nicof\\OneDrive\\Documents\\BUT_3\\BUT3_S5\\QualDev_BUT3_S5\\_00_ASBank2023\\src\\test\\java\\com\\iut\\banque\\test\\facade\\chromedriver.exe");

        driver = new ChromeDriver();
        js = (JavascriptExecutor) driver;
        vars = new HashMap<>();
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testIconPresence() {
        driver.get("http://localhost:8082/_00_ASBank2023/");
        driver.manage().window().setSize(new Dimension(1024, 768));

        try {

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement logo = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("img")));


            assertNotNull("Le logo de la page d'accueil n'est pas présent", logo);


            assertTrue("Le logo n'est pas affiché", logo.isDisplayed());

        } catch (TimeoutException e) {
            fail("Le logo n'a pas été trouvé sur la page après 10 secondes");
        }
    }
}
