package com.kj.ui;

import com.kj.ListFileProcessor;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

class BottomButton {
    private final JButton buttonBottom = new JButton("Load file");

    void initGUI() {
        buttonBottom.addMouseListener(
                new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        openFileChooser();
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
    }

    private void openFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.showOpenDialog(buttonBottom.getTopLevelAncestor());

        ListFileProcessor.instance().process(buttonBottom.getTopLevelAncestor(), List.of(chooser.getSelectedFiles()));
    }

    void addTo(JPanel mainPane, Object constraints) {
        mainPane.add(buttonBottom, constraints);
    }
}
