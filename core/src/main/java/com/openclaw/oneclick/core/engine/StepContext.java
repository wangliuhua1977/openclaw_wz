package com.openclaw.oneclick.core.engine;

import com.openclaw.oneclick.core.exec.CommandRunner;
import com.openclaw.oneclick.core.model.DeploymentSettings;
import com.openclaw.oneclick.core.service.TokenReader;
import com.openclaw.oneclick.core.service.WslService;
import com.openclaw.oneclick.core.state.StateStore;

public record StepContext(
        DeploymentSettings settings,
        CommandRunner commandRunner,
        WslService wslService,
        StateStore stateStore,
        TokenReader tokenReader,
        StepOutput output,
        String openAiApiKey
) {
}
