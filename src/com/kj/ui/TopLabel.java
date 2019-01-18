package com.kj.ui;

import javax.swing.*;
import java.awt.*;

class TopLabel {
    private final JLabel label = new JLabel();

    TopLabel() {
        label.setText("Drop down files");
        label.setFont(new Font("verdana", Font.BOLD, 13));
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    void AddTo(JPanel mainPane, Object constraints) {
        mainPane.add(label, constraints);
        mainPane.setBackground(new Color(255, 181, 41));
    }
}
