package com.openclaw.oneclick.core.service;

import com.openclaw.oneclick.core.engine.Step;
import com.openclaw.oneclick.core.engine.StepContext;
import com.openclaw.oneclick.core.engine.StepEngine;
import com.openclaw.oneclick.core.engine.StepOutput;
import com.openclaw.oneclick.core.exec.CommandRunner;
import com.openclaw.oneclick.core.model.DeploymentSettings;
import com.openclaw.oneclick.core.model.StepState;
import com.openclaw.oneclick.core.state.StateStore;
import com.openclaw.oneclick.core.steps.*;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public class OpenClawOrchestrator {
    private final CommandRunner commandRunner = new CommandRunner();

    public List<StepState> deploy(DeploymentSettings settings, String apiKey, StepOutput output, String resumeStep) throws Exception {
        StateStore stateStore = new StateStore(defaultStatePath(), settings);
        StepContext context = new StepContext(settings, commandRunner, new WslService(commandRunner), stateStore, new TokenReader(), output, apiKey);
        List<Step> steps = List.of(
                new PrecheckStep(),
                new WslInstallStep(),
                new CloudInitStep(),
                new InstallOpenClawStep(),
                new OnboardStep(),
                new TokenStep(),
                new GatewayStep(),
                new SecurityStep()
        );
        return new StepEngine(steps).run(context, resumeStep);
    }

    public String oneClickStart(DeploymentSettings settings, StepOutput output) throws Exception {
        WslService wsl = new WslService(commandRunner);
        wsl.runWsl(settings.distro(), "openclaw gateway status || openclaw gateway start", Duration.ofMinutes(2), (line, err) -> output.append(line));
        var tokenResult = wsl.runWsl(settings.distro(), "cat ~/.openclaw/openclaw.json", Duration.ofSeconds(30), (line, err) -> {});
        String token = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(tokenResult.stdout())
                .path("gateway").path("auth").path("token").asText();
        return com.openclaw.oneclick.core.util.DashboardUrlBuilder.build(settings.gatewayPort(), token);
    }

    public Path defaultStatePath() {
        return Path.of(System.getenv().getOrDefault("APPDATA", System.getProperty("user.home")), "OpenClawOneClick", "state.json");
    }
}
