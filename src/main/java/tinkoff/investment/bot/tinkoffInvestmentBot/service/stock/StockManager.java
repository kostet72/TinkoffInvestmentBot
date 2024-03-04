package tinkoff.investment.bot.tinkoffInvestmentBot.service.stock;

import tinkoff.investment.bot.tinkoffInvestmentBot.model.entity.Stock;
import tinkoff.investment.bot.tinkoffInvestmentBot.utils.FigiToTickerConverter;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import io.github.dankosik.starter.invest.annotation.marketdata.HandleAllLastPrices;

import reactor.core.publisher.Mono;
import ru.tinkoff.piapi.contract.v1.LastPrice;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import io.github.dankosik.starter.invest.contract.marketdata.lastprice.AsyncLastPriceHandler;

import java.util.concurrent.CompletableFuture;

import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Criteria.where;

@RequiredArgsConstructor
@HandleAllLastPrices(tickers = {"AQUA", "FIXP", "LKOH", "POLY", "ROSN", "SOFL", "SVCB", "TRMK"}, afterEachLastPriceHandler = true)
public class StockManager implements AsyncLastPriceHandler {

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

    public Mono<String> getStockPrice(String ticker) {

        return template.selectOne(query(where("ticker").is(ticker)), Stock.class)
                .flatMap(stock -> Mono.just(String.valueOf(stock.getPrice())))
                .switchIfEmpty(Mono.just("""
                        Не смог найти акцию с таким тикером. Убедитесь, что ввели тикер верно. Пример правильного использования:
                        "/price LKOH"

                        Если проблема осталась, то посмотрите в директории:
                        "./src/main/java/service/stock/price" на наличие необходимой
                        вам акции. Если таковая отсутсвует, то добавьте её, следуя инструкции на главной странице репозитория
                        (https://github.com/kostet72/TinkoffInvestmentBot)
                        """));
    }
}
