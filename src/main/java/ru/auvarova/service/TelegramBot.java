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

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId);
                    break;
                default:
                    sendMessage(chatId, "Что-то не то");
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            String callbackDate = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackDate) {
                case "USD_BUTTON" -> {
                    String text = "Press USD";
                    EditMessageText message = new EditMessageText();
                    message.setChatId(chatId);
                    message.setText(text);
                    message.setMessageId((int) messageId);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case "AMD_BUTTON" -> {
                    String text = "Press AMD";
                    EditMessageText message = new EditMessageText();
                    message.setChatId(chatId);
                    message.setText(text);
                    message.setMessageId((int) messageId);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                    break;
                }
                case "BGN_BUTTON" -> {
                    String text = "Press BGN";
                    EditMessageText message = new EditMessageText();
                    message.setChatId(chatId);
                    message.setText(text);
                    message.setMessageId((int) messageId);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                    break;
                }
                case "TRY_BUTTON" -> {
                    String text = "Press TRY";
                    EditMessageText message = new EditMessageText();
                    message.setChatId(chatId);
                    message.setText(text);
                    message.setMessageId((int) messageId);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                    break;
                }
                case "EUR_BUTTON" -> {
                    String text = "Press EUR";
                    EditMessageText message = new EditMessageText();
                    message.setChatId(chatId);
                    message.setText(text);
                    message.setMessageId((int) messageId);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                    break;
                }
                default -> {
                    String text = "Press GRAPH";
                    EditMessageText message = new EditMessageText();
                    message.setChatId(chatId);
                    message.setText(text);
                    message.setMessageId((int) messageId);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
            }
        }

    }

    private void startCommandReceived(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберете валюту для прогноза");
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

    private void startCommandReceived(long chatId) {
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
}
