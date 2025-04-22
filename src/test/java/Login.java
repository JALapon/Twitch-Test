import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;

import java.time.Duration;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Login {
    private WebDriver driver;
    private WebDriverWait wait;
    private String twitchUrl = "https://www.twitch.tv/"; // Replace with your stream URL
    private String username = "softwaretesting2025"; // Replace with a test username.
    private String password = "SoftwareTesting*";

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
    public void testInvalidUsername() throws InterruptedException {
        System.out.println("Running Test 1: Invalid Username");
        WebElement loginButton = driver.findElement(By.cssSelector(".Layout-sc-1xcs6mc-0.bFxzAY"));
        loginButton.click();
        Thread.sleep(2000);

        // Erase Username
        driver.findElement(By.id("login-username")).click();
        for (int i = 0; i < 5; i++) {  // Loop for enough backspace key presses
            driver.findElement(By.id("login-username")).sendKeys(Keys.BACK_SPACE);
        }

        // Erase Password
        driver.findElement(By.id("password-input")).click();
        for (int i = 0; i < 20; i++) {  // Loop for enough backspace key presses
            driver.findElement(By.id("password-input")).sendKeys(Keys.BACK_SPACE);
        }

        // Small Wait
        Thread.sleep(500);

        // Type in username
        driver.findElement(By.id("login-username")).sendKeys(username);
        Thread.sleep(500);

        // Type in password
        driver.findElement(By.id("password-input")).sendKeys(password);
        Thread.sleep(500);

        driver.findElement(By.cssSelector(".Layout-sc-1xcs6mc-0.gzKWOA")).click();
        // 10 Second wait to type in authentication Code
        Thread.sleep(2000);
    }

    @Test(priority = 2)
    public void testInvalidPassword() throws InterruptedException {
        System.out.println("Running Test 2: Invalid Password");

        driver.findElement(By.id("login-username")).click();
        for (int i = 0; i < 50; i++) {  // Loop for enough backspace key presses
            driver.findElement(By.id("login-username")).sendKeys(Keys.BACK_SPACE);
        }

        // Erase Password
        driver.findElement(By.id("password-input")).click();
        for (int i = 0; i < 5; i++) {  // Loop for enough backspace key presses
            driver.findElement(By.id("password-input")).sendKeys(Keys.BACK_SPACE);
        }

        // Small Wait
        Thread.sleep(500);

        // Type in username
        driver.findElement(By.id("login-username")).sendKeys(username);
        Thread.sleep(500);

        // Type in password
        driver.findElement(By.id("password-input")).sendKeys(password);
        Thread.sleep(500);

        driver.findElement(By.cssSelector(".Layout-sc-1xcs6mc-0.gzKWOA")).click();
        Thread.sleep(2000);
    }

    @Test(priority = 3)
    public void testPasswordReset() throws InterruptedException {
        System.out.println("Running Test 3: Password Reset");
        Thread.sleep(2000);

        // Save the current window handle (main tab)
        String mainWindowHandle = driver.getWindowHandle();

        // Wait for and click the "Trouble Signing In" link
        driver.findElement(By.cssSelector("a.ScCoreLink-sc-16kq0mq-0.UpjsG.tw-link")).click();
        Thread.sleep(5000);  // Wait for the new tab to open

        // Switch to the new tab (the second tab)
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(mainWindowHandle)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }

        // Click reset button
        WebElement resetButton = driver.findElement(By.cssSelector(".ScCoreButton-sc-ocjdkq-0.kRWonT"));
        resetButton.click();

        Thread.sleep(2000);

        // Type in email to send reset code
        WebElement emailInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input.ScInputBase-sc-vu7u7d-0.ScInput-sc-19xfhag-0")));
        emailInputField.sendKeys("uhfedlabs@gmail.com");
        Thread.sleep(2000);

        // Click send email
        driver.findElement(By.cssSelector(".ScCoreButton-sc-ocjdkq-0.iumXyx")).click();
        Thread.sleep(2000);

        // Type in username
        driver.findElement(By.cssSelector(".ScInputBase-sc-vu7u7d-0.ScInput-sc-19xfhag-0.gNGlOQ.cHTsdX.InjectLayout-sc-1i43xsx-0.eRDdjS.tw-input")).sendKeys(username);
        Thread.sleep(2000);

        // Select send reset password code
        driver.findElement(By.cssSelector("button.ScCoreButton-sc-ocjdkq-0.khjbBN")).click();
        Thread.sleep(2000);

        // Close the tab
        driver.switchTo().window(mainWindowHandle);
        Thread.sleep(3000);
    }

    @Test(priority = 4)
    public void testSuccessfulLogin() throws InterruptedException {
        System.out.println("Running Test 4: Successful Login");

        // Erase Username
        driver.findElement(By.id("login-username")).click();
        for (int i = 0; i < 50; i++) {  // Loop for enough backspace key presses
            driver.findElement(By.id("login-username")).sendKeys(Keys.BACK_SPACE);
        }

        // Erase Password
        driver.findElement(By.id("password-input")).click();
        for (int i = 0; i < 50; i++) {  // Loop for enough backspace key presses
            driver.findElement(By.id("password-input")).sendKeys(Keys.BACK_SPACE);
        }

        // Small wait after erasing
        Thread.sleep(500);

        // Type in username
        driver.findElement(By.id("login-username")).sendKeys(username);
        Thread.sleep(500);

        // Type in password
        driver.findElement(By.id("password-input")).sendKeys(password);
        Thread.sleep(500);

        // Click sign in button
        driver.findElement(By.cssSelector(".Layout-sc-1xcs6mc-0.gzKWOA")).click();
        // 10 Second wait to type in authentication Code
        Thread.sleep(30000);
    }


    @Test(priority = 5)
    public void testChangingBannerPicture() throws InterruptedException {
        System.out.println("Running Test 5: Change Banner Picture");

        Thread.sleep(8000);

        // Finds the little preview window of the first video playing
        WebElement element = driver.findElement(By.cssSelector(".InjectLayout-sc-1i43xsx-0.click-handler.zzTJm"));
        // Takes a screenshot
        File srcFile = element.getScreenshotAs(OutputType.FILE);
        // Saves it to downloads folder
        File destFile = new File(System.getProperty("user.home") + "/Downloads/partial_screenshot.png");

        try {
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace(); // or handle it however you want
        }

        Thread.sleep(2000);

        // Clicks profile
        driver.findElement(By.cssSelector("img.InjectLayout-sc-1i43xsx-0.gRlROF.tw-image.tw-image-avatar")).click();
        Thread.sleep(2000);

        // Navigate directly to the settings page
        driver.get("https://www.twitch.tv/settings/profile");

        // Wait for the page to load
        Thread.sleep(5000);

        // Select Upload Banner
        driver.findElement(By.xpath("//*[@data-a-target='profile-banner-upload-button']")).click();
        Thread.sleep(2000);

        // Upload screenshot we took
        String filePath = System.getProperty("user.home") + "/Downloads/partial_screenshot.png";

        // Wait for upload to finish
        driver.findElement(By.cssSelector("input.InjectLayout-sc-1i43xsx-0.bLjjio.drag-and-drop-file-picker__input"))
                .sendKeys(filePath);
        Thread.sleep(10000);

        // Navigate to profile
        driver.get("https://www.twitch.tv/softwaretesting2025");

        Thread.sleep(5000);
    }

    // Helper Methods
}
