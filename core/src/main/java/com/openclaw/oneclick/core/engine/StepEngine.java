package com.openclaw.oneclick.core.engine;

import com.openclaw.oneclick.core.model.StepState;
import com.openclaw.oneclick.core.model.StepStatus;
import com.openclaw.oneclick.core.state.StateStore;

import java.util.ArrayList;
import java.util.List;

public class StepEngine {
    private final List<Step> steps;
    private volatile boolean cancelled;

    public StepEngine(List<Step> steps) {
        this.steps = new ArrayList<>(steps);
    }

    public List<StepState> run(StepContext context, String resumeFromStepId) throws Exception {
        List<StepState> results = new ArrayList<>();
        boolean resumeReached = resumeFromStepId == null || resumeFromStepId.isBlank();
        StateStore store = context.stateStore();

        for (Step step : steps) {
            if (cancelled) {
                results.add(new StepState(step.id(), StepStatus.SKIPPED, "已取消"));
                continue;
            }
            if (!resumeReached) {
                if (step.id().equals(resumeFromStepId)) {
                    resumeReached = true;
                } else {
                    results.add(new StepState(step.id(), StepStatus.SKIPPED, "恢复模式跳过"));
                    continue;
                }
            }

            context.output().append("\n==> " + step.title());
            store.markCurrentStep(step.id());
            if (!step.precheck(context)) {
                results.add(new StepState(step.id(), StepStatus.SKIPPED, "预检查跳过"));
                continue;
            }

            try {
                step.run(context);
                results.add(new StepState(step.id(), StepStatus.SUCCESS, "完成"));
            } catch (Exception ex) {
                store.saveError(step.id(), ex.getMessage());
                try {
                    step.rollback(context);
                } catch (Exception ignored) {
                }
                results.add(new StepState(step.id(), StepStatus.FAILED, ex.getMessage()));
                throw ex;
            }
        }
        store.clear();
        return results;
    }

    public void cancel() {
        cancelled = true;
    }
}
