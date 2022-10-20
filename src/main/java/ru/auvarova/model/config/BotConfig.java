package ru.auvarova.model.config;

import lombok.Data;
@Data
public class BotConfig {

    String botName;


    String token;

    public BotConfig(String botName, String token){
        this.token = token;
        this.botName = botName;
    }
}

