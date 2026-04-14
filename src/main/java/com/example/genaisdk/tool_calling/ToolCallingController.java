package com.example.genaisdk.tool_calling;

import com.example.genaisdk.dto.UserInput;
import com.example.genaisdk.tool_calling.currency.CurrencyTools;
import com.example.genaisdk.tool_calling.currenttime.DateTimeTools;
import com.example.genaisdk.tool_calling.weather.WeatherConfigProperties;
import com.example.genaisdk.tool_calling.weather.WeatherToolsFunction;
import com.example.genaisdk.tool_calling.weather.dtos.WeatherRequest;
import com.example.genaisdk.tool_calling.weather.dtos.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
//import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.function.Function;

@RestController
public class ToolCallingController {
    private static final Logger log = LoggerFactory.getLogger(ToolCallingController.class);

    private final ChatClient chatClient;

    private final CurrencyTools currencyTools;

    //private final GoogleGenAiChatModel openAiChatModel;
    private final AzureOpenAiChatModel openAiChatModel;

    //private final Function<WeatherRequest, WeatherResponse> weatherToolsFunction;

    public ToolCallingController(ChatClient.Builder builder,
            WeatherConfigProperties weatherConfigProperties,
            //GoogleGenAiChatModel openAiChatModel,
            AzureOpenAiChatModel openAiChatModel,
            CurrencyTools currencyTools/*, Function<WeatherRequest, WeatherResponse> weatherToolsFunction*/) {

        var toolCallback = FunctionToolCallback
                .builder("current_weather", new WeatherToolsFunction(weatherConfigProperties))
                .description("Get the current weather for a given location")
                .inputType(WeatherRequest.class)
                .build();

        this.chatClient = builder
                .defaultSystem("You are a helpful AI Assistant that can access tools if needed to answer user questions!.")
                //.defaultToolCallbacks(toolCallback)
                .defaultToolNames("weatherToolsFunction")
                .build();

        this.openAiChatModel = openAiChatModel;
        this.currencyTools = currencyTools;
        //this.weatherToolsFunction = weatherToolsFunction;
    }

    @PostMapping("/v1/tool_calling")
    public String toolCalling(@RequestBody UserInput userInput,
            @RequestHeader(value = "USER_ID", required = false) String userId) {

        var tools = ToolCallbacks.from(
                new DateTimeTools(),
                currencyTools);

        return chatClient.prompt()
                .user(userInput.prompt())
                .toolCallbacks(tools)
                .toolContext(Map.of("userId", userId))
                .call()
                .content();
    }

    @PostMapping("/v2/tool_calling/custom")
    public ChatResponse toolCallingCustom(@RequestBody UserInput userInput) {

//        ToolCallback[] tools = ToolCallbacks.from(new DateTimeTools());
        ToolCallingManager toolCallingManager = ToolCallingManager.builder().build();

        ChatOptions chatOptions = ToolCallingChatOptions.builder()
//                .toolCallbacks(tools)
                .internalToolExecutionEnabled(false)
                .build();
        Prompt prompt = new Prompt(userInput.prompt(), chatOptions);

        ChatResponse chatResponse = openAiChatModel.call(prompt);
        log.info(" chatResponse : {} ", chatResponse);
        while (chatResponse.hasToolCalls()) {
            ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, chatResponse);

            prompt = new Prompt(toolExecutionResult.conversationHistory(), chatOptions);

            chatResponse = openAiChatModel.call(prompt);
        }

        return chatResponse;
    }
}
