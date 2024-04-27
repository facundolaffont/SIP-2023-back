package com.example.helloworld;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

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
    public void testPageTitle() {
        
        // Abrir la página web
        driver.get("http://localhost:4040");
        
        WebElement textBox = driver.findElement(By.name("Ingresar"));
        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        
        //assertEquals(expectedTitle, actualTitle);
    }

    @AfterEach
    public void tearDown() {
        // Cerrar el navegador después de cada prueba
        if (driver != null) {
            driver.quit();
        }
    }
}
