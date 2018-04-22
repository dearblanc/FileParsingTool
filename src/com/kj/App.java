package com.kj;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class App implements JobResultListener {
    private JFrame frame;
    private JPanel mainPane;
    private JList<String> list;
    private DefaultListModel<String> model;
    private TaskAllocator worker;
    private List<KJFile> fileList;

    private App(JFrame frame) {
        this.frame = frame;
        init();

        worker = new TaskAllocator(this);
    }

    public static void main(String args[]) {
        new App(new JFrame("KJ p-cap Parser"));
    }

    private void init() {
        initGUI();
    }

    private void initGUI() {
        initMainFrame();
        initMainPane();
        initTopLabel();
        initScrollPane();
        initBottomButton();

        frame.setVisible(true);
    }

    private void initMainFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphicsDevice device =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = device.getDisplayMode().getWidth();
        int height = device.getDisplayMode().getHeight();
        frame.setBounds(width / 6, height / 6, width / 2, height / 2);
    }

    private void initMainPane() {
        mainPane.setLayout(new BorderLayout());
        frame.setContentPane(mainPane);
    }

    private void initTopLabel() {
        JLabel labelTop = new JLabel();
        labelTop.setText("Drop down files");
        labelTop.setHorizontalAlignment(SwingConstants.CENTER);
        mainPane.add(labelTop, BorderLayout.NORTH);
    }

    private void initScrollPane() {
        list = new JList<>();
        list.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DropTarget target = new DropTarget();
        try {
            target.addDropTargetListener(
                    new DropTargetListener() {
                        @Override
                        public void dragEnter(DropTargetDragEvent dtde) {
                        }

                        @Override
                        public void dragOver(DropTargetDragEvent dtde) {
                        }

                        @Override
                        public void dropActionChanged(DropTargetDragEvent dtde) {
                        }

                        @Override
                        public void dragExit(DropTargetEvent dte) {
                        }

                        @Override
                        public void drop(DropTargetDropEvent dtde) {
                            model.clear();
                            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                            Transferable transferable = dtde.getTransferable();
                            DataFlavor[] flavors = transferable.getTransferDataFlavors();

                            List<DataFlavor> fileFlavors =
                                    Stream.of(flavors)
                                            .flatMap(Stream::ofNullable)
                                            .filter(f -> f.equals(DataFlavor.javaFileListFlavor))
                                            .collect(toList());
                            fileFlavors.forEach(
                                    flavor -> {
                                        try {
                                            List<File> list = (List) transferable.getTransferData(flavor);
                                            worker.giveTask(list);
                                        } catch (IOException | UnsupportedFlavorException e) {
                                            e.printStackTrace();
                                        }
                                    });
                        }
                    });
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }

        model = new DefaultListModel<>();
        list.setModel(model);
        setListLineBorder();

        list.addListSelectionListener((ListSelectionEvent e) -> System.out.println(e.getFirstIndex()));

        JScrollPane scrollPene = new JScrollPane(list);
        scrollPene.setBackground(Color.WHITE);
        mainPane.add(scrollPene, BorderLayout.CENTER);
        scrollPene.setDropTarget(target);
    }

    private void initBottomButton() {
        JButton buttonBottom = new JButton("Load file");
        mainPane.add(buttonBottom, BorderLayout.SOUTH);
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
        chooser.showOpenDialog(frame);
        worker.giveTask(List.of(chooser.getSelectedFiles()));
    }

    private void setListLineBorder() {
        list.setCellRenderer(
                new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        JLabel listCellRendererComponent =
                                (JLabel)
                                        super.getListCellRendererComponent(
                                                list, value, index, isSelected, cellHasFocus);
                        listCellRendererComponent.setBorder(
                                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

                        if (index < fileList.size()) {

                            if (fileList.get(index).getKeys().isEmpty()) {
                                setBackground(Color.GREEN);
                            } else {
                                setBackground(Color.LIGHT_GRAY);
                            }
                        }

                        return listCellRendererComponent;
                    }
                });
    }

    @Override
    public void onJobDone(Thread thread, List<KJFile> files) {
        model.clear();
        fileList =
                files
                        .stream()
                        .sorted(Comparator.comparing(KJFile::getFileNameAbsolutePath))
                        .collect(toList());
        fileList.forEach(file -> model.addElement(file.getFileNameAbsolutePath()));
    }
}
