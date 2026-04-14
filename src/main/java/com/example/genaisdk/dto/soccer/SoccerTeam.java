package com.example.genaisdk.dto.soccer;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"country", "players"})
public record SoccerTeam(String country, List<String> players) {
}
