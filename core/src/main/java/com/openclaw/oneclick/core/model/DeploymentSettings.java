package com.openclaw.oneclick.core.model;

public record DeploymentSettings(
        String distro,
        int gatewayPort,
        String gatewayBind,
        boolean installDaemon,
        boolean enableSkills,
        boolean useInstaller,
        String authMode
) {
    public static DeploymentSettings defaults() {
        return new DeploymentSettings("Ubuntu-24.04", 18789, "loopback", true, false, true, "api-key");
    }
}
