package tinkoff.investment.bot.tinkoffInvestmentBot.service.stock.price;

import org.jetbrains.annotations.NotNull;
import io.github.dankosik.starter.invest.annotation.marketdata.HandleLastPrice;

import ru.tinkoff.piapi.contract.v1.LastPrice;
import io.github.dankosik.starter.invest.contract.marketdata.lastprice.BlockingLastPriceHandler;

@HandleLastPrice(ticker = "SOFL")
class SOFL implements BlockingLastPriceHandler {

    @Override
    public void handleBlocking(@NotNull LastPrice lastPrice) {}
}
