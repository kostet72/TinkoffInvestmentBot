package tinkoff.investment.bot.tinkoffInvestmentBot.service.stock;

import tinkoff.investment.bot.tinkoffInvestmentBot.model.entity.Stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Criteria.where;

@Service
@RequiredArgsConstructor
public class StockService {

    private final R2dbcEntityTemplate template;

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
