package ru.netology.web.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.val;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    private SelenideElement heading = $("h1");
    private ElementsCollection cards = $$(".list__item");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";


    public DashboardPage() {
        heading.shouldBe(visible).shouldHave(text("Ваши карты"));
    }

    public int getCardBalance(String lastNumberCard) {
        val balance = cards.findBy(text(lastNumberCard)).text();
        val start = balance.indexOf(balanceStart);
        val finish = balance.indexOf(balanceFinish);
        val value = balance.substring(start + balanceStart.length(), finish).trim();
        return Integer.parseInt(value);
    }

    public TransferPage transferMoneyTo(String lastNumbersCard) {
        var replenishButton = cards.findBy(text(lastNumbersCard)).$("[data-test-id=action-deposit]");
        replenishButton.click();
        return new TransferPage();
    }
}
