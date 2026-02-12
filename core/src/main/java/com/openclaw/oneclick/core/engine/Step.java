package com.openclaw.oneclick.core.engine;

public interface Step {
    String id();

    String title();

    default boolean needsAdmin() {
        return false;
    }

    default boolean precheck(StepContext context) throws Exception {
        return true;
    }

    void run(StepContext context) throws Exception;

    default void rollback(StepContext context) throws Exception {
    }
}
