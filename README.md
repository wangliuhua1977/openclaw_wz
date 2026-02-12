# OpenClaw OneClick（Windows 11 一键部署器）

一个基于 **Java 22 + Maven + Swing + FlatLaf** 的桌面安装器，用于在 Windows 11 上一键部署并使用 OpenClaw。

## 能做什么
- 一键部署：预检 → WSL2/Ubuntu → cloud-init 初始化 → 安装 OpenClaw → 非交互式 onboarding → 启动 gateway → 输出 Dashboard URL。
- 一键启动：确保 gateway 存活并生成携带 token 的 URL。
- 状态/日志/更新/修复/卸载入口（调用官方 CLI）。
- 默认安全策略：`loopback + token + --skip-skills`。

## 一键部署流程（文字流程图）
1. Windows 环境预检（管理员权限、WSL 可用性、PowerShell）。
2. 安装/更新 WSL（必要时提示重启并写入 state.json 断点续跑）。
3. 写入 cloud-init（`%USERPROFILE%\.cloud-init\Ubuntu-24.04.user-data`），首次启动并等待完成。
4. 安装 OpenClaw（官方 installer 默认，或 npm 方案）。
5. 以 `--non-interactive` 执行 onboard，默认 `--skip-skills`。
6. 读取 `~/.openclaw/openclaw.json` 中 token，生成 Dashboard URL。
7. 启动并校验 Gateway。
8. 执行安全审计（若 CLI 可用）。

## 构建与运行
```bash
mvn -q -DskipTests package
java -jar app/target/openclaw-oneclick-app.jar
```

## Windows 安装包
```bash
mvn -Pwindows-jpackage -DskipTests package
```
输出目录：`dist/`。

## 常见问题
1. **WSL 安装失败 / 需要重启**
   - 执行：`wsl --update`、`wsl --install`。
   - 如果提示重启：重启后在应用中选择“继续上次部署”。
2. **cloud-init 未生效**
   - 必须先写 user-data，再 `wsl --install -d Ubuntu-24.04 --no-launch`。
3. **systemd 未启用**
   - 确认 `/etc/wsl.conf` 含 `[boot] systemd=true`，然后 `wsl --shutdown` 后再开。
4. **Gateway 端口占用**
   - 在高级设置改端口（默认 18789）。
5. **token 丢失**
   - 从 `~/.openclaw/openclaw.json` 读取 `gateway.auth.token`。
6. **只允许本机访问**
   - 保持 `gateway.bind=loopback`，不要启用 LAN 暴露。

## 安全提示
- 默认不启用 skills（供应链风险控制）。
- 如需启用，请仅安装可信来源并最小权限运行。

## 仓库结构
```text
repo/
  pom.xml
  app/
  core/
  packaging/
  dist/
```
