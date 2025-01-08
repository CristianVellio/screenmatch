package com.cristianvellio.screenmatch.service;

import com.cristianvellio.config.Config;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

public class ConsultaChatGPT {
    private static final String API_GPT = Config.getApiKeyOpenAi();

    public static String obtenerTraduccion(String texto) {
        OpenAiService service = new OpenAiService(API_GPT);

        CompletionRequest peticion = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt("traduce a español el siguiente texto: " + texto)
                .maxTokens(1000)
                .temperature(0.7)
                .build();

        var respuesta = service.createCompletion(peticion);
        return respuesta.getChoices().get(0).getText();
    }
}
