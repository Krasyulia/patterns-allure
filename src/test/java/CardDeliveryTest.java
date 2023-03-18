import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import com.google.common.io.Files;
import io.qameta.allure.Attachment;

import java.io.File;
import java.io.IOException;
import com.codeborne.selenide.Screenshots;

public class CardDeliveryTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(true));
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("AllureSelenide");
    }
    @BeforeEach
    void setupTest() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999/");
        Faker faker = new Faker(new Locale("ru"));
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.chord(Keys.BACK_SPACE));
    }

    @AfterEach
    public void tearDown() throws IOException {
        screenshot();
    }

    @Attachment(type = "image/png")
    public byte[] screenshot() throws IOException {
        File screenshot = Screenshots.getLastScreenshot();
        return screenshot == null ? null : Files.toByteArray(screenshot);
    }

    String dateByClient = DataGenerator.date(3);

    @Test
    void deliveryReplanningTest() {
        $("[data-test-id='city'] input").val(DataGenerator.city());
        $("[data-test-id='date'] input").val(dateByClient);
        $("[data-test-id='name'] input").val(DataGenerator.surNameAndFirstName());
        $("[data-test-id='phone'] input").val(DataGenerator.phone());
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $(".notification__title").shouldHave(Condition.text("Успешно!"), Duration.ofSeconds(15));
        $(".notification__content").shouldHave(Condition.text("Встреча успешно запланирована на " + dateByClient), Duration.ofSeconds(15));
        $(".button__text").click();
        $("[data-test-id='replan-notification'] .notification__content").shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"), Duration.ofSeconds(15));
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.chord(Keys.BACK_SPACE));
        String newDateByClient = DataGenerator.date(4);
        $("[data-test-id='date'] input").val(newDateByClient);
        $("[data-test-id='replan-notification'] button").click();
        $(".notification__title").shouldHave(Condition.text("Успешно"), Duration.ofSeconds(15));
        $(".notification__content").shouldHave(Condition.text("Встреча успешно запланирована на " + newDateByClient), Duration.ofSeconds(15));
    }
}