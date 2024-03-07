package tinkoff.investment.bot.tinkoffInvestmentBot.service.bot;

import tinkoff.investment.bot.tinkoffInvestmentBot.model.entity.User;
import tinkoff.investment.bot.tinkoffInvestmentBot.utils.SearchInStockList;
import tinkoff.investment.bot.tinkoffInvestmentBot.service.stock.StockService;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import reactor.core.publisher.Mono;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Update.update;
import static org.springframework.data.relational.core.query.Criteria.where;

@Slf4j
@Service
@Component
@RequiredArgsConstructor
public class BotService extends TelegramLongPollingBot {

    private final SearchInStockList search;

    private final StockService stockService;

    private final R2dbcEntityTemplate template;

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
            String userTag = update.getMessage().getFrom().getUserName();
            long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("/price")) {

                String ticker = messageText.substring(7);
                getLastPriceCommand(chatId, ticker);
            }
            else {
                switch (messageText) {

                    case "/start":
                        startCommand(chatId, update.getMessage().getChat().getFirstName(), userTag);
                        break;

                    case "/help":
                        helpCommand(chatId);
                        break;

                    default:
                        sendMessage(chatId, "Я не понимаю вас. Обратитесь к команде /help, чтобы узнать о доступных командах");
                        break;
                }
            }
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

    private void startCommand(long chatId, String userFirstName, String userTag) {

        Mono.just(userTag)
                .flatMap(tag -> template.exists(query(where("tag").is(tag)), User.class))
                .flatMap(tagExists -> {

                    if (Boolean.TRUE.equals(tagExists)) {

                        String answer = "Здравствуйте, " + userFirstName + "!\nНашёл вас в списке наших пользователей. С возвращением!\n\n" +
                                "Напоминаю, что вы можете ознакомиться со списком возможностей бота, использовав команду /help";
                        sendMessage(chatId, answer);

                        return template.update(query(where("tag").is(userTag)),
                                update("chat_id", chatId),
                                User.class);
                    }
                    else {
                        String answer = "Здравствуйте, " + userFirstName + "!\n" +
                                "Я буду вашим небольшим помощником в области инвестиций.\n\n" +
                                "Предлагаю вам ознакомьтесь с возможностями бота, использовав команду /help";
                        sendMessage(chatId, answer);

                        User user = User.builder()
                                .tag(userTag)
                                .chatId(String.valueOf(chatId))
                                .build();

                        return template.insert(user);
                    }
                }).subscribe();
    }

    public void helpCommand(long chatId) {

        String answer = """
                Доступные на данный момент команды:

                /price {}   -   получить текущую цену акции.
                Пример использования:   "/price LKOH"
                """;
        sendMessage(chatId, answer);
    }

    public void getLastPriceCommand(long chatId, String ticker) {

        String nameOfTheCompany = search.getNameByTicker(ticker);
        String answer = "Котировки \"" + nameOfTheCompany + "\" сейчас находятся на уровне: " + stockService.getStockPrice(ticker).block();
        sendMessage(chatId, answer);
    }
}
