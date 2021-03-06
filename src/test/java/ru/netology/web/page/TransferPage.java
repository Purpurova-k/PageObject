package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static java.time.Duration.ofSeconds;

public class TransferPage {
    private SelenideElement heading = $("h1");
    private SelenideElement amountField = $("[data-test-id=amount] input");
    private SelenideElement fromField = $("[data-test-id=from] input");
    private SelenideElement transferButton = $("[data-test-id=action-transfer] span");
    private SelenideElement errorNotification = $("[data-test-id=error-notification]");


    public TransferPage() {
        heading.shouldBe(visible).shouldHave(text("Пополнение карты"));
    }

    public DashboardPage validTransferFrom(int amount, DataHelper.CardInfo cardFrom) {
        amountField.sendKeys(Keys.chord(Keys.CONTROL, "a") + Keys.BACK_SPACE);
        amountField.val(String.valueOf(amount));
        fromField.sendKeys(Keys.chord(Keys.CONTROL, "a") + Keys.BACK_SPACE);
        fromField.val(cardFrom.getNumber());
        transferButton.click();
        return new DashboardPage();
    }

    public void invalidTransferFrom(int amount, DataHelper.CardInfo cardFrom) {
        amountField.sendKeys(Keys.chord(Keys.CONTROL, "a") + Keys.BACK_SPACE);
        amountField.val(String.valueOf(amount));
        fromField.sendKeys(Keys.chord(Keys.CONTROL, "a") + Keys.BACK_SPACE);
        fromField.val(cardFrom.getNumber());
        transferButton.click();

        errorNotification.shouldBe(visible);
        $("[data-test-id=error-notification] .notification__content").shouldHave(text("Ошибка!"));
    }

    public void errorNotificationShouldBeVisible() {
        errorNotification.shouldBe(visible);
        $("[data-test-id=error-notification] .notification__content").shouldHave(text("Ошибка!"));
    }
}
