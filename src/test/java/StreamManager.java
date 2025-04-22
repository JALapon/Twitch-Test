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

public class StreamManager {
    private WebDriver driver;
    private WebDriverWait wait;
    private String twitchStreamUrl = "https://www.twitch.tv/"; // Replace with your stream URL

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

    @Test (priority = 1)
    public void NavigateToPage() throws Exception {
        WebElement ProfileBtn = driver.findElement(By.xpath("//button[@data-a-target='user-menu-toggle' and @data-test-selector='user-menu__toggle']"));
        Thread.sleep(2000);
        ProfileBtn.click();
        Thread.sleep(2000);

        WebElement CreatorDashBtn = driver.findElement(By.xpath("//a[contains(@href, '/dashboard') and contains(., 'Creator Dashboard')]"));
        Thread.sleep(2000);
        CreatorDashBtn.click();
        Thread.sleep(2000);

        WebElement ManagerBtn = driver.findElement(By.xpath("//a[contains(@href, '/stream-manager') and contains(., 'Stream Manager')]"));
        Thread.sleep(2000);
        ManagerBtn.click();
        Thread.sleep(2000);

        TakesScreenshot managerpic = (TakesScreenshot) driver;
        File src = managerpic.getScreenshotAs(OutputType.FILE);
        Thread.sleep(1000);
        File des = new File(System.getProperty("user.home") + "/Downloads/managerpic.png");
        Thread.sleep(1000);
        FileHandler.copy(src, des);

        Thread.sleep(2000);
        Assert.assertTrue(des.exists(), "Screenshot was not taken");

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL: " + currentUrl);

    }



    @Test (priority = 2)
    public void SplitLineResize() throws InterruptedException {

        WebElement splitLine = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(@class, 'mosaic-split') and contains(@class, '-column')]/div[@class='mosaic-split-line']")));

        // Create Actions instance
        Actions actions = new Actions(driver);

        // Get element’s location (optional debug info)
        Point location = splitLine.getLocation();
        System.out.println("Split line starting location: " + location);

        // Drag the line upward (negative Y) or downward (positive Y)
        // For example: drag 100 pixels down
        actions.clickAndHold(splitLine)
                .moveByOffset(0, 50)  // move vertically: positive = down, negative = up
                .release()
                .perform();

        Thread.sleep(2000); // Optional pause to see the result
        System.out.println("Split line new location: " + location);

        //Horizontal Line
        // Wait for the split line to be visible (optional but safer)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement horizontalLine = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'mosaic-split') and contains(@class, '-row')]"))

        );

        // Create Actions instance
        Actions dragactions = new Actions(driver);

        //  Drag the line 100px to the right
        dragactions.clickAndHold(horizontalLine)
                .moveByOffset(100, 0)  // Right
                .release()
                .perform();

        Thread.sleep(1000); // Wait to see the effect

        //  Then drag it 50px to the left
        dragactions.clickAndHold(horizontalLine)
                .moveByOffset(-50, 0)  // Left
                .release()
                .perform();


    }

    @Test(priority = 3)
    public void DragToolbarLeft() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait for the container div that holds the h2 with text 'Audio Mixer'
        WebElement toolbarToDrag = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class, 'mosaic-window-toolbar') and contains(@class, 'draggable') and .//h2[text()='Collaboration']]")
        ));

        // Optional: confirm we found the correct toolbar by printing text
        WebElement header = toolbarToDrag.findElement(By.tagName("h2"));
        System.out.println("Dragging panel titled: " + header.getText());

        // Create Actions instance
        Actions actions = new Actions(driver);

        // Drag left for visibility
        actions.moveToElement(toolbarToDrag)
                .pause(Duration.ofMillis(500))
                .clickAndHold()
                .pause(Duration.ofMillis(500))
                .moveByOffset(-300, 0) // move 600 pixels to the left
                .pause(Duration.ofMillis(500))
                .release()
                .pause(Duration.ofMillis(500))
                .perform();

        Thread.sleep(1000); // Final wait to observe result
    }


    @Test(priority = 9)
    public void ChatUser() throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement TextBox = driver.findElement(By.xpath("//div[@data-a-target='chat-input' and @role='textbox']"));
        Thread.sleep(2000);

        WebElement ChatBtn = driver.findElement(By.xpath("//button[@data-a-target='chat-send-button']"));
        Thread.sleep(2000);

        TextBox.click();
        Thread.sleep(1000);

        String[] phrases = {
                "Let’s go!", "Wow", "Epic move!", "How’s everyone doing?",
                "Nice stream!", "Testing from Selenium", "This chat is awesome!",
                "Woooo!", "Subscribed ", "Stream quality is top tier!"
        };

        Actions actions = new Actions(driver);
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            // Pick a random message
            String msg = phrases[random.nextInt(phrases.length)];
            System.out.println("Sending message: " + msg);

            // Focus the chat box and type
            WebElement chatInput = wait.until(ExpectedConditions.elementToBeClickable(TextBox));
            actions.moveToElement(chatInput)
                    .click()
                    .sendKeys(msg)
                    .pause(Duration.ofMillis(300))
                    .perform();

            // Click the chat send button
            WebElement chatBtn = wait.until(ExpectedConditions.elementToBeClickable(ChatBtn));
            chatBtn.click();

            // Wait between messages
            Thread.sleep(1000);

        }



        // Find all usernames currently in chat
        List<WebElement> users = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//span[@data-a-target='chat-message-username']")
        ));

        // Click a random one (or change to users.get(0) to click the first)
        if (!users.isEmpty()) {
            WebElement randomUser = users.get(new Random().nextInt(users.size()));
            System.out.println("Clicking on user: " + randomUser.getText());
            randomUser.click();
            Thread.sleep(2000); // Wait to allow user card to load
        } else {
            System.out.println("No chat users found to click.");
        }

        WebElement MessageTab = driver.findElement(By.xpath("//button[@role='tab' and @data-action='0' and contains(@aria-label, 'Messages')]"));
        Thread.sleep(1000);

        MessageTab.click();
        Thread.sleep(1000);


        // Locate the scrollable container
        WebElement scrollContainer = driver.findElement(By.cssSelector("div.simplebar-scroll-content"));

        // Create JS executor
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Scroll down smoothly by 300 pixels
        js.executeScript("arguments[0].scrollBy({ top: 300, behavior: 'smooth' })", scrollContainer);
        Thread.sleep(2000); // Wait for the scroll to finish

        // Scroll down again
        js.executeScript("arguments[0].scrollBy({ top: 300, behavior: 'smooth' })", scrollContainer);
        Thread.sleep(2000);

        // Scroll up smoothly by 300 pixels
        js.executeScript("arguments[0].scrollBy({ top: -300, behavior: 'smooth' })", scrollContainer);
        Thread.sleep(2000);

        MessageTab.click();



    }

    @Test(priority = 10, description = "Extract and validate chatter viewer card details")
    public void ChatterDetails() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait for the viewer card to appear
        WebElement viewerCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("VIEWER_CARD_ID")));
        Assert.assertTrue(viewerCard.isDisplayed(), "Viewer card is not visible.");

        // Extract Username
        WebElement usernameLink = viewerCard.findElement(By.xpath(".//a[contains(@href, 'twitch.tv/')]"));
        String username = usernameLink.getText();
        System.out.println("Username: " + username);
        Assert.assertFalse(username.isEmpty(), "Username is empty.");

        // Extract Account Creation Date
        WebElement creationDate = viewerCard.findElement(By.xpath(".//p[contains(text(), 'Account Created')]"));
        String accountDate = creationDate.getText();
        System.out.println("Account Creation Date: " + accountDate);
        Assert.assertTrue(accountDate.contains("Account Created"), "Account creation date not found.");

        // Extract Message Count
        WebElement messagesTab = viewerCard.findElement(By.xpath(".//button[contains(@aria-label, 'Messages')]"));
        String messageCount = messagesTab.getAttribute("aria-label");
        System.out.println("Messages Info: " + messageCount);
        Assert.assertTrue(messageCount.matches(".*\\d+.*"), "Message count not found in aria-label.");

        // Extract Mod Actions Info
        WebElement modActionsTab = viewerCard.findElement(By.xpath(".//button[contains(@aria-label, 'Mod Actions')]"));
        String modActions = modActionsTab.getAttribute("aria-label");
        System.out.println("Mod Actions: " + modActions);
        Assert.assertTrue(modActions.contains("Mod Actions"), "Mod Actions label is incorrect.");

        // Extract all badges (optional)
        List<WebElement> badges = viewerCard.findElements(By.xpath(".//img[@aria-label]"));
        System.out.print("Badges: ");
        for (WebElement badge : badges) {
            String badgeLabel = badge.getAttribute("aria-label");
            System.out.print(badgeLabel + " ");
            Assert.assertNotNull(badgeLabel, "Badge label is null.");
        }
        System.out.println();
    }
}