package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.Assert;
import java.time.Duration;
import java.util.List;

public class DemoQATests {
    WebDriver driver;
    WebDriverWait wait;
    Actions actions;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\drivers\\chromedriver.exe");
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
            "--remote-allow-origins=*",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-notifications",
            "--start-maximized"
        );
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Aumentado a 20 segundos
        actions = new Actions(driver);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testTextBoxForm() {
        driver.get("https://demoqa.com/text-box");
        hideAds();
        
        WebElement fullName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userName")));
        fullName.sendKeys("Juan Pérez");
        
        WebElement email = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userEmail")));
        email.sendKeys("juan@test.com");
        
        WebElement currentAddress = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currentAddress")));
        currentAddress.sendKeys("Calle Falsa 123");
        
        WebElement permanentAddress = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("permanentAddress")));
        permanentAddress.sendKeys("Avenida Siempre Viva 456");
        
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        scrollAndClick(submitBtn);
        
        WebElement nameOutput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        Assert.assertTrue("El nombre no se muestra en los resultados", 
                        nameOutput.getText().contains("Juan Pérez"));
        
        WebElement emailOutput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        Assert.assertTrue("El email no se muestra en los resultados",
                        emailOutput.getText().contains("juan@test.com"));
    }

    @Test
    public void testCheckBox() {
        driver.get("https://demoqa.com/checkbox");
        hideAds();
        
        WebElement toggleBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("button.rct-collapse-btn")));
        clickWithJS(toggleBtn);
        
        WebElement desktopCheckbox = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//span[text()='Desktop']/preceding-sibling::span[@class='rct-checkbox']")));
        clickWithJS(desktopCheckbox);
        
        WebElement result = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("result")));
        Assert.assertTrue("El resultado no muestra la selección esperada",
                        result.getText().contains("desktop"));
    }

    @Test
    public void testRadioButton() {
        driver.get("https://demoqa.com/radio-button");
        hideAds();
        
        WebElement yesLabel = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//label[@for='yesRadio']")));
        scrollAndClick(yesLabel);
        
        WebElement yesRadio = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.id("yesRadio")));
        boolean isChecked = (Boolean) ((JavascriptExecutor)driver).executeScript("return arguments[0].checked;", yesRadio);
        Assert.assertTrue("El radio button 'Yes' no está seleccionado", isChecked);
        
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".text-success")));
        Assert.assertEquals("Texto de confirmación incorrecto", 
                          "Yes", successMessage.getText());
    }

    @Test
    public void testWebTables() {
        driver.get("https://demoqa.com/webtables");
        hideAds();
        
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("addNewRecordButton")));
        scrollAndClick(addButton);
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstName"))).sendKeys("Ana");
        driver.findElement(By.id("lastName")).sendKeys("Gómez");
        driver.findElement(By.id("userEmail")).sendKeys("ana@test.com");
        driver.findElement(By.id("age")).sendKeys("28");
        driver.findElement(By.id("salary")).sendKeys("3500");
        driver.findElement(By.id("department")).sendKeys("TI");
        
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        scrollAndClick(submitButton);
        
        WebElement anaCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[@class='rt-td' and text()='Ana']")));
        Assert.assertTrue("El registro no aparece en la tabla", anaCell.isDisplayed());
        
        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[text()='Ana']/ancestor::div[@role='rowgroup']//span[@title='Delete']")));
        scrollAndClick(deleteButton);
        
        wait.until(ExpectedConditions.invisibilityOf(anaCell));
        List<WebElement> anaRecords = driver.findElements(
            By.xpath("//div[@class='rt-td' and text()='Ana']"));
        Assert.assertTrue("El registro no fue eliminado correctamente", anaRecords.isEmpty());
    }

    @Test
    public void testButtons() {
        driver.get("https://demoqa.com/buttons");
        hideAds();
        
        // Doble clic
        WebElement doubleClickBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("doubleClickBtn")));
        actions.doubleClick(doubleClickBtn).perform();
        
        WebElement doubleClickMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("doubleClickMessage")));
        Assert.assertTrue("No se muestra el mensaje de doble clic",
                        doubleClickMessage.isDisplayed());
        
        // Clic derecho - Versión mejorada
        WebElement rightClickBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("rightClickBtn")));
        
        // Scroll y espera adicional
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rightClickBtn);
        wait.until(ExpectedConditions.visibilityOf(rightClickBtn));
        
        // Intentar clic derecho normal primero, luego fallback con JavaScript
        try {
            actions.contextClick(rightClickBtn).perform();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                "var event = new MouseEvent('contextmenu', { bubbles: true }); arguments[0].dispatchEvent(event);", 
                rightClickBtn);
        }
        
        // Esperar primero presencia, luego visibilidad
        WebElement rightClickMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.id("rightClickMessage")));
        wait.until(ExpectedConditions.visibilityOf(rightClickMessage));
        
        Assert.assertTrue("No se muestra el mensaje de clic derecho",
                        rightClickMessage.isDisplayed());
    }

    @Test
    public void testDragAndDrop() {
        driver.get("https://demoqa.com/droppable");
        hideAds();
        
        WebElement dragElement = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("draggable")));
        WebElement dropZone = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("droppable")));
        
        String originalText = dropZone.getText();
        String originalColor = dropZone.getCssValue("background-color");
        
        actions.dragAndDrop(dragElement, dropZone).perform();
        
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(dropZone, originalText)));
        
        Assert.assertNotEquals("El texto no cambió después del drop",
                             originalText, dropZone.getText());
        Assert.assertNotEquals("El color no cambió después del drop",
                             originalColor, dropZone.getCssValue("background-color"));
    }

    // ==================== MÉTODOS AUXILIARES MEJORADOS ====================

    private void scrollAndClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            clickWithJS(element);
        }
    }

    private void clickWithJS(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].click();", element);
    }

    private void hideAds() {
        try {
            ((JavascriptExecutor) driver).executeScript(
                "const ads = document.querySelectorAll('.ad-backdrop, #fixedban, .adsbygoogle');" +
                "ads.forEach(ad => ad.style.display = 'none');");
        } catch (Exception e) {
            System.out.println("No se encontraron anuncios para ocultar");
        }
    }

    private void rightClickWithJS(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
            "var event = new MouseEvent('contextmenu', { " +
            "bubbles: true, cancelable: true, view: window }); " +
            "arguments[0].dispatchEvent(event);", element);
    }
}