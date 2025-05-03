package com.bolara.uno_client.dto;

public record PlayCheatCardRequest(
        int playerIndex,
        Card card
) {}
