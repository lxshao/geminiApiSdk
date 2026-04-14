package com.example.genaisdk.chats;

import com.example.genaisdk.dto.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class PromptController {


    private static final Logger log = LoggerFactory.getLogger(PromptController.class);
    private final ChatClient chatClient;


    @Value("classpath:/prompt-templates/java-coding-assistant.st")
    private Resource systemTemplateMessage;

    @Value("classpath:/prompt-templates/coding-assistant.st")
    private Resource systemText;


    public PromptController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping("/v1/prompts")
    public String prompts(@RequestBody UserInput userInput) {
        log.info("userInput message: {}", userInput.prompt());

        var systemMessage = new SystemMessage(systemTemplateMessage);
        var userMessage = new UserMessage(userInput.prompt());

        var promptMessage = new Prompt(List.of(systemMessage, userMessage));

        return chatClient.prompt(promptMessage)
                .call()
                .content();
    }

    @PostMapping("/v1/prompts/{language}")
    public String promptsByLanguage(
            @PathVariable String language,
            @RequestBody UserInput userInput) {
        log.info("userInput message: {}, language: {}", userInput.prompt(), language);

        var systemPromptTemplate = new SystemPromptTemplate(systemText);
        var sysMessage = systemPromptTemplate.createMessage(Map.of("language", language));

        var userMessage = new UserMessage(userInput.prompt());

        var promptMessage = new Prompt(List.of(sysMessage, userMessage));

        return chatClient.prompt(promptMessage)
                .call()
                .content();
    }

}
