package com.example.genaisdk;

import com.example.genaisdk.tool_calling.currency.CurrencyExchangeConfigProperties;
import com.example.genaisdk.tool_calling.weather.WeatherConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({
		CurrencyExchangeConfigProperties.class,
		WeatherConfigProperties.class
})
public class GenaisdkApplication {

	public static void main(String[] args) {
		SpringApplication.run(GenaisdkApplication.class, args);
	}

}
