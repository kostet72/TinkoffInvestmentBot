package tinkoff.investment.bot.tinkoffInvestmentBot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@Component
public class BotService extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            sendMessage(chatId, messageText);
        }
    }

    // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### // ### //

    public void sendMessage(long chatId, String text) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при доставке сообщения: " + e.getMessage());
        }
    }
}
