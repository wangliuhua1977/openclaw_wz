package com.openclaw.oneclick.core.steps;

import com.openclaw.oneclick.core.engine.StepContext;
import com.openclaw.oneclick.core.exec.CommandResult;

import java.time.Duration;
import java.util.List;

public class WslInstallStep extends BaseStep {
    @Override public String id() { return "install-wsl"; }
    @Override public String title() { return "Step 2: 安装/更新 WSL"; }
    @Override public boolean needsAdmin() { return true; }

    @Override
    public void run(StepContext context) throws Exception {
        CommandResult update = context.wslService().runWindows(List.of("wsl.exe", "--update"), Duration.ofMinutes(5),
                (line, err) -> context.output().append(line));
        if (!update.success()) {
            context.output().append("尝试首次安装 WSL...");
            CommandResult install = context.wslService().runWindows(
                    List.of("wsl.exe", "--install", "-d", context.settings().distro(), "--no-launch"),
                    Duration.ofMinutes(15), (line, err) -> context.output().append(line));
            if (!install.success() && (install.stdout().contains("restart") || install.stderr().contains("restart"))) {
                throw new IllegalStateException("需要重启系统后继续部署。已保存 state.json，请重启后继续。");
            }
            ensureOk(install, "WSL 安装失败");
        }
    }
}
