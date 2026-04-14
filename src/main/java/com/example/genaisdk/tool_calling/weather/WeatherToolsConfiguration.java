package com.example.genaisdk.tool_calling.weather;

import com.example.genaisdk.tool_calling.weather.dtos.WeatherRequest;
import com.example.genaisdk.tool_calling.weather.dtos.WeatherResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration(proxyBeanMethods = false)
public class WeatherToolsConfiguration {
    private final WeatherConfigProperties weatherConfigProperties;

    public WeatherToolsConfiguration(WeatherConfigProperties weatherConfigProperties) {
        this.weatherConfigProperties = weatherConfigProperties;
    }

    @Bean
    @Description("Get the current weather for a given location")
    public Function<WeatherRequest, WeatherResponse> weatherToolsFunction() {
        return new WeatherToolsFunction(weatherConfigProperties);
    }

}
