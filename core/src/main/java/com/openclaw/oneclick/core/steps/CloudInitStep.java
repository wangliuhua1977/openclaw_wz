package com.openclaw.oneclick.core.steps;

import com.openclaw.oneclick.core.engine.StepContext;
import com.openclaw.oneclick.core.exec.CommandResult;

import java.time.Duration;
import java.util.List;

public class CloudInitStep extends BaseStep {
    @Override public String id() { return "cloud-init"; }
    @Override public String title() { return "Step 3: cloud-init 初始化 Ubuntu"; }

    @Override
    public void run(StepContext context) throws Exception {
        String distro = context.settings().distro();
        String script = "$dir=Join-Path $env:USERPROFILE '.cloud-init'; New-Item -ItemType Directory -Force -Path $dir | Out-Null; " +
                "$f=Join-Path $dir '" + distro + ".user-data'; " +
                "$content=@'\n#cloud-config\nusers:\n  - default\n  - name: openclaw\n    groups: [sudo]\n    sudo: ['ALL=(ALL) NOPASSWD:ALL']\n    shell: /bin/bash\npackages:\n  - curl\n  - git\n  - jq\n  - ca-certificates\nwrite_files:\n  - path: /etc/wsl.conf\n    content: |\n      [user]\n      default=openclaw\n      [boot]\n      systemd=true\n'@; Set-Content -Path $f -Value $content -Encoding UTF8";
        CommandResult write = context.wslService().runWindows(
                List.of("powershell.exe", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", script),
                Duration.ofMinutes(2), (line, err) -> context.output().append(line));
        ensureOk(write, "写入 cloud-init 文件失败");

        CommandResult installDistro = context.wslService().runWindows(
                List.of("wsl.exe", "--install", "-d", distro, "--no-launch"), Duration.ofMinutes(15),
                (line, err) -> context.output().append(line));
        if (!installDistro.success()) {
            context.output().append("发行版可能已安装，继续执行首次启动。");
        }
        runWsl(context, "cloud-init status --wait || true", Duration.ofMinutes(10));
        CommandResult whoami = runWsl(context, "whoami", Duration.ofSeconds(30));
        if (!whoami.stdout().contains("openclaw")) {
            throw new IllegalStateException("cloud-init 未将默认用户设置为 openclaw，请执行 wsl --shutdown 后重试。");
        }
    }
}
