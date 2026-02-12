package com.openclaw.oneclick.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class TokenReader {
    private final ObjectMapper mapper = new ObjectMapper();

    public Optional<String> readGatewayToken(Path configPath) {
        try {
            JsonNode root = mapper.readTree(configPath.toFile());
            JsonNode token = root.path("gateway").path("auth").path("token");
            return token.isTextual() ? Optional.of(token.asText()) : Optional.empty();
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
