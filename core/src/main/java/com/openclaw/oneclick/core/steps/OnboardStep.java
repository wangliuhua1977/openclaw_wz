package com.openclaw.oneclick.core.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclaw.oneclick.core.engine.StepContext;
import com.openclaw.oneclick.core.exec.CommandResult;

import java.time.Duration;

public class OnboardStep extends BaseStep {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override public String id() { return "onboard"; }
    @Override public String title() { return "Step 5: 非交互式 Onboarding"; }

    @Override
    public void run(StepContext context) throws Exception {
        if (context.openAiApiKey() == null || context.openAiApiKey().isBlank()) {
            throw new IllegalStateException("请在 UI 中提供 OPENAI_API_KEY 后重试。");
        }
        String skillsFlag = context.settings().enableSkills() ? "" : " --skip-skills";
        String cmd = "OPENAI_API_KEY='" + escape(context.openAiApiKey()) + "' openclaw onboard --non-interactive " +
                "--mode local --openai-api-key \"$OPENAI_API_KEY\" --workspace ~/.openclaw/workspace " +
                "--gateway-port " + context.settings().gatewayPort() + " --gateway-bind " + context.settings().gatewayBind() +
                " --daemon-runtime node " + (context.settings().installDaemon() ? " --install-daemon" : "") + skillsFlag + " --json";

        CommandResult result = runWsl(context, cmd, Duration.ofMinutes(15));
        ensureOk(result, "Onboarding 失败");

        JsonNode parsed = mapper.readTree(result.stdout().isBlank() ? "{}" : result.stdout());
        context.output().append("Onboarding 完成: " + parsed.path("status").asText("ok"));
    }

    private String escape(String s) {
        return s.replace("'", "'\\''");
    }
}
