import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;


public class BrowseCategories {
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

    @Test(priority = 1)
    public void testHomePageLoads() throws InterruptedException {
        // Click the "Browse" tab
        WebElement browseTab = driver.findElement(By.xpath("//a[@data-test-selector='top-nav__browse-link']"));
        browseTab.click();
        Thread.sleep(2000); // 👀 Short pause to see tab click

        // Wait for Browse page to load
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//img[@alt='World of Warcraft cover image']"))
        );

        // Scroll to and click World of Warcraft
        WebElement warcraftGame = driver.findElement(By.xpath("//img[@alt='World of Warcraft cover image']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", warcraftGame);
        Thread.sleep(2000); // 👀 Pause to see scroll
        warcraftGame.click();
        Thread.sleep(2000); // 👀 Pause after click

        // Wait for game page and first stream preview to appear
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@class, 'preview-card-image-link')]"))
        );

        // Click on the first available stream
        WebElement firstStream = driver.findElement(By.xpath("(//a[contains(@class, 'preview-card-image-link')])[1]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", firstStream);
        Thread.sleep(2000); // 👀 Pause before clicking first stream
        firstStream.click();
        Thread.sleep(2000); // 👀 Pause to observe stream load

        //  Assert that we navigated to a stream page
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.urlContains("twitch.tv")
        );
        Assert.assertTrue(driver.getCurrentUrl().matches("https://www\\.twitch\\.tv/.+"),
                "Expected to be on a stream page.");
    }

    @Test(priority = 2)
    public void testLiveChannels() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click the Browse tab
        WebElement browseTab = driver.findElement(By.xpath("//a[@data-test-selector='top-nav__browse-link']"));
        Assert.assertTrue(browseTab.isDisplayed(), "Browse tab should be visible on homepage");
        browseTab.click();
        Thread.sleep(1000); // 👀 Wait for Browse page to load

        //  Click the Live Channels tab
        WebElement liveChannelsTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@data-test-selector='browse-header-tab-live-channels']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", liveChannelsTab);
        Thread.sleep(1000);
        liveChannelsTab.click();
        Thread.sleep(1000);

        Assert.assertTrue(driver.getCurrentUrl().contains("/directory/all"),
                "Expected to be on the Live Channels page.");

        // Click the search box
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@id='dropdown-search-input']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchBox);
        Thread.sleep(1000);
        searchBox.click();

        // Type Minecraft and press Enter
        searchBox.sendKeys("Minecraft");
        Thread.sleep(1000); // Let the text appear
        searchBox.sendKeys(Keys.ENTER); // 🔑 Press Enter
        Thread.sleep(2000); // 👀 Wait for search results to update

        // Click on the first video
        WebElement firstVideo = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//a[contains(@class, 'preview-card-image-link')])[1]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", firstVideo);
        Thread.sleep(1000);
        firstVideo.click();

        // Confirm stream page is loaded
        Thread.sleep(2000);
        Assert.assertTrue(driver.getCurrentUrl().contains("twitch.tv"),
                "Expected to be on a Twitch stream page.");


    }

    @Test(priority = 3)
    public void testClickBrowseTab() throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        //  Click the Browse tab
        WebElement browseTab = driver.findElement(By.xpath("//a[@data-test-selector='top-nav__browse-link']"));
        browseTab.click();
        Thread.sleep(1000); // 👀 Let the Browse page load

        // Validate we’re on the /directory page
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/directory"),
                "After clicking Browse, URL should contain '/directory'");

        // Locate the main search bar at the top of the page
        WebElement mainSearchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@placeholder='Search']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", mainSearchInput);
        Thread.sleep(1000); // 👀 Show the field
        mainSearchInput.click();

        // Type "roblox" and press Enter
        mainSearchInput.sendKeys("roblox");
        Thread.sleep(1000);
        mainSearchInput.sendKeys(Keys.ENTER);
        Thread.sleep(2000); // 👀 Wait for search results to show

        // roblox-related search result page
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("roblox"),
                "Expected URL to contain 'roblox' after searching.");
    }

    @Test(priority = 4)
    public void testBrowseTabURL() throws IOException, InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click Inbox (Prime Offers) button
        WebElement inboxButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@data-a-target='prime-offers-icon']")));
        inboxButton.click();
        Thread.sleep(1000);

        // Click the three dots (More Options) button
        WebElement moreOptionsButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@data-a-target='ellipsis-button']")));
        moreOptionsButton.click();
        Thread.sleep(1000);

        // Click the Blog option
        WebElement blogLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@data-a-target='blog-link']")));
        blogLink.click();

        // Switch to the new tab (Blog)
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
        }

        // Wait for page to load
        Thread.sleep(2000);

        //  Scroll to bottom of the page
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(2000); // 👀 Let it scroll visibly

        // Scroll back to top
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
        Thread.sleep(2000); // 👀 Let it scroll back

        //  Take a screenshot after scrolling back up
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destination = new File("twitch_blog_page_scrolled.png");
        Files.copy(screenshot.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

        System.out.println(" Screenshot saved at: " + destination.getAbsolutePath());

        //  Close the blog tab
        driver.close();

        //  Switch back to the original Twitch tab
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
        }

        //  Navigate to Twitch homepage (if needed)
        driver.get("https://www.twitch.tv/");

        // Optional: Wait until homepage is loaded (e.g., wait for some homepage element)
        WebElement homepageBanner = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[@data-a-target='home-link']"))); // or another reliable homepage element



    }

    @Test(priority = 5)
    public void testBrowseToLiveChannelThenCategoryGame() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Click the Browse tab
        WebElement browseTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@data-test-selector='top-nav__browse-link']")));
        browseTab.click();
        Thread.sleep(1000);
        Assert.assertTrue(driver.getCurrentUrl().contains("/directory"),
                "Should land on /directory after clicking Browse.");

        // Switch to Live Channels tab
        WebElement liveTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@data-test-selector='browse-header-tab-live-channels']")));
        liveTab.click();
        Thread.sleep(1000);
        Assert.assertTrue(driver.getCurrentUrl().contains("/directory/all"),
                "Should be on Live Channels page.");

        //  Scroll down to load more streams
        js.executeScript("window.scrollBy(0, 1000);");
        Thread.sleep(2000);
        js.executeScript("window.scrollBy(0, 1000);");
        Thread.sleep(2000);

        // Click one of the later stream previews
        WebElement midStream = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//a[contains(@class, 'preview-card-image-link')])[5]")));
        midStream.click();
        Thread.sleep(3000);

        //  Assert you're on a stream page
        Assert.assertTrue(driver.getCurrentUrl().contains("twitch.tv"),
                "Expected to be on a Twitch stream page.");

        //  Go back to Browse
        driver.navigate().back();
        Thread.sleep(2000);
        driver.navigate().back(); // May need twice if stream redirected
        Thread.sleep(2000);

        //  Switch to Categories tab
        WebElement categoriesTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@data-test-selector='browse-header-tab-categories']")));
        categoriesTab.click();
        Thread.sleep(2000);

        //  Click on a known category/game (e.g., Fortnite)
        WebElement fortniteCard = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//img[@alt='Fortnite cover image']")));
        js.executeScript("arguments[0].scrollIntoView(true);", fortniteCard);
        Thread.sleep(1000);
        fortniteCard.click();

        // Validate you’re on Fortnite’s game page
        Thread.sleep(2000);
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("fortnite"),
                "Expected to be on the Fortnite game page.");
    }

}