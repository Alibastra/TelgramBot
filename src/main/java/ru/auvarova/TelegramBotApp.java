package ru.auvarova;

//import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.auvarova.model.config.BotConfig;
import ru.auvarova.model.config.BotInitializer;

public class TelegramBotApp {
    public static void main(String[] args) throws TelegramApiException {
        BotConfig config = new BotConfig("SpeculatorRateBot","5479099663:AAHTzle6kkb5bUAtXu5Hy9708-aEii1j0jA");
        BotInitializer bot = new BotInitializer(config);
        bot.init();
    }
}
