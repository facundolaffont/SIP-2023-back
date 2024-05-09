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

        Thread.sleep(5000);

        WebElement courseButton = driver.findElement(By.xpath("/html/body/div/div/div[3]/div/div"));
        courseButton.click();

        Thread.sleep(3000);

        WebElement registracionesButton = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[1]/span"));
        registracionesButton.click();

        Thread.sleep(3000);

        WebElement registracionAlumnosButton = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[1]/div/a[1]"));
        registracionAlumnosButton.click();

        Thread.sleep(5000);

        WebElement inputFile = driver.findElement(By.id("file"));
        inputFile.sendKeys("C://Users//leo_2//OneDrive//Escritorio//UNLU//Seminario de Integracion Profesional//Selenium//archivo-5.xlsx");

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

        registracionesButton = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[1]/span"));
        registracionesButton.click();

        Thread.sleep(3000);

        WebElement registrarEventosButton = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[1]/div/a[4]"));
        registrarEventosButton.click();

        Thread.sleep(3000);

        inputFile = driver.findElement(By.id("file"));
        inputFile.sendKeys("C://Users//leo_2//OneDrive//Escritorio//UNLU//Seminario de Integracion Profesional//Selenium//archivo-5.xlsx");
        
        sheetNames = driver.findElement(By.id("sheet-names"));
        select = new Select(sheetNames);
        select.selectByVisibleText("alta-eventos");

        cellRange = driver.findElement(By.id("cell-range"));
        cellRange.sendKeys("A2:D9");

        loadButton = driver.findElement(By.className("load-button"));
        loadButton.click();

        Thread.sleep(3000);

        WebElement registerEventsButton = driver.findElement(By.className("register-events-button"));
        registerEventsButton.click();

        Thread.sleep(3000);

        registracionesButton = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[1]/span"));
        registracionesButton.click();

        Thread.sleep(3000);

        WebElement registrarAsistenciasButton = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[1]/div/a[5]"));
        registrarAsistenciasButton.click();

        inputFile = driver.findElement(By.id("file"));
        inputFile.sendKeys("C://Users//leo_2//OneDrive//Escritorio//UNLU//Seminario de Integracion Profesional//Selenium//archivo-5.xlsx");

        sheetNames = driver.findElement(By.id("sheet-names"));
        select = new Select(sheetNames);
        select.selectByVisibleText("asistencia-clase1");

        cellRange = driver.findElement(By.id("cell-range"));
        cellRange.sendKeys("A2:B6");

        WebElement eventsListSelect = driver.findElement(By.id("events-select"));
        Select eventSelect = new Select(eventsListSelect);
        eventSelect.selectByValue("1");

        loadButton = driver.findElement(By.className("load-button"));
        loadButton.click();

        Thread.sleep(3000);

        WebElement registerAttendanceButton = driver.findElement(By.className("register-attendance-button"));
        registerAttendanceButton.click();

        Thread.sleep(3000);

        sheetNames = driver.findElement(By.id("sheet-names"));
        select = new Select(sheetNames);
        select.selectByVisibleText("asistencia-clase2");

        eventsListSelect = driver.findElement(By.id("events-select"));
        eventSelect = new Select(eventsListSelect);
        eventSelect.selectByValue("2");

        loadButton = driver.findElement(By.className("load-button"));
        loadButton.click();

        Thread.sleep(3000);

        registerAttendanceButton = driver.findElement(By.className("register-attendance-button"));
        registerAttendanceButton.click();

        Thread.sleep(3000);
        
        registracionesButton = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[1]/span"));
        registracionesButton.click();

        Thread.sleep(3000);

        WebElement registrarCalificacionesButton = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[1]/div/a[7]"));
        registrarCalificacionesButton.click();

        inputFile = driver.findElement(By.id("file"));
        inputFile.sendKeys("C://Users//leo_2//OneDrive//Escritorio//UNLU//Seminario de Integracion Profesional//Selenium//archivo-5.xlsx");

        sheetNames = driver.findElement(By.id("sheet-names"));
        select = new Select(sheetNames);
        select.selectByVisibleText("calificaciones-parcial1");

        cellRange = driver.findElement(By.id("cell-range"));
        cellRange.sendKeys("A2:B6");

        eventsListSelect = driver.findElement(By.id("events-select"));
        eventSelect = new Select(eventsListSelect);
        eventSelect.selectByValue("6");

        loadButton = driver.findElement(By.className("load-button"));
        loadButton.click();

        Thread.sleep(3000);

        WebElement registerCalifficationButton = driver.findElement(By.className("register-califications-button"));
        registerCalifficationButton.click();

        Thread.sleep(3000);

        informacionCursada = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[2]/span"));
        informacionCursada.click();

        WebElement consultarEventosAlumno = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[2]/div/a[4]"));
        consultarEventosAlumno.click();

        WebElement inputLegajo = driver.findElement(By.xpath("/html/body/div/div/div[3]/div/input"));
        inputLegajo.sendKeys("166364");

        WebElement buscarButton = driver.findElement(By.xpath("/html/body/div/div/div[3]/div/button"));
        buscarButton.click();

        Thread.sleep(8000);

        WebElement menuCondicionFinal = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[3]/span"));
        menuCondicionFinal.click();

        WebElement crearCriterioEvaluacion = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[3]/div/a[1]"));
        crearCriterioEvaluacion.click();

        Thread.sleep(3000);

        WebElement selectCriterioEvaluacion = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/select"));
        Select selectCE = new Select(selectCriterioEvaluacion);
        selectCE.selectByValue("1");

        WebElement inputValorRegular = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/input[1]"));
        inputValorRegular.sendKeys("50");
        WebElement inputValorPromovido = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/input[2]"));
        inputValorPromovido.sendKeys("75");

        WebElement cargarButton = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/button"));
        cargarButton.click();

        Thread.sleep(3000);

        selectCriterioEvaluacion = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/select"));
        selectCE = new Select(selectCriterioEvaluacion);
        selectCE.selectByValue("4");

        inputValorRegular.clear();
        inputValorRegular = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/input[1]"));
        inputValorRegular.sendKeys("100");
        inputValorPromovido.clear();
        inputValorPromovido = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/input[2]"));
        inputValorPromovido.sendKeys("100");

        cargarButton = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/button"));
        cargarButton.click();

        Thread.sleep(3000);

        selectCriterioEvaluacion = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/select"));
        selectCE = new Select(selectCriterioEvaluacion);
        selectCE.selectByValue("8");

        inputValorRegular.clear();
        inputValorRegular = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/input[1]"));
        inputValorRegular.sendKeys("50");
        inputValorPromovido.clear();
        inputValorPromovido = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/input[2]"));
        inputValorPromovido.sendKeys("0");

        cargarButton = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/button"));
        cargarButton.click();

        Thread.sleep(3000);

        selectCriterioEvaluacion = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/select"));
        selectCE = new Select(selectCriterioEvaluacion);
        selectCE.selectByValue("5");

        inputValorRegular.clear();
        inputValorRegular = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/input[1]"));
        inputValorRegular.sendKeys("4");
        inputValorPromovido.clear();
        inputValorPromovido = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/input[2]"));
        inputValorPromovido.sendKeys("6");

        cargarButton = driver.findElement(By.xpath("/html/body/div/div/div[3]/form/button"));
        cargarButton.click();

        Thread.sleep(3000);

        menuCondicionFinal = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[3]/span"));
        menuCondicionFinal.click();

        WebElement calcularCondicionFinal = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[3]/div/a[3]"));
        calcularCondicionFinal.click();

        Thread.sleep(3000);

        WebElement calcularCondicionButton = driver.findElement(By.className("calculate-button"));
        calcularCondicionButton.click();

        Thread.sleep(10000);

        informacionCursada = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[2]/span"));
        informacionCursada.click();

        WebElement listarEventos = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[2]/div/a[2]"));
        listarEventos.click();

        Thread.sleep(7000);

        informacionCursada = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[2]/span"));
        informacionCursada.click();

        WebElement resumenEventos = driver.findElement(By.xpath("/html/body/div/div/div[1]/nav/div[2]/div[2]/div/a[6]"));
        resumenEventos.click();

        Thread.sleep(7000);

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
