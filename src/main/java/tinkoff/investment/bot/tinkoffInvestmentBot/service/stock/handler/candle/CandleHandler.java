package tinkoff.investment.bot.tinkoffInvestmentBot.service.stock.handler.candle;

import tinkoff.investment.bot.tinkoffInvestmentBot.model.dto.CandleDTO;
import tinkoff.investment.bot.tinkoffInvestmentBot.model.enums.CandleType;
import tinkoff.investment.bot.tinkoffInvestmentBot.utils.SearchInStockList;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import io.github.dankosik.starter.invest.annotation.marketdata.HandleAllCandles;

import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.SubscriptionInterval;
import io.github.dankosik.starter.invest.contract.marketdata.candle.AsyncCandleHandler;

import java.util.concurrent.CompletableFuture;

@HandleAllCandles
(
    tickers = {"ROSN"},
    subscriptionInterval = SubscriptionInterval.SUBSCRIPTION_INTERVAL_FIVE_MINUTES
)
@RequiredArgsConstructor
class CandleHandler implements AsyncCandleHandler {

    private final SearchInStockList search;

    @NotNull
    @Override
    public CompletableFuture<Void> handleAsync(@NotNull Candle candle) {

        CandleDTO candleDTO;

        String figi = candle.getFigi();
        String ticker = search.getTickerByFigi(figi);

        Double openRubles = (double) candle.getOpen().getUnits();
        Double openKopecks = (double) candle.getOpen().getNano() / 1000000000;
        Double openPrice = openRubles + openKopecks;

        Double closeRubles = (double) candle.getClose().getUnits();
        Double closeKopecks = (double) candle.getClose().getNano() / 1000000000;
        Double closePrice = closeRubles + closeKopecks;

        Double highShadowRubles = (double) candle.getHigh().getUnits();
        Double highShadowKopecks = (double) candle.getHigh().getNano() / 1000000000;
        Double highShadow = highShadowRubles + highShadowKopecks;

        Double lowShadowRubles = (double) candle.getLow().getUnits();
        Double lowShadowKopecks = (double) candle.getLow().getNano() / 1000000000;
        Double lowShadow = lowShadowRubles + lowShadowKopecks;

//        System.out.println("OPEN PRICE: " + openPrice + "\n" + "CLOSE PRICE: " + closePrice + "\n" +
//                "HIGH SHADOW: " + highShadow + "\n" + "LOW SHADOW: " + lowShadow);

        if (openPrice < closePrice) {

            candleDTO = CandleDTO.builder()
                    .ticker(ticker)
                    .openPrice(openPrice)
                    .closePrice(closePrice)
                    .highShadow(highShadow)
                    .lowShadow(lowShadow)
                    .candleType(CandleType.BULL_CANDLE)
                    .build();
        }
        else if (openPrice > closePrice) {

            candleDTO = CandleDTO.builder()
                    .ticker(ticker)
                    .openPrice(openPrice)
                    .closePrice(closePrice)
                    .highShadow(highShadow)
                    .lowShadow(lowShadow)
                    .candleType(CandleType.BEAR_CANDLE)
                    .build();
        }
        else {
            candleDTO = null;
        }

        return CompletableFuture.runAsync(() -> candleAnalysis(candleDTO));
    }

    public void candleAnalysis(CandleDTO candleDTO) {}
}
