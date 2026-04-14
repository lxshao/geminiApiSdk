package com.example.genaisdk.image;

import com.example.genaisdk.dto.ImageInput;
import com.example.genaisdk.dto.UserInput;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.azure.openai.AzureOpenAiImageModel;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.example.genaisdk.utils.ImageUtil.decodeBase64ToImage;
import static com.example.genaisdk.utils.ImageUtil.saveImageToFile;

/**
 * https://platform.openai.com/docs/api-reference/images/create
 */
@RestController
public class ImageController {
    private static final Logger log = LoggerFactory.getLogger(ImageController.class);
    public AzureOpenAiImageModel openAiImageModel;


    public ImageController(AzureOpenAiImageModel openAiImageModel) {
        this.openAiImageModel = openAiImageModel;
    }

    @PostMapping("/v1/images")
    public ImageResponse images(@Valid @RequestBody UserInput userInput) {
        log.info("Received request to upload image for user {}", userInput);
        var response = openAiImageModel.call(new ImagePrompt(userInput.prompt()));


        return response;
    }
}
