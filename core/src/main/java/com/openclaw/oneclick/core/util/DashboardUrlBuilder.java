package com.openclaw.oneclick.core.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class DashboardUrlBuilder {
    private DashboardUrlBuilder() {
    }

    public static String build(int port, String token) {
        return "http://127.0.0.1:" + port + "/?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
    }
}
