package tests;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import io.appium.java_client.remote.MobileCapabilityType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.URL;
import java.time.Duration;

public class CompraSteps {

    private AndroidDriver<MobileElement> driver;

    @Given("que o usuário está na tela inicial")
    public void que_o_usuario_esta_na_tela_inicial() {
        try {
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability(MobileCapabilityType.DEVICE_NAME, "emulator-5554");
            caps.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
            caps.setCapability(MobileCapabilityType.PLATFORM_VERSION, "13.0");

            // Verifica se o arquivo APK existe
            File app = new File("C:\\Users\\wesle\\Desktop\\apks\\Android.apk");
            if (!app.exists()) {
                throw new RuntimeException("APK not found at " + app.getAbsolutePath());
            }
            caps.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());
            caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");

            driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723"), caps);

            if (driver == null) {
                throw new RuntimeException("Failed to initialize Appium session");
            }

            WebDriverWait wait = new WebDriverWait(driver, 60);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.TextView[@text='Products']")));
            System.out.println("Tela inicial carregada com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Appium session");
        }
    }

    @When("o usuário adiciona o produto {string} ao carrinho")
    public void o_usuario_adiciona_o_produto_ao_carrinho(String produto) {
        if (driver == null) {
            throw new IllegalStateException("Driver is not initialized, make sure the Appium session is created successfully");
        }

        WebDriverWait wait = new WebDriverWait(driver, 60);

        MobileElement produtoElement = scrollToElementByText(produto);

        if (produtoElement != null) {
            produtoElement.click();
            System.out.println("Produto encontrado e clicado: " + produto);

            wait.until(ExpectedConditions.presenceOfElementLocated(MobileBy.AccessibilityId("Add To Cart button")));
            MobileElement addToCartButton = driver.findElement(MobileBy.AccessibilityId("Add To Cart button"));
            addToCartButton.click();
            System.out.println("Botão 'Add To Cart' clicado.");

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.view.ViewGroup[@content-desc='cart badge']//android.widget.ImageView")));
            MobileElement cartBadge = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc='cart badge']//android.widget.ImageView"));
            cartBadge.click();
            System.out.println("Badge do carrinho encontrado e clicado.");
        } else {
            System.out.println("Elemento não encontrado: " + produto);
        }
    }

    @When("o usuário procede para o checkout")
    public void o_usuario_procede_para_o_checkout() {
        if (driver == null) {
            throw new IllegalStateException("Driver is not initialized, make sure the Appium session is created successfully");
        }

        WebDriverWait wait = new WebDriverWait(driver, 60);
        MobileElement checkoutButton = (MobileElement) wait.until(ExpectedConditions.visibilityOfElementLocated(MobileBy.xpath("//android.widget.TextView[@text='Proceed To Checkout']")));
        checkoutButton.click();
        System.out.println("Botão 'Proceed To Checkout' clicado.");
    }

    @When("o usuário completa o processo de checkout")
    public void o_usuario_completa_o_processo_de_checkout() {
        if (driver == null) {
            throw new IllegalStateException("Driver is not initialized, make sure the Appium session is created successfully");
        }

        WebDriverWait wait = new WebDriverWait(driver, 60);
        try {
            // Valida se estamos na tela de login
            MobileElement loginScreenValidation = (MobileElement) wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//android.widget.TextView[@text='Login']")));
            if (loginScreenValidation == null) {
                throw new IllegalStateException("Não estamos na tela de login");
            }

            // Clica no email para preencher os campos de login
            MobileElement emailElement = (MobileElement) wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.TextView[@text='bob@example.com']")));
            emailElement.click();
            System.out.println("Email selecionado: bob@example.com");

            MobileElement loginButton = (MobileElement) wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.view.ViewGroup[@content-desc='Login button']")));
            loginButton.click();
            System.out.println("Botão 'Login' clicado.");

            // Preenche os campos de endereço
            preencherCampo(wait, "Full Name* input field", "Rebecca Winter");
            preencherCampo(wait, "Address Line 1* input field", "Manderley 112");
            preencherCampo(wait, "Address Line 2 input field", "Entrance 1");
            preencherCampo(wait, "City* input field", "Truro");
            preencherCampo(wait, "State/Region input field", "Cornwall");

            // Rolar para o campo "Zip Code*"
            scrollDown();
            preencherCampo(wait, "Zip Code* input field", "89750");

            scrollDown();
            preencherCampo(wait, "Country* input field", "United Kingdom");

            // Rolar para o botão "To Payment"
            scrollDown();
            MobileElement toPaymentButton = (MobileElement) wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.TextView[@text='To Payment']")));
            toPaymentButton.click();
            System.out.println("Botão 'To Payment' clicado.");

            // Preencher os campos de pagamento
            preencherCamposDePagamento();

        } catch (TimeoutException e) {
            System.out.println("Timeout ao esperar pelo elemento: " + e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            System.out.println("Elemento não encontrado: " + e.getMessage());
            throw e;
        } catch (WebDriverException e) {
            System.out.println("Erro do WebDriver: " + e.getMessage());
            throw e;
        }
    }

    private void preencherCamposDePagamento() {
        WebDriverWait wait = new WebDriverWait(driver, 60);

        try {
            // Verificar se estamos na página de pagamento
            MobileElement checkoutHeader = (MobileElement) wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//android.widget.TextView[@text='Checkout']")));
            if (checkoutHeader == null) {
                throw new IllegalStateException("Não estamos na página de checkout");
            }

            // Encontrar os elementos de pagamento
            preencherCampo(wait, "Full Name* input field", "Rebecca Winter");
            preencherCampo(wait, "Card Number* input field", "5162 5370 5336 8886");
            preencherCampo(wait, "Expiration Date* input field", "11/25");
            preencherCampo(wait, "Security Code* input field", "646");

            // Clica no botão "Review Order"
            MobileElement reviewOrderButton = (MobileElement) wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.TextView[@text='Review Order']")));
            reviewOrderButton.click();
            System.out.println("Botão 'Review Order' clicado.");

        } catch (TimeoutException e) {
            System.out.println("Timeout ao esperar pelo elemento: " + e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            System.out.println("Elemento não encontrado: " + e.getMessage());
            throw e;
        } catch (WebDriverException e) {
            System.out.println("Erro do WebDriver: " + e.getMessage());
            throw e;
        }
    }

    @Then("a compra deve ser concluída com sucesso")
    public void a_compra_deve_ser_concluida_com_sucesso() {
        if (driver == null) {
            throw new IllegalStateException("Driver is not initialized, make sure the Appium session is created successfully");
        }

        WebDriverWait wait = new WebDriverWait(driver, 90); // Aumentando o tempo de espera para 90 segundos
        try {
            // Verificar se estamos na página de revisão do pedido
            MobileElement reviewOrderHeader = (MobileElement) wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//android.widget.TextView[@text='Review Order']")));
            if (reviewOrderHeader == null) {
                throw new IllegalStateException("Não estamos na página de revisão do pedido");
            }

            // Adicionar uma espera extra para garantir que a página esteja totalmente carregada
            Thread.sleep(2000);

            // Clicar no botão "Place Order"
            MobileElement placeOrderButton = (MobileElement) wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.TextView[@text='Place Order']")));
            placeOrderButton.click();
            System.out.println("Botão 'Place Order' clicado.");

            // Verificar se estamos na página de confirmação do pedido
            MobileElement confirmationHeader = (MobileElement) wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//android.widget.TextView[@text='Checkout Complete']")));
            if (confirmationHeader == null) {
                throw new IllegalStateException("Não estamos na página de confirmação do pedido");
            }

            // Verificar a mensagem de sucesso
            MobileElement successMessage = (MobileElement) wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//android.widget.TextView[contains(@text, 'Your new swag is on its way')]")));
            assert successMessage != null;
            System.out.println("Compra concluída com sucesso.");

        } catch (TimeoutException e) {
            System.out.println("Timeout ao esperar pelo elemento: " + e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            System.out.println("Elemento não encontrado: " + e.getMessage());
            throw e;
        } catch (WebDriverException e) {
            System.out.println("Erro do WebDriver: " + e.getMessage());
            throw e;
        } catch (InterruptedException e) {
            System.out.println("Erro de interrupção: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void preencherCampo(WebDriverWait wait, String accessibilityId, String valor) {
        try {
            MobileElement campo = (MobileElement) wait.until(ExpectedConditions.presenceOfElementLocated(MobileBy.AccessibilityId(accessibilityId)));
            campo.clear();
            campo.sendKeys(valor);
            System.out.println("Campo '" + accessibilityId + "' preenchido com valor: " + valor);
        } catch (TimeoutException e) {
            System.out.println("Timeout ao esperar pelo campo: " + accessibilityId + ". " + e.getMessage());
            throw e;
        } catch (NoSuchElementException e) {
            System.out.println("Campo não encontrado: " + accessibilityId + ". " + e.getMessage());
            throw e;
        } catch (WebDriverException e) {
            System.out.println("Erro do WebDriver ao preencher o campo: " + accessibilityId + ". " + e.getMessage());
            throw e;
        }
    }

    private MobileElement scrollToElementByText(String text) {
        MobileElement scrollView = driver.findElement(By.className("android.widget.ScrollView"));

        for (int i = 0; i < 5; i++) {
            try {
                MobileElement element = (MobileElement) scrollView.findElement(By.xpath("//*[contains(@text, '" + text + "')]"));
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (Exception e) {
                scrollDown();
            }
        }
        return null;
    }

    private void scrollDown() {
        int startX = driver.manage().window().getSize().width / 2;
        int startY = (int) (driver.manage().window().getSize().height * 0.8);
        int endY = (int) (driver.manage().window().getSize().height * 0.2);

        new TouchAction<>(driver)
                .press(PointOption.point(startX, startY))
                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000)))
                .moveTo(PointOption.point(startX, endY))
                .release()
                .perform();
    }
}
