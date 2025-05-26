package com.javaproject.vacancy_aggregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HhClientConfig {

    @Bean
    public WebClient hhWebClient(WebClient.Builder builder) {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024))
                .build();
        return builder
                .baseUrl("https://api.hh.ru")
                .defaultHeader(HttpHeaders.USER_AGENT, "VacancyAggregator/1.0")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();
    }
}