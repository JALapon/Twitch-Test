import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Random;

public class Stream {
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



    @Test(priority = 1, description = "Scroll Twitch homepage and click a random livestream")
    public void Streamers() throws InterruptedException {
        driver.get("https://www.twitch.tv/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Actions actions = new Actions(driver);

        // Wait for page to partially load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        // Scroll down to load streams
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 1000);");
        Thread.sleep(3000); // Allow time for thumbnails to appear

        // Find all visible livestream links (typically in <a> tags with hrefs leading to channels)
        List<WebElement> streams = driver.findElements(By.xpath("//a[contains(@href, '/')]"));

        // Filter only visible and likely stream links (exclude categories, etc.)
        streams.removeIf(el -> !el.isDisplayed() || el.getAttribute("href").contains("/directory/"));

        Assert.assertTrue(streams.size() > 0, "No livestreams found on homepage!");

        // Pick a random stream
        Random rand = new Random();
        WebElement randomStream = streams.get(rand.nextInt(streams.size()));

        // Scroll to it and click
        actions.moveToElement(randomStream).perform();
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView()", randomStream);
        Thread.sleep(1000);
        randomStream.click();

        // Wait for navigation to stream page
        Thread.sleep(3000); // Could be replaced with URL wait

        // Assert you’re on a new URL (channel page)
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("twitch.tv") && !currentUrl.equals("https://www.twitch.tv/"),
                "Failed to navigate to a livestream. Current URL: " + currentUrl);

        System.out.println("Successfully opened a random stream: " + currentUrl);
    }



    @Test (priority = 2)
    public void StreamPlayFunc() throws InterruptedException {

        WebElement PauseBtn = driver.findElement(By.xpath("//*[@id=\"channel-player\"]/div/div[1]/div[1]"));
        Thread.sleep(2000);
        PauseBtn.click();


        Actions actions = new Actions(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Step 1: Hover over the mute button to reveal the slider
        WebElement muteButton = driver.findElement(By.cssSelector("button[data-a-target='player-mute-unmute-button']"));
        actions.moveToElement(muteButton).perform();

        // Step 2: Wait for slider to appear
        WebElement slider = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[data-a-target='player-volume-slider']")));

        // Step 3: Move the slider thumb to far left (volume 0)
        // Get the size of the slider and move to left edge
        int sliderWidth = slider.getSize().width;
        actions.clickAndHold(slider)
                .moveByOffset(-sliderWidth / 2, 0) // move left half width (to 0%)
                .release()
                .perform();


        Thread.sleep(1000);

        System.out.println("Slider value after move: " + slider.getAttribute("value"));

    }

    @Test (priority = 3)
    public void Chatting() throws InterruptedException {
        Thread.sleep(10000);
        WebElement Chatbox = driver.findElement(By.xpath("//div[@data-a-target='chat-input' and @contenteditable='true']"));
        Thread.sleep(10000);
        Chatbox.click();
        Thread.sleep(10000);

        List<WebElement> okButtons = driver.findElements(By.xpath("//*[contains(text(),'Okay, Got It')]"));
        if (!okButtons.isEmpty()) {
            okButtons.get(0).click();
        }


        Chatbox.sendKeys("Fire Stream");
        Thread.sleep(3000);
        WebElement ChatBtn = driver.findElement(By.xpath("//*[@id=\"live-page-chat\"]/div/div/div[2]/div/div/section/div/div[6]/div[2]/div[2]/div[2]/div[3]/div/button/div"));
        Thread.sleep(3000);
        ChatBtn.click();

        Thread.sleep(5000);
        WebElement closeModalButton = driver.findElement(By.xpath("//button[@data-a-target='modalClose']"));
        closeModalButton.click();

    }

    @Test (priority = 4)
    public void StreamStats() throws InterruptedException {
        Thread.sleep(5000);
        WebElement StreamTitle = driver.findElement(By.xpath("//*[@id=\"live-channel-stream-information\"]/div/div/div[2]/div[2]/div[2]/div[1]/div/div[1]"));
        Thread.sleep(10000);
        System.out.println(StreamTitle.getText());
        List<WebElement> StreamerInfo = driver.findElements(By.xpath("//*[@id=\"live-channel-about-panel\"]/div"));
        for (WebElement element : StreamerInfo) {
            System.out.println(element.getText());
        }


        WebElement livetime = driver.findElement(By.className("live-time"));
        System.out.println(livetime.getText());
        Thread.sleep(10000);
        WebElement Watchers = driver.findElement(By.xpath("//*[@id=\"live-channel-stream-information\"]/div/div/div[2]/div[2]/div[2]/div[2]/div/div/div[1]/div[1]/div/p[2]"));
        System.out.println(Watchers.getText());

    }

    @Test (priority = 5)
    public void HideChat() throws InterruptedException, IOException {

        //Take screenshot and save to location
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        File src = screenshot.getScreenshotAs(OutputType.FILE);
        Thread.sleep(2000);
        File des = new File(System.getProperty("user.home") + "/Downloads/beforescreenshot.png");
        Thread.sleep(2000);
        FileHandler.copy(src, des);

        //Confirm it screenshot is taken
        Assert.assertTrue(des.exists(), "Screenshot was not taken");
        //scroll chat and unpause
        WebElement collapseChatButton = driver.findElement(By.xpath("//button[@data-a-target='right-column__toggle-collapse-btn']"));
        collapseChatButton.click();

        //Take screenshot and save to location
        TakesScreenshot screenshotafter = (TakesScreenshot) driver;
        File src2 = screenshotafter.getScreenshotAs(OutputType.FILE);
        Thread.sleep(2000);
        File des2 = new File(System.getProperty("user.home") + "/Downloads/afterscreenshot.png");
        Thread.sleep(2000);
        FileHandler.copy(src2, des2);

        //Confirm it screenshot is taken
        Assert.assertTrue(des.exists(), "Screenshot was not taken");

    }


}
