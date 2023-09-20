import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.xml.xpath.XPath;
import java.time.Duration;
import java.util.List;

public class SeleniumTest1 {
    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get("https://www.google.com");

        WebElement search = driver.findElement(By.tagName("textarea"));
        search.sendKeys("Open Lending");

        List<WebElement> listItems = driver.findElements(By.tagName("li"));
        WebElement targetItem = listItems.get(0).findElement(By.xpath("//div[@aria-label='open lending']"));
        targetItem.click();

        WebElement targetLink = driver.findElement(By.cssSelector("a[href='https://www.openlending.com/']"));
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        wait.until(d -> targetLink.isDisplayed());

        targetLink.click();
        assert driver.getCurrentUrl() == "https://www.openlending.com/";

        driver.close();
    }
}
