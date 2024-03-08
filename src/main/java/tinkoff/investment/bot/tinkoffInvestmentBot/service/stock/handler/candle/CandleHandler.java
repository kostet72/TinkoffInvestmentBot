package tinkoff.investment.bot.tinkoffInvestmentBot.service.stock.handler.candle;

import tinkoff.investment.bot.tinkoffInvestmentBot.utils.UserInfo;
import tinkoff.investment.bot.tinkoffInvestmentBot.model.dto.CandleDTO;
import tinkoff.investment.bot.tinkoffInvestmentBot.model.enums.CandleType;
import tinkoff.investment.bot.tinkoffInvestmentBot.service.bot.BotService;
import tinkoff.investment.bot.tinkoffInvestmentBot.utils.SearchInStockList;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import io.github.dankosik.starter.invest.annotation.marketdata.HandleAllCandles;

import reactor.core.publisher.Mono;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.SubscriptionInterval;
import io.github.dankosik.starter.invest.contract.marketdata.candle.AsyncCandleHandler;

import java.util.concurrent.CompletableFuture;

@HandleAllCandles
(
    tickers = {"ROSN"},
    subscriptionInterval = SubscriptionInterval.SUBSCRIPTION_INTERVAL_FIFTEEN_MINUTES
)
@RequiredArgsConstructor
class CandleHandler implements AsyncCandleHandler {

    private final BotService bot;

    private final SearchInStockList search;

    private final UserInfo userInfo;

    @NotNull
    @Override
    public CompletableFuture<Void> handleAsync(@NotNull Candle candle) {

        String figi = candle.getFigi();
        String ticker = search.getTickerByFigi(figi);
        String nameOfTheCompany = search.getNameByTicker(ticker);

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

        Double percentChange = Math.abs((openPrice - closePrice) / openPrice * 100);

        CandleDTO candleDTO = CandleDTO.builder()
                .nameOfTheCompany(nameOfTheCompany)
                .openPrice(openPrice)
                .closePrice(closePrice)
                .highShadow(highShadow)
                .lowShadow(lowShadow)
                .percentChange(percentChange)
                .build();

        return CompletableFuture.runAsync(() -> candleAnalysis(candleDTO));
    }

    public void candleAnalysis(CandleDTO candleDTO) {

        String nameOfTheCompany = candleDTO.getNameOfTheCompany();
        Double openPrice = candleDTO.getOpenPrice();
        Double closePrice = candleDTO.getClosePrice();
        Double highShadow = candleDTO.getHighShadow();
        Double lowShadow = candleDTO.getLowShadow();
        Double percentChange = candleDTO.getPercentChange();

        if (openPrice < closePrice && percentChange >= 1) {

            Double highToClose = Math.abs((highShadow - closePrice) / highShadow * 100);
            Double lowToOpen = Math.abs((openPrice - lowShadow) / openPrice * 100);

            if (highToClose < 0.1 && lowToOpen < 0.1) {

                candleDTO = CandleDTO.builder()
                        .nameOfTheCompany(nameOfTheCompany)
                        .percentChange(percentChange)
                        .candleType(CandleType.MARIBOZU_BULL)
                        .build();
                notifyAboutPriceChange(candleDTO);
            }
//            else if () {}
        }
        else if (openPrice > closePrice && percentChange >= 1) {

            Double highToOpen = Math.abs((highShadow - openPrice) / highShadow * 100);
            Double lowToClose = Math.abs((closePrice - lowShadow) / closePrice * 100);

            if (highToOpen < 0.1 && lowToClose < 0.1) {

                candleDTO = CandleDTO.builder()
                        .nameOfTheCompany(nameOfTheCompany)
                        .percentChange(percentChange)
                        .candleType(CandleType.MARIBOZU_BEAR)
                        .build();
                notifyAboutPriceChange(candleDTO);
            }
//            else if () {}
        }
    }

    public void notifyAboutPriceChange(CandleDTO candleDTO) {

        switch (candleDTO.getCandleType()) {

            case MARIBOZU_BULL -> sendNotification("Сильное изменение котировок \"" + candleDTO.getNameOfTheCompany() + "\". " +
                    "Рост на " + candleDTO.getPercentChange() + "процента!");

            case MARIBOZU_BEAR -> sendNotification("Сильное изменение котировок \"" + candleDTO.getNameOfTheCompany() + "\". " +
                    "Падение на " + candleDTO.getPercentChange() + "процента!");
        }
    }

    private void sendNotification(String answer) {

        userInfo.getChatIdFromUsers()
                .flatMap(chatId -> {

                    bot.sendMessage(chatId, answer);
                    return Mono.empty();
                });
    }
}
