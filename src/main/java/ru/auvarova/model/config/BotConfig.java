package ru.auvarova.model.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

//@Configuration
@Data
//@PropertySource("properties")
public class BotConfig {
   //@Value("${bot.name}")
    String botName;

    //@Value("${bot.token}")
    String token;

    public BotConfig(String botName, String token){
        this.token = token;
        this.botName = botName;
    }
}

