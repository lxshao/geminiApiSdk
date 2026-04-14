package com.example.genaisdk.tool_calling.currency.dtos;

import java.util.Map;

public record CurrencyResponse(
    String disclaimer,
    String license,
    long timestamp,
    String base,
    Map<String, Double> rates
) {}