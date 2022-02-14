package ru.netology.web.data;

import lombok.Value;
import ru.netology.web.page.DashboardPage;

import java.util.Random;

public class DataHelper {
    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    public static AuthInfo getOtherAuthInfo(AuthInfo original) {
        return new AuthInfo("petya", "123qwerty");
    }

    @Value
    public static class VerificationCode {
        private String code;
    }

    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) {
        return new VerificationCode("12345");
    }

    public static VerificationCode getAnotherVerificationCodeFor(AuthInfo authInfo) {
        return new VerificationCode("54321");
    }

    @Value
    public static class CardInfo {
        private String number;
    }

    public static CardInfo getFirstCardInfo() {
        return new CardInfo("5559 0000 0000 0001");
    }

    public static CardInfo getSecondCardInfo() {
        return new CardInfo("5559 0000 0000 0002");
    }

    public static CardInfo getAnotherCardInfo() {
        return new CardInfo("5559 0000 0000 0000");
    }


    public static int getRandomAmount(int balance) {
        Random random = new Random();
        int amount = random.nextInt(balance);
        return amount;
    }


    public static void returnInitialBalance() {
        var dashboardPage = new DashboardPage();
        var balanceFirstCard = dashboardPage.getCardBalance("0001");
        var balanceSecondCard = dashboardPage.getCardBalance("0002");
        if (balanceFirstCard == balanceSecondCard) {
            return;
        } else if (balanceFirstCard > balanceSecondCard) {
            int difference = balanceFirstCard - balanceSecondCard;
            var replenishmentPage = dashboardPage.transferMoneyTo("0002");
            replenishmentPage.validTransferFrom(difference / 2, getFirstCardInfo());
        } else if (balanceSecondCard > balanceFirstCard) {
            int difference = balanceSecondCard - balanceFirstCard;
            var replenishmentPage = dashboardPage.transferMoneyTo("0001");
            replenishmentPage.validTransferFrom(difference / 2, DataHelper.getSecondCardInfo());
        }
    }
}
