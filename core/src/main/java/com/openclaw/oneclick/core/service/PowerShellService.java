package com.openclaw.oneclick.core.service;

import java.util.List;

public class PowerShellService {
    public List<String> wrap(String script) {
        return List.of("powershell.exe", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", script);
    }
}
