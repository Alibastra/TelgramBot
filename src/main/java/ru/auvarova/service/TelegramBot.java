package ru.auvarova.service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.auvarova.model.config.BotConfig;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TelegramBot extends TelegramLongPollingBot {
    BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    private static String currency;
    private static String date;
    private static int countDays;
    private static String algoritm;
    private static final int DAY = 1;
    private static final int WEEK = 7;
    private static final int MONTH = 30;
    private static final String HEADER = "\n\tAMD - Армянский драм " +
                                         "\n\tBGN - Болгарский лев " +
                                         "\n\tUSD - Доллар США " +
                                         "\n\tEUR - Евро " +
                                         "\n\tTRY - Турецкая лира ";
    private static final String ERROR_MSG = "Ой-ой-ой! Что-то пошло не так!\nПопробуй начать заново командой \"/start\"";
    private static final String REG_EX_DATE="^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]|(?:Jan|Mar|May|Jul|Aug|Oct|Dec)))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2]|(?:Jan|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec))\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)(?:0?2|(?:Feb))\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9]|(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep))|(?:1[0-2]|(?:Oct|Nov|Dec)))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId);
                    currency = null;
                    date = null;
                    countDays = 0;
                    algoritm = null;

                    break;
                default:
                    if (Pattern.matches(REG_EX_DATE,messageText)){
                        date = messageText;
                        selectAlgorithm(chatId);
                    }else if (currency.equals("GRAPH")){
                        currency = messageText;
                        selectPeriod(chatId);
                    } else {
                        sendMessage(chatId, ERROR_MSG);
                    }
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            String callbackDate = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackDate) {
                case "USD_BUTTON" -> {
                    replaceMessage(chatId, messageId, "Валюта: Доллар США (USD)");
                    currency = "USD";
                    selectPeriod(chatId);
                    break;
                }
                case "AMD_BUTTON" -> {
                    replaceMessage(chatId, messageId,"Валюта: Армянский драм (AMD)");
                    currency = "AMD";
                    selectPeriod(chatId);
                    break;
                }
                case "BGN_BUTTON" -> {
                    replaceMessage(chatId, messageId,"Валюта: Болгарский лев (BGN)");
                    currency = "BGN";
                    selectPeriod(chatId);
                    break;
                }
                case "TRY_BUTTON" -> {
                    replaceMessage(chatId, messageId,"Валюта: Турецкая лира (TRY)");
                    currency = "TRY";
                    selectPeriod(chatId);
                    break;
                }
                case "EUR_BUTTON" -> {
                    replaceMessage(chatId, messageId,"Валюта: Евро (EUR)");
                    currency = "EUR";
                    selectPeriod(chatId);
                    break;
                }
                case "GRAPH_BUTTON" -> {
                    replaceMessage(chatId, messageId,"Сводный график валют.");
                    currency = "GRAPH";
                    sendMessage(chatId, "Введите валюты через разделитель\nПример: USD,TRY,EUR"+HEADER);
                    break;
                }
                case "TOMORRY_BUTTON" -> {
                    replaceMessage(chatId, messageId,"Прогноз на завтра.");
                    countDays = DAY;
                    selectAlgorithm(chatId);
                    break;
                }
                case "WEEK_BUTTON" -> {
                    replaceMessage(chatId, messageId,"Прогноз на неделю.");
                    countDays = WEEK;
                    selectAlgorithm(chatId);
                    break;
                }
                case "MONTH_BUTTON" -> {
                    replaceMessage(chatId, messageId,"Прогноз на месяц.");
                    countDays = MONTH;
                    selectAlgorithm(chatId);
                    break;
                }
                case "USER_DAY_BUTTON" -> {
                    replaceMessage(chatId, messageId,"Задайте день для прогноза в формате DD.MM.YYYY:");
                    break;
                }
                case "LAST_YEAR_BUTTON" -> {
                    replaceMessage(chatId, messageId,"Алгоритм \"Прошлогодний\".");
                    algoritm = "LastYear";
                    RateService rateService = new RateService();
                    rateService.ExchangeRateForecast(currency, date, countDays, algoritm);
                    break;
                }
                case "MYSTICAL_BUTTON" -> {
                    replaceMessage(chatId, messageId,"Алгоритм \"Мистический\".");
                    algoritm = "Mystical";
                    RateService rateService = new RateService();
                    rateService.ExchangeRateForecast(currency, date, countDays, algoritm);
                    break;
                }
                case "LINE_REG_BUTTON" -> {
                    replaceMessage(chatId, messageId,"Алгоритм \"из интернета\"(Линейной регрессии).");
                    algoritm = "LineReg";
                    RateService rateService = new RateService();
                    rateService.ExchangeRateForecast(currency, date, countDays, algoritm);
                    break;
                }
            }
        }

    }

    private void startCommandReceived(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберете валюту для прогноза:" + HEADER);
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineFirst = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineSecond = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineThird = new ArrayList<>();


        var buttonAMD = new InlineKeyboardButton();
        buttonAMD.setText("AMD");
        buttonAMD.setCallbackData("AMD_BUTTON");

        var buttonBGN = new InlineKeyboardButton();
        buttonBGN.setText("BGN");
        buttonBGN.setCallbackData("BGN_BUTTON");

        var buttonUSD = new InlineKeyboardButton();
        buttonUSD.setText("USD");
        buttonUSD.setCallbackData("USD_BUTTON");

        var buttonEUR = new InlineKeyboardButton();
        buttonEUR.setText("EUR");
        buttonEUR.setCallbackData("EUR_BUTTON");

        var buttonTRY = new InlineKeyboardButton();
        buttonTRY.setText("TRY");
        buttonTRY.setCallbackData("TRY_BUTTON");

        var buttonGRAPH = new InlineKeyboardButton();
        buttonGRAPH.setText("График нескольких валют");
        buttonGRAPH.setCallbackData("GRAPH_BUTTON");

        rowInLineFirst.add(buttonAMD);
        rowInLineFirst.add(buttonBGN);
        rowInLineFirst.add(buttonTRY);

        rowInLineSecond.add(buttonEUR);
        rowInLineSecond.add(buttonUSD);

        rowInLineThird.add(buttonGRAPH);


        rowsInLine.add(rowInLineFirst);
        rowsInLine.add(rowInLineSecond);
        rowsInLine.add(rowInLineThird);

        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private void selectPeriod(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберете период для прогноза:");
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineFirst = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineSecond = new ArrayList<>();


        var buttonTomorry = new InlineKeyboardButton();
        buttonTomorry.setText("На завтра");
        buttonTomorry.setCallbackData("TOMORRY_BUTTON");

        var buttonWeek = new InlineKeyboardButton();
        buttonWeek.setText("На неделю");
        buttonWeek.setCallbackData("WEEK_BUTTON");

        var buttonMonth = new InlineKeyboardButton();
        buttonMonth.setText("На месяц");
        buttonMonth.setCallbackData("MONTH_BUTTON");

        var buttonUserDay = new InlineKeyboardButton();
        buttonUserDay.setText("Задать день самостоятельно");
        buttonUserDay.setCallbackData("USER_DAY_BUTTON");


        rowInLineFirst.add(buttonTomorry);
        rowInLineFirst.add(buttonWeek);
        rowInLineFirst.add(buttonMonth);

        rowInLineSecond.add(buttonUserDay);

        rowsInLine.add(rowInLineFirst);
        rowsInLine.add(rowInLineSecond);

        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void selectAlgorithm(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберете алгоритм прогнозирования:");
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineFirst = new ArrayList<>();
        List<InlineKeyboardButton> rowInLineSecond = new ArrayList<>();


        var buttonLastYear = new InlineKeyboardButton();
        buttonLastYear.setText("Прошлогодний");
        buttonLastYear.setCallbackData("LAST_YEAR_BUTTON");

        var buttonMystical = new InlineKeyboardButton();
        buttonMystical.setText("Мистический");
        buttonMystical.setCallbackData("MYSTICAL_BUTTON");

        var buttonLineReg = new InlineKeyboardButton();
        buttonLineReg.setText("Алгоритм Линейной регрессии");
        buttonLineReg.setCallbackData("LINE_REG_BUTTON");


        rowInLineFirst.add(buttonLastYear);
        rowInLineFirst.add(buttonMystical);

        rowInLineSecond.add(buttonLineReg);

        rowsInLine.add(rowInLineFirst);
        rowsInLine.add(rowInLineSecond);

        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }


    }

    private void sendMessage(long chatId, String textMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textMessage);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void replaceMessage(long chatId,long messageId, String textMessage) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(textMessage);
        message.setMessageId((int)messageId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
