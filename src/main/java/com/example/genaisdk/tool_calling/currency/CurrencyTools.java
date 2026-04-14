package com.example.genaisdk.tool_calling.currency;

import com.example.genaisdk.tool_calling.currency.dtos.CurrencyRequest;
import com.example.genaisdk.tool_calling.currency.dtos.CurrencyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class CurrencyTools {
    private static final Logger log = LoggerFactory.getLogger(CurrencyTools.class);

    private final RestClient restClient;
    private final CurrencyExchangeConfigProperties currencyExchangeConfigProperties;

    public CurrencyTools(CurrencyExchangeConfigProperties currencyExchangeConfigProperties) {
        this.restClient = RestClient.create(currencyExchangeConfigProperties.baseUrl());
        this.currencyExchangeConfigProperties = currencyExchangeConfigProperties;
    }

    @Tool(name = "get_currency_rates", description = "Get the latest currency exchange rates for specified " +
            "currencies", returnDirect = true)
    public CurrencyResponse getCurrencyRates(CurrencyRequest currencyRequest, ToolContext toolContext) {
        log.info("RestClient CurrencyTools - getCurrencyRates called: {}", currencyRequest);
        if (toolContext != null) {
            var userId = toolContext.getContext().get("userId");
            log.info("ToolContext userId: {}", userId);
        }
        try {
            var response = restClient.get().uri("/latest.json?app_id={key}&base={base}&symbols" +
                                    "={symbols}",
                            currencyExchangeConfigProperties.apiKey(),
                            currencyRequest.base(),
                            currencyRequest.symbols())
                    .retrieve()
                    .body(CurrencyResponse.class);
            log.info("response: {}", response);
            return response;
        }catch (RestClientException e) {
            log.error("Error fetching currency rates: {}", e.getMessage(), e);
            throw e;
        }
    }
}