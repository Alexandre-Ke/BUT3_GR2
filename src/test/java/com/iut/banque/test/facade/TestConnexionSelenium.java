package com.iut.banque.test.facade;

import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class TestConnexionSelenium {
    private WebDriver driver;
    private Map<String, Object> vars;
    private WebDriverWait wait;

    @Before
    public void setUp() {

        System.setProperty("webdriver.chrome.driver", "C:\\Users\\nicof\\OneDrive\\Documents\\BUT_3\\BUT3_S5\\QualDev_BUT3_S5\\_00_ASBank2023\\src\\test\\java\\com\\iut\\banque\\test\\facade\\chromedriver.exe");

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Attente explicite de 10 sec
        vars = new HashMap<>();
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testConnexion() {
        try {
            driver.get("http://localhost:8082/_00_ASBank2023/");


            assertEquals("ASBank - Accueil", driver.getTitle());


            WebElement loginPageLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Page de Login")));
            loginPageLink.click();


            WebElement userField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("controller_Connect_login_action_userCde")));
            userField.sendKeys("admin");

            WebElement passwordField = driver.findElement(By.id("controller_Connect_login_action_userPwd"));
            passwordField.sendKeys("adminpass");

            WebElement submitButton = driver.findElement(By.id("controller_Connect_login_action_submit"));
            submitButton.click();


            WebElement welcomeMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("welcomeMessage")));
            assertTrue("Le message de bienvenue n'est pas affiché", welcomeMessage.isDisplayed());

            System.out.println("✅ Test réussi : L'utilisateur est bien connecté.");

        } catch (TimeoutException e) {
            fail("⛔ Échec du test : Un élément n'a pas été trouvé à temps.");
        } catch (Exception e) {
            fail("⛔ Erreur inattendue : " + e.getMessage());
        }
    }
}
