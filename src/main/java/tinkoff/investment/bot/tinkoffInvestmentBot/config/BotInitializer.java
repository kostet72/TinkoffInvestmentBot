package tinkoff.investment.bot.tinkoffInvestmentBot.config;

import tinkoff.investment.bot.tinkoffInvestmentBot.service.bot.BotService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Autowired;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.springframework.context.event.ContextRefreshedEvent;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class BotInitializer {

    @Autowired
    BotService bot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            log.error("Ошибка при инициализации: " + e.getMessage());
        }
    }
}
