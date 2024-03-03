package tinkoff.investment.bot.tinkoffInvestmentBot.service.stock;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import io.github.dankosik.starter.invest.annotation.marketdata.HandleAllLastPrices;

import ru.tinkoff.piapi.contract.v1.LastPrice;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import io.github.dankosik.starter.invest.contract.marketdata.lastprice.AsyncLastPriceHandler;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@HandleAllLastPrices(afterEachLastPriceHandler = true)
class StockManager implements AsyncLastPriceHandler {

    private final FigiToTickerConverter converter;

    private final R2dbcEntityTemplate template;

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

        String figi = lastPrice.getFigi();
        String ticker = converter.findTickerByFigi(figi);

        return CompletableFuture.runAsync(() -> updateStockPrice(ticker, price));
    }

    public void updateStockPrice(String ticker, String price) {

        template.getDatabaseClient()
                .sql(String.format(
                        "INSERT INTO stock (ticker, price) VALUES ('%s', '%s') " +
                        "ON CONFLICT (ticker) DO UPDATE SET price = EXCLUDED.price;",
                        ticker, price))
                .fetch()
                .rowsUpdated()
                .block();
    }
}
