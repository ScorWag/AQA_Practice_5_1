package ru.netology.delivery.test;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$x;

class DeliveryTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        SelenideElement form = $("form");
        form.$("[data-test-id='city'] input").setValue(validUser.getCity()).click();
        form.$("[data-test-id='date'] input").sendKeys(Keys.CONTROL + "A");
        form.$("[data-test-id='date'] input").sendKeys(Keys.BACK_SPACE);
        form.$("[data-test-id='date'] input").setValue(firstMeetingDate);
        form.$("[data-test-id='name'] input").setValue(validUser.getName());
        form.$("[data-test-id='phone'] input").setValue(validUser.getPhone());
        form.$("[data-test-id='agreement']").click();
        $x("//button[contains(@class,'button_view_extra')]").shouldBe(visible).click();
        $("[data-test-id='success-notification']").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id='success-notification']")
                .shouldHave(exactText("Успешно!" + "\n" + "Встреча успешно запланирована на " + firstMeetingDate));
        $x("//button[contains(@class,'notification__closer')]").click();
        form.$("[data-test-id='date'] input").sendKeys(Keys.CONTROL + "A");
        form.$("[data-test-id='date'] input").sendKeys(Keys.BACK_SPACE);
        form.$("[data-test-id='date'] input").setValue(secondMeetingDate);
        $x("//button[contains(@class,'button_view_extra')]").shouldBe(visible).click();
        $("[data-test-id='replan-notification']").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id='replan-notification'] [class='notification__title']")
                .shouldHave(exactText("Необходимо подтверждение"));
        $("[data-test-id='replan-notification'] [class='notification__content']")
                .shouldHave(exactText("У вас уже запланирована встреча на другую дату. " +
                        "Перепланировать?\n\nПерепланировать"));
        $x("//button[contains(.,'Перепланировать')]").shouldBe(visible).click();
        $("[data-test-id='success-notification']")
                .shouldHave(exactText("Успешно!" + "\n" + "Встреча успешно запланирована на " + secondMeetingDate));
        $x("//button[contains(@class,'notification__closer')]").click();
    }
}
