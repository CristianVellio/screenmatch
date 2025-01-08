package com.cristianvellio.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.load();

    public static String getApiKey() {
        return dotenv.get("API_KEY");
    }

    public static String getApiKeyOpenAi() {
        return dotenv.get("API_GPT");
    }
}