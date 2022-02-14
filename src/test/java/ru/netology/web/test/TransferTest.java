package ru.netology.web.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPage;


import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {

    private SelenideElement errorNotification = $("[data-test-id=error-notification]");

    @BeforeEach
    void setUp() {
        open("http://localhost:9999/");
    }

    @Test
    public void shouldTransferMoneyFrom1To2() {
        // авторизация
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verifyInfo = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verifyInfo);

        // возврат изначального баланса на картах
        DataHelper.returnInitialBalance();

        // перевод средств
        var balanceFirstCardBefore = dashboardPage.getCardBalance("0001");
        var balanceSecondCardBefore = dashboardPage.getCardBalance("0002");
        var transferPage = dashboardPage.transferMoneyTo("0002");
        var amount = DataHelper.getRandomAmount(balanceFirstCardBefore);
        var dashboardPage2 = transferPage.validTransferFrom(amount, DataHelper.getFirstCardInfo());

        // проверка
        var balanceFirstCardAfter = dashboardPage2.getCardBalance("0001");
        var balanceSecondCardAfter = dashboardPage2.getCardBalance("0002");
        var expectedBalanceFirstCard = balanceFirstCardBefore - amount;
        var expectedBalanceSecondCard = balanceSecondCardBefore + amount;

        assertEquals(expectedBalanceFirstCard, balanceFirstCardAfter);
        assertEquals(expectedBalanceSecondCard, balanceSecondCardAfter);
    }


    @Test
    public void shouldTransferMoneyFrom2To1() {
        // авторизация
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verifyInfo = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verifyInfo);

        // возврат изначального баланса на картах
        DataHelper.returnInitialBalance();

        // перевод средств
        var balanceFirstCardBefore = dashboardPage.getCardBalance("0001");
        var balanceSecondCardBefore = dashboardPage.getCardBalance("0002");
        var transferPage = dashboardPage.transferMoneyTo("0001");
        var amount = DataHelper.getRandomAmount(balanceSecondCardBefore);
        var dashboardPage2 = transferPage.validTransferFrom(amount, DataHelper.getSecondCardInfo());

        // проверка
        var balanceFirstCardAfter = dashboardPage2.getCardBalance("0001");
        var balanceSecondCardAfter = dashboardPage2.getCardBalance("0002");
        var expectedBalanceFirstCard = balanceFirstCardBefore + amount;
        var expectedBalanceSecondCard = balanceSecondCardBefore - amount;

        assertEquals(expectedBalanceFirstCard, balanceFirstCardAfter);
        assertEquals(expectedBalanceSecondCard, balanceSecondCardAfter);
    }

    // Падающий тест
    @Test
    public void shouldWarnIfTransferAmountOutOfLimit() {
        // авторизация
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verifyInfo = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verifyInfo);

        // возврат изначального баланса на картах
        DataHelper.returnInitialBalance();

        // перевод средств
        var transferPage = dashboardPage.transferMoneyTo("0001");
        $("[data-test-id=amount] input").sendKeys(Keys.chord(Keys.CONTROL, "a") + Keys.BACK_SPACE);
        $("[data-test-id=amount] input").val("15000");
        $("[data-test-id=from] input").sendKeys(Keys.chord(Keys.CONTROL, "a") + Keys.BACK_SPACE);
        $("[data-test-id=from] input").val(String.valueOf(DataHelper.getSecondCardInfo()));
        $("[data-test-id=action-transfer] span").click();

        // проверка
        errorNotification.shouldBe(visible, ofSeconds(10));
        $("[data-test-id=error-notification] .notification__content").shouldHave(text("Ошибка!"));
    }


    @Test
    public void shouldNotTransferMoneyFromNotExistingCard() {
        // авторизация
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verifyInfo = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verifyInfo);

        // возврат изначального баланса на картах
        DataHelper.returnInitialBalance();

        // перевод средств
        var transferPage = dashboardPage.transferMoneyTo("0001");
        var amount = DataHelper.getRandomAmount(10000);
        transferPage.invalidTransferFrom(amount, DataHelper.getAnotherCardInfo());

        // проверка
        errorNotification.shouldBe(visible);
        $("[data-test-id=error-notification] .notification__content").shouldHave(text("Ошибка!"));
    }

    // Падающий тест
    @Test
    public void shouldNotTransferMoneyBetweenSameCard() {
        // авторизация
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verifyInfo = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verifyInfo);

        // возврат изначального баланса на картах
        DataHelper.returnInitialBalance();

        // перевод средств
        var transferPage = dashboardPage.transferMoneyTo("0001");
        var amount = DataHelper.getRandomAmount(10000);
        transferPage.invalidTransferFrom(amount, DataHelper.getFirstCardInfo());

        // проверка
        errorNotification.shouldBe(visible);
        $("[data-test-id=error-notification] .notification__content").shouldHave(text("Ошибка!"));
    }
}
