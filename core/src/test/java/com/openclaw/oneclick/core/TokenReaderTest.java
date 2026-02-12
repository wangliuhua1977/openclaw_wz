package com.openclaw.oneclick.core;

import com.openclaw.oneclick.core.service.TokenReader;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenReaderTest {
    @Test
    void shouldReadGatewayToken() throws Exception {
        var file = Files.createTempFile("openclaw", ".json");
        Files.writeString(file, "{\"gateway\":{\"auth\":{\"token\":\"abc123\"}}}");
        var token = new TokenReader().readGatewayToken(file).orElseThrow();
        assertEquals("abc123", token);
    }
}
