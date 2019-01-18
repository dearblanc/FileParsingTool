package com.kj.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame {
    private final JFrame frame;
    private final JPanel mainPane = new JPanel();
    private final ScrollPane scrollPane = new ScrollPane();
    private final BottomButton bottomButton = new BottomButton();

    public MainFrame(JFrame frame) {
        this.frame = frame;
    }

    public void initGUI() {
        SwingUtilities.invokeLater(this::initInUIThread);
    }

    private void initInUIThread() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //exec.shutdown();
                super.windowClosing(e);
            }
        });

        GraphicsDevice device =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = device.getDisplayMode().getWidth();
        int height = device.getDisplayMode().getHeight();
        frame.setBounds(width / 6, height / 6, width * 2 / 3, height * 2 / 3);
        frame.setIconImage(Toolkit.getDefaultToolkit().createImage("....png"));

        initInsideFrame();
        frame.setVisible(true);
    }

    private void initInsideFrame() {
        mainPane.setLayout(new BorderLayout());
        frame.setContentPane(mainPane);

        TopLabel label = new TopLabel();
        label.AddTo(mainPane, BorderLayout.NORTH);

        scrollPane.initGUI();
        scrollPane.addTo(mainPane, BorderLayout.CENTER);

        bottomButton.initGUI();
        bottomButton.addTo(mainPane, BorderLayout.SOUTH);
    }
}
