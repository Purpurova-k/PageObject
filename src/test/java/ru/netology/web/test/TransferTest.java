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

        // перевод средств и проверка на уведомление об ошибке
        var transferPage = dashboardPage.transferMoneyTo("0001");
        transferPage.invalidTransferFrom(15000, DataHelper.getSecondCardInfo());
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
        transferPage.errorNotificationShouldBeVisible();
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
        transferPage.errorNotificationShouldBeVisible();
    }
}
