package com.example.helloworld;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeleniumTest {
    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C://Users//leo_2//OneDrive//Escritorio//UNLU//Seminario de Integracion Profesional//chromedriver-win32//chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
    }

    @Test
    public void testLogin() throws InterruptedException {
        
        // Abrir la página web
        driver.get("http://localhost:4040");
        
        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        submitButton.click();

        WebElement inputUsername = driver.findElement(By.id("username"));
        inputUsername.sendKeys("facu-docente@fake.com");

        WebElement inputPassword = driver.findElement(By.id("password"));
        inputPassword.sendKeys("FaL4FCxD9?Siqse");

        submitButton = driver.findElement(By.xpath("/html/body/div/main/section/div/div/div/form/div[2]/button"));
        submitButton.click();

        Thread.sleep(3000);

        WebElement courseButton = driver.findElement(By.xpath("/html/body/div/div/div[3]/div/div"));
        courseButton.click();

        Thread.sleep(5000);

        WebElement registracionesButton = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[1]/span"));
        registracionesButton.click();

        Thread.sleep(3000);

        WebElement registracionAlumnosButton = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[1]/div/a[1]"));
        registracionAlumnosButton.click();

        Thread.sleep(5000);

        WebElement inputFile = driver.findElement(By.id("file"));
        inputFile.sendKeys("C://Users//leo_2//OneDrive//Escritorio//UNLU//Seminario de Integracion Profesional//Selenium//archivo-5.xlsx");

        Thread.sleep(5000);

        WebElement sheetNames = driver.findElement(By.id("sheet-names"));
        Select select = new Select(sheetNames);

        select.selectByVisibleText("alta-estudiantes");

        WebElement cellRange = driver.findElement(By.id("cell-range"));
        cellRange.sendKeys("A2:G6");

        WebElement loadButton = driver.findElement(By.className("load-button"));
        loadButton.click();

        Thread.sleep(3000);

        WebElement registerButton = driver.findElement(By.className("register-button"));
        registerButton.click();

        Thread.sleep(3000);

        WebElement informacionCursada = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[2]/span"));
        informacionCursada.click();

        WebElement listarAlumnos = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[2]/div/a[3]"));
        listarAlumnos.click();

        Thread.sleep(5000);

        

    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        // Cerrar el navegador después de cada prueba
        Thread.sleep(1000);
        if (driver != null) {
            driver.quit();
        }
    }
}
