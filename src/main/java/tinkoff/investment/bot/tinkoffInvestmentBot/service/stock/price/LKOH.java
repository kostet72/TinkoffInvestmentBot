package tinkoff.investment.bot.tinkoffInvestmentBot.service.stock.price;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import io.github.dankosik.starter.invest.annotation.marketdata.HandleLastPrice;

import ru.tinkoff.piapi.contract.v1.LastPrice;
import io.github.dankosik.starter.invest.contract.marketdata.lastprice.AsyncLastPriceHandler;
import tinkoff.investment.bot.tinkoffInvestmentBot.service.stock.StockManager;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@HandleLastPrice(ticker = "LKOH")
class LKOH implements AsyncLastPriceHandler {

    private final StockManager stockManager;

    @NotNull
    @Override
    public CompletableFuture<Void> handleAsync(@NotNull LastPrice lastPrice) {

        String rubles = String.valueOf(lastPrice.getPrice().getUnits());
        String kopecks = String.valueOf(lastPrice.getPrice().getNano()).replaceAll("0*$", "");

        String price;
        if (kopecks.isEmpty()) {
            price = rubles + " RUB";
        } else {
            price = rubles + "," + kopecks + " RUB";
        }

        return CompletableFuture.runAsync(() -> {

            stockManager.updateStockPrice("LKOH", price);
            System.out.println(price);
        });
    }
}
