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
import java.time.Duration;
import java.util.List;
import java.util.Random;

public class Moderate {

    private WebDriver driver;
    private WebDriverWait wait;
    private String twitchStreamUrl = "https://www.twitch.tv/softwaretesting2025"; // Replace with your stream URL
    private String testUserName = "softwaretesting2025"; // Replace with a test username.
    private int defaultTimeout = 10;

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
        driver.get(twitchStreamUrl);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1)
    public void testChangeChatMode() throws InterruptedException {
        System.out.println("Running Test 1: Change Chat Mode");
        Thread.sleep(6000);

        // Selects the chat
        driver.findElement(By.cssSelector("[data-a-target='channel-home-tab-Chat']")).click();
        Thread.sleep(5000);


        // Selects the chat
        WebElement expandChatButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[aria-label='Expand Chat']")
        ));

        // Click the button
        expandChatButton.click();

        // Types in Chat box
        driver.findElement(By.cssSelector("[data-a-target='chat-input-text']")).sendKeys("/slow 10");
        Thread.sleep(2000);

        // Send Message
        driver.findElement(By.cssSelector("[data-a-target='chat-send-button']")).click();
        Thread.sleep(5000);

        // Types in Chat box
        driver.findElement(By.cssSelector("[data-a-target='chat-input-text']")).sendKeys("/slowoff");
        Thread.sleep(2000);

        // Send Message
        driver.findElement(By.cssSelector("[data-a-target='chat-send-button']")).click();
        Thread.sleep(5000);
    }

    @Test(priority = 2)
    public void testDeleteMessage() throws InterruptedException {
        System.out.println("Running Test 2: Delete Message");
        Thread.sleep(5000);

        WebElement chatContainer = driver.findElement(By.cssSelector("[data-test-selector='chat-scrollable-area__message-container']"));
        List<WebElement> messages = chatContainer.findElements(By.cssSelector("div"));

        // Scroll up to make sure all messages are loaded
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollTo({ top: 0, behavior: 'smooth' });", chatContainer
        );

        Thread.sleep(4000);

        String targetUsername = "uhfed";
        for (WebElement message : messages) {
            List<WebElement> spans = message.findElements(By.cssSelector("span[data-a-user='" + targetUsername + "']"));
            if (!spans.isEmpty()) {
                WebElement usernameElement = spans.get(0);

                // Delete the message
                WebElement deleteButton = driver.findElement(By.cssSelector("button[aria-label='Delete message']"));
                deleteButton.click();
                Thread.sleep(3000);

                break;
            }
        }
    }

    @Test(priority = 3)
    public void testTimeoutUser() throws InterruptedException {
        System.out.println("Running Test 3: Timeout User");
        Thread.sleep(5000);

        WebElement chatContainer = driver.findElement(By.cssSelector("[data-test-selector='chat-scrollable-area__message-container']"));
        List<WebElement> messages = chatContainer.findElements(By.cssSelector("div"));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollTo({ top: 0, behavior: 'smooth' });", chatContainer
        );

        Thread.sleep(4000);

        String targetUsername = "uhfed";
        for (WebElement message : messages) {
            List<WebElement> spans = message.findElements(By.cssSelector("span[data-a-user='" + targetUsername + "']"));
            if (!spans.isEmpty()) {
                // Found the message from the target user
                WebElement usernameElement = spans.get(0);

                // Right-click to open moderation menu
                usernameElement.click();
                // Wait for the moderation dropdown to appear
                Thread.sleep(5000);

                // Time out
                driver.findElement(By.cssSelector("button[aria-label='Timeout uhFed']")).click();
                Thread.sleep(5000);

                // Untime out
                driver.findElement(By.cssSelector("button[aria-label='Untimeout uhfed']")).click();
                Thread.sleep(5000);

                // Hide popup
                driver.findElement(By.cssSelector("button[aria-label='Hide']")).click();
                Thread.sleep(2000);

                break;
            }
        }

    }

    @Test(priority = 4)
    public void testClearChat() throws InterruptedException {
        System.out.println("Running Test 4: Clear Chat");
        Thread.sleep(5000);

        // Types in Chat box
        driver.findElement(By.cssSelector("[data-a-target='chat-input-text']")).sendKeys("/clear");
        Thread.sleep(2000);

        // Send Message
        driver.findElement(By.cssSelector("[data-a-target='chat-send-button']")).click();
        Thread.sleep(5000);
    }

    @Test(priority = 5)
    public void testBanUser() throws InterruptedException {
        System.out.println("Running Test 5: Ban User");
        Thread.sleep(10000);

        // Click  Community Tab
        driver.findElement(By.cssSelector("button[aria-label='Community']")).click();
        Thread.sleep(5000);

        WebElement button = driver.findElement(By.cssSelector("button[aria-label='Open details for Uhfed']"));

        Actions actions = new Actions(driver);
        actions.moveToElement(button).perform(); // Hover over the item
        Thread.sleep(5000);

        driver.findElement(By.cssSelector("button[aria-label='Ban Uhfed']")).click();
        Thread.sleep(5000);

        driver.findElement(By.cssSelector("button[aria-label='Unban Uhfed']")).click();
        Thread.sleep(5000);

    }
}
