package com.bolara.uno_client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Hand(String username, List<Card> cards, boolean calledUno) {}
