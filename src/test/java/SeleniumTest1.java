import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeleniumTest1 {
    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get("https://www.google.com");

        // necessary to guarantee consistent environment - Open Lending has a hamburger
        // menu when the screen size is too small
        driver.manage().window().maximize();

        // Google Search
        WebElement search = driver.findElement(By.tagName("textarea"));
        search.sendKeys("Open Lending");

        List<WebElement> listItems = driver.findElements(By.tagName("li"));
        WebElement targetItem = listItems.get(0).findElement(By.xpath("//div[@aria-label='open lending']"));
        targetItem.click();

        // Clicking targeted search result - Open Lending home page
        WebElement targetLink = driver.findElement(By.cssSelector("a[href='https://www.openlending.com/']"));
        assert targetLink.isDisplayed();

        targetLink.click();
        assert driver.getCurrentUrl().equals("https://www.openlending.com/");

        // Current url should be openlending.com at this point
        // Navigating to resources page now
        // Needed to wait till the targetLink was clickable to have consistent behavior
        targetLink = driver.findElement(By.cssSelector("li.menu-item a[href='https://www.openlending.com/resources/']"));
        wait.until(ExpectedConditions.elementToBeClickable(targetLink));
        targetLink.click();
        assert driver.getCurrentUrl().equals("https://www.openlending.com/resources/");

        // Resources page has been reached, grabbing load more button
        // Also grabbing the button's parent - div - since the button changes for a second when clicked
        // The idea is to capture when the button is clicked and blogs are loading
        // When that happens, wait for the button to reach its original state
        WebElement loadMoreBtn = driver.findElement(By.cssSelector("button.facetwp-load-more"));
        WebElement btnParent = loadMoreBtn.findElement(By.xpath(".."));
        assert loadMoreBtn.isDisplayed();
        assert btnParent.isDisplayed();

        // This could be moved up above finding the load more button
        // Getting a working list of blogs from the insights container
        WebElement insightsContainer = driver.findElement(By.cssSelector("ul.facetwp-template"));
        List<WebElement> blogList = insightsContainer.findElements(By.cssSelector("li div.paragraph-p2 p"));
        Set<String> uniqueBlogList = new HashSet<>();

        // initializing the uniqueBlogList set - this will be used to determine if there
        // are duplicates given the set data structure
        for (WebElement p : blogList) {
            uniqueBlogList.add(p.getText());
        }

        // build up the unique blog list given new list items displayed after clicking
        // load more button
        while (true) {
            if (loadMoreBtn.isDisplayed()) {
                loadMoreBtn.click();
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='Loading']")));
                blogList = insightsContainer.findElements(By.cssSelector("li div.paragraph-p2 p"));
                blogList.forEach(blog -> uniqueBlogList.add(blog.getText()));

            } else {
                break;
            }
            loadMoreBtn = driver.findElement(By.cssSelector("button.facetwp-load-more"));
        }

        // final check: checking uniqueBlogList size against blogList size
        // Since HashSet doesn't add duplicates, if the size of uniqueBlogList
        // and blogList are the same then that should confirm there are no duplicates
        blogList = insightsContainer.findElements(By.cssSelector("li div.paragraph-p2 p"));
        assertEquals(uniqueBlogList.size(), blogList.size());

        driver.close();
    }
}
