package ru.auvarova.config;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.auvarova.service.TelegramBot;

@Component
public class BotInitializer {
    BotConfig config;
    public BotInitializer(BotConfig config){
        this.config = config;
    }
    public void init() throws TelegramApiException{
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot( new TelegramBot(config) );
        }
        catch (TelegramApiException e) {
            throw new RuntimeException();
        }
    }
}
