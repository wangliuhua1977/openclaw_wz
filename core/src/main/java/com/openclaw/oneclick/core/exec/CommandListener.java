package com.openclaw.oneclick.core.exec;

@FunctionalInterface
public interface CommandListener {
    void onLine(String line, boolean error);
}
