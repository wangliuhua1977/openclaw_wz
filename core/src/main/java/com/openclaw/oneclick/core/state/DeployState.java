package com.openclaw.oneclick.core.state;

import com.openclaw.oneclick.core.model.DeploymentSettings;

public record DeployState(String currentStepId, DeploymentSettings settings, String lastError) {
}
