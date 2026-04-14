package com.example.genaisdk.tool_calling.weather.dtos;

public record Current(String temp_f, Condition condition, String wind_mph, String humidity) {
}
