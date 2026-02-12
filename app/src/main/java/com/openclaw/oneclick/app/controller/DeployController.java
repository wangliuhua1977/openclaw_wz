package com.openclaw.oneclick.app.controller;

import com.openclaw.oneclick.core.model.DeploymentSettings;
import com.openclaw.oneclick.core.service.OpenClawOrchestrator;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class DeployController {
    private final OpenClawOrchestrator orchestrator = new OpenClawOrchestrator();

    public void deploy(DeploymentSettings settings, String apiKey, JTextArea logArea) {
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                orchestrator.deploy(settings, apiKey, s -> publish(s + "\n"), null);
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                chunks.forEach(logArea::append);
            }
        }.execute();
    }

    public void start(DeploymentSettings settings, JTextArea logArea) {
        new SwingWorker<String, String>() {
            @Override
            protected String doInBackground() throws Exception {
                return orchestrator.oneClickStart(settings, s -> publish(s + "\n"));
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                chunks.forEach(logArea::append);
            }

            @Override
            protected void done() {
                try {
                    String url = get();
                    logArea.append("Dashboard URL: " + url + "\n");
                    Desktop.getDesktop().browse(URI.create(url));
                } catch (Exception ex) {
                    logArea.append("启动失败: " + ex.getMessage() + "\n");
                }
            }
        }.execute();
    }
}
