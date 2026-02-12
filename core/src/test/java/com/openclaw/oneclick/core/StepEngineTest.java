package com.openclaw.oneclick.core;

import com.openclaw.oneclick.core.engine.*;
import com.openclaw.oneclick.core.exec.CommandRunner;
import com.openclaw.oneclick.core.model.DeploymentSettings;
import com.openclaw.oneclick.core.model.StepStatus;
import com.openclaw.oneclick.core.service.TokenReader;
import com.openclaw.oneclick.core.service.WslService;
import com.openclaw.oneclick.core.state.StateStore;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StepEngineTest {
    @Test
    void shouldResumeFromFailureStep() throws Exception {
        var tmp = Files.createTempFile("state", ".json");
        var state = new StateStore(tmp, DeploymentSettings.defaults());
        var ctx = new StepContext(DeploymentSettings.defaults(), new CommandRunner(), new WslService(new CommandRunner()), state, new TokenReader(), s -> {}, "k");
        var steps = List.of(step("a"), step("b"), step("c"));
        var states = new StepEngine(steps).run(ctx, "b");
        assertEquals(StepStatus.SKIPPED, states.get(0).status());
        assertEquals(StepStatus.SUCCESS, states.get(1).status());
    }

    private Step step(String id) {
        return new Step() {
            @Override public String id() { return id; }
            @Override public String title() { return id; }
            @Override public void run(StepContext context) { }
        };
    }
}
