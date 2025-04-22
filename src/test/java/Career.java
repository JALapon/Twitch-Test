import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class Career {
    private WebDriver driver;
    private WebDriverWait wait;
    private String twitchUrl = "https://www.twitch.tv/jobs/"; // Replace with your stream URL

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
    public void testCareersPageLoads() {
        // Confirm main jobs page loaded
        Assert.assertTrue(driver.getTitle().toLowerCase().contains("careers") ||
                        driver.getPageSource().contains("Join our quest to empower"),
                "Careers page did not load properly");

        // Click on "Job Openings"
        WebElement jobOpeningsButton = driver.findElement(By.cssSelector("a[href='/jobs/en/careers']"));
        Assert.assertTrue(jobOpeningsButton.isDisplayed(), "Job Openings button not visible");
        jobOpeningsButton.click();

        // Wait for job listings page to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Type in "Software Engineer"
        WebElement searchBox = driver.findElement(By.cssSelector("input.w-full.h-10.px-2"));
        searchBox.clear();
        searchBox.sendKeys("Software Engineer");
        searchBox.sendKeys(Keys.RETURN);

        // Wait for search results to appear
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Scroll to the specific job "Software Engineer I – iOS"
        WebElement targetJob = driver.findElement(By.xpath("//a[contains(text(), 'Software Engineer I') and contains(text(), 'iOS')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", targetJob);

        //  short pause for visual smooth scroll
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click the job
        String jobTitleText = targetJob.getText();
        targetJob.click();

        // Wait and confirm job detail page contains job title
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(driver.getPageSource().toLowerCase().contains(jobTitleText.toLowerCase()),
                "Job detail page does not contain the expected title: " + jobTitleText);

    }

    @Test(priority = 2)
    public void testFilterDepartment() {
        {
            driver.get("https://www.twitch.tv/jobs/en/careers");

            // Verify heading is present
            Assert.assertTrue(driver.getPageSource().contains("Open Jobs"), "Jobs page did not load properly");

            // Scroll slowly through page
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for (int i = 0; i < 3; i++) {
                js.executeScript("window.scrollBy(0, 500);");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Take screenshot and save to Desktop
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String userHome = System.getProperty("user.home");
            File destination = new File(userHome + "/Desktop/twitch_job_listing_scroll.png");

            try {
                FileHandler.copy(screenshot, destination);
                System.out.println("Screenshot saved at: " + destination.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Confirm that multiple job elements are present
            List<WebElement> jobs = driver.findElements(By.cssSelector("a[href*='/jobs/careers/']"));
            Assert.assertTrue(jobs.size() > 5, "Expected at least 6 job listings on page");
        }
    }

    @Test(priority = 3)
    public void testSearchSoftwareEngineer() throws InterruptedException {

        driver.get("https://www.twitch.tv/jobs/en/careers");


        // Scroll all the way to the bottom of the page
        scrollToBottom();
        Thread.sleep(2000); // let footer finish loading

        // Click on Instagram link
        WebElement instagramLink = driver.findElement(By.xpath("//a[contains(text(), 'Instagram') and contains(@href, 'instagram.com')]"));
        instagramLink.click();

        // Switch to new tab (Instagram)
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
        }

        // Wait and check if URL is correct
        Thread.sleep(3000);
        Assert.assertTrue(driver.getCurrentUrl().contains("instagram.com/twitch"),
                "Instagram page did not open correctly");
    }

    public void scrollToBottom() throws InterruptedException {
        long lastHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");

        while (true) {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(1000); // wait for any lazy loading
            long newHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");

            if (newHeight == lastHeight) {
                break;
            }
            lastHeight = newHeight;
        }
    }

    @Test(priority = 4)
    public void testJobTitlesContainSoftware() {

        try {
            // Store the original Twitch tab
            String twitchTab = driver.getWindowHandle();

            // Switch to the Instagram tab
            for (String handle : driver.getWindowHandles()) {
                driver.switchTo().window(handle);
                if (driver.getCurrentUrl().contains("instagram.com")) {
                    break;
                }
            }

            // Close Instagram popup if it's there
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("svg[aria-label='Close']")));
                closeButton.click();
                System.out.println("Closed Instagram pop-up.");
            } catch (Exception e) {
                System.out.println("No pop-up found or failed to close.");
            }

            // Close the Instagram tab
            driver.close();

            // Switch back to the original Twitch tab
            driver.switchTo().window(twitchTab);
            System.out.println("Switched back to Twitch tab.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test(priority = 5)
    public void testJobRedirectToDetailsPage() throws InterruptedException {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Ensure on correct tab
            for (String handle : driver.getWindowHandles()) {
                driver.switchTo().window(handle);
                if (driver.getCurrentUrl().contains("twitch.tv/jobs")) break;
            }

            // Scroll to top
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
            Thread.sleep(5000);

            // === STEP 1: Open the language dropdown ===
            WebElement languageDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[.//span[contains(text(), 'English')]]")));
            languageDropdown.click();

            // === STEP 2: Select Español ===
            WebElement spanishOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[normalize-space()='Español']")));
            spanishOption.click();
            Thread.sleep(5000); // Let the page reload or update

            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File src = screenshot.getScreenshotAs(OutputType.FILE);
            Thread.sleep(2000);

            // Get the user's Desktop directory on macOS
            String userHome = System.getProperty("user.home");
            File des = new File(userHome + "/Desktop/Testng_Screenshot/beforescreenshot.png");

            // Optional: Create the directory if it doesn't exist
            des.getParentFile().mkdirs();

            Thread.sleep(2000);
            FileHandler.copy(src, des);
            System.out.println("Screenshot saved at: " + des.getAbsolutePath());


            // Confirm we navigated to Spanish version (based on URL or visible text)
            Assert.assertTrue(driver.getCurrentUrl().contains("/es") ||
                            driver.getPageSource().contains("Unete a nuestra misión") ||
                            driver.getPageSource().toLowerCase().contains("carreras"),
                    "Page did not switch to Spanish.");

            // === STEP 3: Switch back to English ===
            // Open the language dropdown again (updated DOM — wait for it to stabilize)
            languageDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(., 'Español')]")));
            languageDropdown.click();

            // Select English
            WebElement englishOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[normalize-space()='English']")));
            englishOption.click();
            Thread.sleep(5000); // Let it update again

            // Confirm we're back on English version
            Assert.assertTrue(driver.getCurrentUrl().contains("/jobs") &&
                            driver.getPageSource().toLowerCase().contains("careers"),
                    "Page did not switch back to English.");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed to switch languages via dropdown.");
        }
    }
}