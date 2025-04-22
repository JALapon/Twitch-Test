import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Set;

public class HomePage {
    private WebDriver driver;
    private WebDriverWait wait;
    private String twitchUrl = "https://www.twitch.tv/"; // Replace with your stream URL

    @BeforeClass
    public void setupWithLogin() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--disable-extensions");

        // Use the real profile for the tests that need login
        chromeOptions.addArguments("user-data-dir=C://Users//Fed//AppData//Local//Google//Chrome//User Data");
        chromeOptions.addArguments("profile-directory=Default");

        driver = new ChromeDriver(chromeOptions);
        driver.manage().window().maximize();
        driver.get(twitchUrl);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Test 1: Verify that the Twitch homepage loads and the logo is visible
    @Test(priority = 1)
    public void verifyHomePageLoads() throws InterruptedException {
        wait.until(ExpectedConditions.titleContains("Twitch"));
        WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[@aria-label='Twitch Home']")));
        Assert.assertTrue(logo.isDisplayed());
        Thread.sleep(3000);

        String currentUrl = driver.getCurrentUrl();
        String pageTitle = driver.getTitle();
        System.out.println("Current URL: " + currentUrl);
        System.out.println("Page Title: " + pageTitle);
        Thread.sleep(3000);

        Dimension pageSize = driver.manage().window().getSize();
        System.out.println("Browser Window Size: " + pageSize);


        // List all web elements on the page (using a broad XPath selector)
        List<WebElement> allElements = driver.findElements(By.xpath("//*"));
        System.out.println("Total number of web elements on the page: " + allElements.size());
    }
    // Test 2: Rotate the carousel and click the visible channel
    @Test(priority = 2)
    public void testCarouselContentUpdate() throws InterruptedException {
        // Click the right arrow on the carousel three times with delays
        WebElement carouselNextButton = driver.findElement(By.xpath("/html/body/div[1]/div/div[1]/div/" +
                "main/div[1]/div[3]/div/div/div/div[1]/div[2]/div/div[2]/div/button"));
        for (int i = 0; i < 3; i++) {
            carouselNextButton.click();
            Thread.sleep(1000); // Wait for animation / content update
        }
        WebElement carouselPreviousButton = driver.findElement(By.cssSelector(".cpoPDg > div:nth-child(1)"));
        for (int i = 0; i < 5; i++) {
            carouselNextButton.click();
            Thread.sleep(1000); // Wait for animation / content update
        }
    }

    // Test 3: Click on the "Browse" button and verify the URL contains "/directory"
    @Test(priority = 3)
    public void clickBrowseAndCheckUrl() throws InterruptedException, IOException {
        // Wait for the Browse button to be clickable and then click it
        WebElement browse = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/directory')]")));
        browse.click();

        // Wait until the URL contains "/directory"
        wait.until(ExpectedConditions.urlContains("/directory"));

        // Assert that the current URL is correct
        Assert.assertTrue(driver.getCurrentUrl().contains("/directory"),
                "URL does not contain '/directory'. Current URL: " + driver.getCurrentUrl());

        // Optional pause to visually confirm navigation (can be removed in headless runs)
        Thread.sleep(3000);

        // Take a screenshot and save it to the specified location
        TakesScreenshot ts = (TakesScreenshot) driver;
        File sourceFile = ts.getScreenshotAs(OutputType.FILE);

        // Define the destination path and ensure directory exists
        Path destinationPath = Paths.get("C:\\screenshots\\browse_screenshot.png");
        Files.createDirectories(destinationPath.getParent()); // create dir if not exists

        // Copy the screenshot file to the destination
        Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
    }


    // Test 4: Navigate to the directory page and hover over the first category tile to verify visibility
    @Test(priority = 4)
    public void hoverOverCategoryTile() throws InterruptedException {
        driver.get("https://www.twitch.tv/directory");
        WebElement firstTile = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div[data-target='directory-first-item']")));
        Actions actions = new Actions(driver);
        actions.moveToElement(firstTile).perform();
        Assert.assertTrue(firstTile.isDisplayed());
        Thread.sleep(3000);

        // List all web elements on the page (using a broad XPath selector)
        List<WebElement> allElements = driver.findElements(By.xpath("//*"));
        System.out.println("Total number of web elements on the directory page: " + allElements.size());
    }
    //Test 5
    @Test(priority = 5)
    public void liveOnTwitch() throws InterruptedException {
        driver.get("https://twitch.tv/");
        Thread.sleep(3000);
        WebElement liveLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Live on Twitch')]")));
        liveLink.click();
        Thread.sleep(3000);

        WebElement Watchers = driver.findElement(By.xpath("/html/body/div[1]/div/div[1]/div/main/div[1]" +
                "/div[3]/div/div/div/div/section/div[4]"));
        System.out.println(Watchers.getText());


    }
}

