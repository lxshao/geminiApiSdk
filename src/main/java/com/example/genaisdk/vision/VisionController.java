package com.example.genaisdk.vision;

import com.example.genaisdk.dto.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ChatModelCallAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class VisionController {
    private static final Logger log = LoggerFactory.getLogger(VisionController.class);

    private static final String UPLOAD_DIR = "explore-openai/src/main/resources/uploaded_images";

    private final ChatClient chatClient;

    //@Autowired
    private final AzureOpenAiChatModel azureChatModel;


    public VisionController(ChatClient.Builder chatClientBuilder, AzureOpenAiChatModel azureChatModel) {

        this.azureChatModel = azureChatModel;
        var callAdvisor = ChatModelCallAdvisor.builder()
                .chatModel(azureChatModel)
                .build();

        this.chatClient = chatClientBuilder
                .defaultAdvisors(callAdvisor, SimpleLoggerAdvisor.builder().build())
                .build();
    }

    @PostMapping("/v1/vision")
    public String vision(@RequestBody UserInput userInput) {
        log.info("userInput message: {}", userInput);
        var imageResource = new ClassPathResource("files/vision/zebra.jpg");
        var userMessage = UserMessage.builder().text("Explain what do you see in this image?")
                .media(new Media(MimeTypeUtils.IMAGE_JPEG, imageResource))
                .build();
        var response = chatClient.prompt(new Prompt(userMessage)).call();
        return response.content();
    }

    @PostMapping(value = "/v2/vision")
    public String visionV2(
            @RequestParam("file") MultipartFile file,
            @RequestParam("prompt") String prompt
    ) {

        try {
            // Get the original filename
            String fileName = file.getOriginalFilename();
            log.info("Uploaded File name is :  {} " , fileName);

            var userMessage = UserMessage.builder()
                    .text(prompt)
                    .media(new Media(MimeTypeUtils.IMAGE_JPEG, file.getResource()))
                    .build();

            var response = chatClient
                    .prompt(new Prompt(userMessage))
                    .call();
            log.info("response : {} ", response.chatResponse());
            return response.content();
        } catch (Exception e) {
            log.error("Exception is : {} ", e.getMessage(), e);
            throw e;
        }
    }

}
