package tests;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.net.URL;

public class baseTest {
    protected static AndroidDriver<MobileElement> driver;

    @BeforeClass
    public void setUp() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("platformVersion", "13.0"); // Atualize para a versão do seu dispositivo/emulador
        capabilities.setCapability("deviceName", "emulator-5554"); // Substitua pelo nome do seu dispositivo/emulador
        capabilities.setCapability("app", "C:\\Users\\wesle\\Desktop\\apks\\Android.apk"); // Caminho para o APK do aplicativo
        capabilities.setCapability("automationName", "UiAutomator2");

        driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723"), capabilities);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
