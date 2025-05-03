package com.bolara.uno_client.dto;

import java.util.List;

public record Hand(String username, List<Card> cards, boolean calledUno) {}
