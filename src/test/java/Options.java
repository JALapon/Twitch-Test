import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class Options {
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
    public void lunaAccess() throws InterruptedException {
        //Store the current window
        String parentWindow = driver.getWindowHandle();

        // Open the menu
        driver.findElement(By.cssSelector(".hKEeil > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > " +
                "button:nth-child(1)")).click();
        Thread.sleep(3000);
        //Click the Luna button that opens a new tab/window
        driver.findElement(By.cssSelector("div.Layout-sc-1xcs6mc-0:nth-child(11) > a:nth-child(1) > " +
                "div:nth-child(1)")).click();
        Thread.sleep(3000);

        Thread.sleep(3000);
        //Switch back to the parent window
        driver.switchTo().window(parentWindow);

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
    //
    @Test(priority = 2)
    public void aboutPage() throws InterruptedException {
        String parentWindow = driver.getWindowHandle();

        // Open menu
        driver.findElement(By.cssSelector(".hKEeil > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > button:nth-child(1)")).click();
        Thread.sleep(2000);
        // Click About (opens in new tab or window)
        driver.findElement(By.cssSelector("div.jNrYjU:nth-child(2) > a:nth-child(1) > div:nth-child(1) > div:nth-child(1)")).click();
        Thread.sleep(2000);

        // Switch to the new tab
        Set<String> allWindows = driver.getWindowHandles();
        for (String windowHandle : allWindows) {
            if (!windowHandle.equals(parentWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }

        // Scroll down
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(3000);
        // Scroll up
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
        Thread.sleep(3000);

        // Close the About window and go back to the parent
        driver.close();
        driver.switchTo().window(parentWindow);
    }
    //Test 3 switch between dark and lightmode.
    @Test(priority = 3)
    public void darkmodeSelector() throws InterruptedException {
        // Opens user menu and test whether dark and light mode work.
        driver.findElement(By.cssSelector(".bfqNgN > div:nth-child(1) > div:nth-child(1)")).click();
        driver.findElement(By.cssSelector(".ScToggle-sc-iguyno-0")).click();
        Thread.sleep(3000);
        driver.findElement(By.cssSelector(".ScToggle-sc-iguyno-0")).click();
        Thread.sleep(3000);
    }
    //Test 4 Change the language between english and spanish and back to english.
    @Test(priority = 4)
    public void changeLanguage() throws InterruptedException {
        Thread.sleep(5000);
        // Opens user menu and test whether dark and light mode work.
        //Opens language drop down
        driver.findElement(By.cssSelector(".gvrLBv > div:nth-child(1) > button:nth-child(1) > div:nth-child(1) > " +
                "div:nth-child(2)")).click();
        Thread.sleep(5000);
        //Selects Spanish
        driver.findElement(By.cssSelector("div.Layout-sc-1xcs6mc-0:nth-child(6) > button:nth-child(1) > div:nth-child(1)" +
                " > div:nth-child(1)")).click();
        Thread.sleep(5000);
        //Opens user menu
        driver.findElement(By.cssSelector(".bfqNgN > div:nth-child(1) > div:nth-child(1)")).click();
        //Opens language drop down
        driver.findElement(By.cssSelector(".gvrLBv > div:nth-child(1) > button:nth-child(1) > div:nth-child(1) > " +
                "div:nth-child(2)")).click();
        Thread.sleep(5000);
        //Selects English
        driver.findElement(By.cssSelector(".gvrLBv > div:nth-child(1) > button:nth-child(1) > " +
                "div:nth-child(1)")).click();
        Thread.sleep(3000);
    }
    //Test 5 collapsing the live channels and making it appear again.
    @Test(priority = 5)
    public void sideBar() throws InterruptedException {
        driver.findElement(By.cssSelector(".eeRiXn > div:nth-child(1)")).click();
        Thread.sleep(3000);
        driver.findElement(By.cssSelector(".bZyUVU > div:nth-child(1) > button:nth-child(1)")).click();
        Thread.sleep(3000);

        List<WebElement> StreamerInfo = driver.findElements(By.xpath("/html/body/div[1]/div/div[1]/div" +
                "/div[1]/div/div/div/div[3]/div/div/div[2]/nav/div/div/div[1]/div"));
        for (WebElement element : StreamerInfo) {
            System.out.println(element.getText());
        }
    }
}