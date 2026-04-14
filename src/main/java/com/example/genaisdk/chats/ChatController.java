package com.example.genaisdk.chats;

import com.example.genaisdk.dto.UserInput;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatClient chatClient;

    @Autowired
    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping("/v1/chats")
    public ChatResponse chat(@Valid @RequestBody UserInput userInput) {
        log.info("userInput message: {}", userInput.prompt());
        var responseSpec = chatClient.prompt().user(userInput.prompt())
                .call()
                .chatResponse();

        log.info("content: {} ", responseSpec != null ? responseSpec.getResult().getOutput().getText() : null);
        return responseSpec;
    }

    @PostMapping("/v1/chats/stream")
    public Flux<String> chatWithStream(@Valid @RequestBody UserInput userInput) {

        return chatClient.prompt().user(userInput.prompt())
                .stream()
                .content();

    }

    @PostMapping("/v2/chats")
    public ChatResponse chatV2(@Valid @RequestBody UserInput userInput) {
        log.info("userInput message: {}", userInput.prompt());
        var systemMessage = """
            You are a helpful assistant, who can content Java based questions.
            For any other questions, please respond with "I am specialized in Java related topics only."
            """;
        var responseSpec = chatClient.prompt()
                .user(userInput.prompt())
                .system(systemMessage)
                .call()
                .chatResponse();

        log.info("content: {} ", responseSpec != null ? responseSpec.getResult().getOutput().getText() : null);
        return responseSpec;
    }

}
