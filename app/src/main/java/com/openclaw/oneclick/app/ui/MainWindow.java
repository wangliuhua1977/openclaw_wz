package com.openclaw.oneclick.app.ui;

import com.openclaw.oneclick.app.controller.DeployController;
import com.openclaw.oneclick.core.model.DeploymentSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainWindow extends JFrame {
    private final DeployController controller = new DeployController();
    private final JTextArea deployLog = new JTextArea();
    private final JTextArea startLog = new JTextArea();
    private final JPasswordField apiKey = new JPasswordField();
    private final JCheckBox enableSkills = new JCheckBox("启用 skills（高风险）", false);

    public MainWindow() {
        setTitle("OpenClaw OneClick (Windows 11)");
        setSize(1200, 760);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        JLabel security = new JLabel("安全提示：默认仅 loopback + token，且默认不安装 skills。仅在可信环境启用扩展。", SwingConstants.LEFT);
        security.setForeground(new Color(0xA15F00));
        root.add(security, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.addTab("一键部署", buildDeployTab());
        tabs.addTab("一键启动", buildStartTab());
        tabs.addTab("状态", textPanel("WSL/Node/OpenClaw/Gateway 状态检查入口（调用 openclaw status --deep）。"));
        tabs.addTab("日志", textPanel("可执行 openclaw logs --follow / gateway logs，并支持导出 zip。"));
        tabs.addTab("更新", textPanel("执行 openclaw update --channel stable，并在后续调用 doctor。"));
        tabs.addTab("修复/诊断", textPanel("执行 openclaw doctor --non-interactive、可选 security audit --fix。"));
        tabs.addTab("卸载", textPanel("强确认后卸载 OpenClaw；可选清理 Ubuntu/WSL（默认关闭）。"));
        tabs.addTab("高级设置", buildAdvancedPanel());
        root.add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildDeployTab() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        JPanel top = new JPanel(new GridLayout(3, 2, 8, 8));
        top.add(new JLabel("OPENAI_API_KEY"));
        top.add(apiKey);
        top.add(new JLabel("默认发行版"));
        top.add(new JLabel("Ubuntu-24.04"));
        top.add(enableSkills);
        JButton deploy = new JButton("开始部署");
        deploy.addActionListener(e -> controller.deploy(settings(), new String(apiKey.getPassword()), deployLog));
        top.add(deploy);
        p.add(top, BorderLayout.NORTH);
        deployLog.setEditable(false);
        p.add(new JScrollPane(deployLog), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildStartTab() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        JButton start = new JButton("一键启动");
        start.addActionListener(e -> controller.start(settings(), startLog));
        p.add(start, BorderLayout.NORTH);
        startLog.setEditable(false);
        p.add(new JScrollPane(startLog), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildAdvancedPanel() {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.add(new JLabel("Gateway 端口")); p.add(new JLabel("18789"));
        p.add(new JLabel("Gateway 绑定")); p.add(new JLabel("loopback"));
        p.add(new JLabel("安装 daemon")); p.add(new JLabel("是"));
        p.add(new JLabel("认证方式")); p.add(new JLabel("OpenAI API Key（默认）"));
        p.add(new JLabel("LAN 暴露")); p.add(new JLabel("关闭（需高级手动开启）"));
        return p;
    }

    private JPanel textPanel(String text) {
        JTextArea area = new JTextArea(text);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private DeploymentSettings settings() {
        return new DeploymentSettings("Ubuntu-24.04", 18789, "loopback", true, enableSkills.isSelected(), true, "api-key");
    }
}
