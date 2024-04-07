package com.example.SeleniumJavaIntermedio;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

@SpringBootTest
class SeleniumJavaIntermedioApplicationTests {

	private ExtentReports extentReport;
	private WebDriver webDriver;
	private String usuario;
	private String contrasenna;

	@BeforeEach
	void getUp(){
		webDriver = new ChromeDriver();
		extentReport = new ExtentReports();
		webDriver.get("https://www.saucedemo.com");
		usuario = "standard_user";
		contrasenna = "secret_sauce";
	}


	@Test
	void TestWebShop() {

		//Configurando e inicializando el ExtentReporter en Spark.
		ExtentSparkReporter extentSparkReporter = new ExtentSparkReporter("reports/spark_report_shop_status.html");

		extentReport.attachReporter(extentSparkReporter);
		
		ExtentTest testlog = extentReport.createTest("report_shop_status");
		
		try{
			//Vista login inicio.
			try{
				
				webDriver.findElement(By.id("user-name")).sendKeys(usuario);

				webDriver.findElement(By.id("password")).sendKeys(contrasenna);
				
				File screenshotFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(screenshotFile, new File("images/screenshot_1_login.png"));

				webDriver.findElement(By.id("login-button")).click();

			} catch (Exception e) {

				testlog.log(Status.FAIL, "Error en la vista de login. Error: "+ e.getMessage());
				e.printStackTrace();
			}

			//Vista seleccionar items a comprar.

			try {

				List<WebElement> precios_web_list = webDriver.findElements(By.className("inventory_item_price"));
				List<Float> precios_float_list = new ArrayList<>();

				for (WebElement price : precios_web_list){
					Float precio_float = Float.parseFloat(price.getText().replace("$", ""));
					precios_float_list.add(precio_float);
				}

				Float precio_minimo_float_list = Collections.min(precios_float_list);

				List<WebElement> div_inventory_items = webDriver.findElements(By.cssSelector(".pricebar .inventory_item_price, .pricebar button"));

				for (int i = 0; i < div_inventory_items.size(); i += 2) {

					WebElement precioElement = div_inventory_items.get(i);
					WebElement botonElement = div_inventory_items.get(i+1);

					if (precioElement.getText().replace("$","").equals(Float.toString(precio_minimo_float_list))){
						
						botonElement.click();
						break;

					}

				}

				File screenshotFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(screenshotFile, new File("images/screenshot_2_seleccionar_items.png"));

				webDriver.findElement(By.className("shopping_cart_link")).click();
			} catch (Exception e) {

				testlog.log(Status.FAIL, "Error en la vista de elegir productos. Error: "+ e.getMessage());
				e.printStackTrace();
			}

			//Vista carrito de compras.

			try{

				File screenshotFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(screenshotFile, new File("images/screenshot_3_carrito_compras.png"));

				webDriver.findElement(By.id("checkout")).click();
			} catch (Exception e) {

				testlog.log(Status.FAIL, "Error en la vista de carrito de compras. Error: "+ e.getMessage());
				e.printStackTrace();
			}
			//Vista ingresar datos de compra.

			try{

				webDriver.findElement(By.id("first-name")).sendKeys("Lázaro");
				webDriver.findElement(By.id("last-name")).sendKeys("Bertín");
				webDriver.findElement(By.id("postal-code")).sendKeys("12345678");

				File screenshotFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(screenshotFile, new File("images/screenshot_4_datos_compra.png"));

				webDriver.findElement(By.id("continue")).click();
			} catch (Exception e) {

				testlog.log(Status.FAIL, "Error en la vista de ingresar datos de compra. Error: "+ e.getMessage());
				e.printStackTrace();
			}

			//Vista compra realizada e información de la compra.
			try{
				
				File screenshotFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(screenshotFile, new File("images/screenshot_5_compra_finalizada.png"));

				webDriver.findElement(By.id("finish")).click();

				screenshotFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(screenshotFile, new File("images/screenshot_6_compra_exito.png"));
				testlog.log(Status.PASS, "Éxito al hacer prueba de comprar.");
			} catch (Exception e) {

				testlog.log(Status.FAIL, "Error en la vista de compra realizada. Error: "+ e.getMessage());
				e.printStackTrace();
			}

		} catch (Exception e) {

			testlog.log(Status.FAIL, "Error al inicio de prueba de comprar. Error: "+ e.getMessage());
			e.printStackTrace();

		} finally {
			//Interesa poner estas funciones aquí porque sirve para que sea cual
			//sea el resultado (si entra al try o al except) cerrará igualmente 
			//el reporte y la página web.
			extentReport.flush();
			webDriver.close();
		}
		
	}

}
