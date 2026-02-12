package com.openclaw.oneclick.app;

import com.formdev.flatlaf.FlatLightLaf;
import com.openclaw.oneclick.app.ui.MainWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
