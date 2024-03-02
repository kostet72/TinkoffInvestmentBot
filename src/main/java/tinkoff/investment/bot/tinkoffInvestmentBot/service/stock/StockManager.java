package tinkoff.investment.bot.tinkoffInvestmentBot.service.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

@Service
@RequiredArgsConstructor
public class StockManager {

    private final R2dbcEntityTemplate template;

    public void updateStockPrice(String ticker, String price) {

        template.getDatabaseClient()
                .sql(String.format(
                        "INSERT INTO stock (ticker, price) " +
                        "SELECT '%s', '%s' WHERE NOT EXISTS (" +
                        "SELECT ticker FROM stock WHERE ticker = '%s');",
                        ticker, price, ticker))
                .fetch()
                .rowsUpdated()
                .block();
    }
}
