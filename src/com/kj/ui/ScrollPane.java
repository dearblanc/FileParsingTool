package com.kj.ui;

import com.kj.DnDListFileProcessor;
import com.kj.KJFile;
import com.kj.enc.Key;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class ScrollPane {
    private final JScrollPane scrollPane = new JScrollPane();
    private final JList<String> list = new JList<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();

    void initGUI() {
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
                            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                            Transferable transferable = dtde.getTransferable();
                            DataFlavor[] flavors = transferable.getTransferDataFlavors();
                            List<File> list = new ArrayList<>();

                            List<DataFlavor> fileFlavors =
                                    Stream.of(flavors)
                                            .flatMap(Stream::ofNullable)
                                            .filter(f -> f.equals(DataFlavor.javaFileListFlavor))
                                            .collect(toList());
                            fileFlavors.forEach(
                                    flavor -> {
                                        try {
                                            list.addAll(
                                                    (List<File>)
                                                            transferable.getTransferData(flavor));
                                        } catch (IOException | UnsupportedFlavorException e) {
                                            e.printStackTrace();
                                        }
                                    });
                            if (!list.isEmpty()) {
                                DnDListFileProcessor.instance().process(scrollPane.getTopLevelAncestor(), list);
                            }
                        }
                    });
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }

        list.setModel(model);
        DnDListFileProcessor.instance().setListDataModel(model);
        setListLineBorder();

        list.addMouseListener(
                new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent arg0) {
                        if (arg0.getClickCount() == 2) {
                            showKeysGUI(list.locationToIndex(arg0.getPoint()));
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent arg0) {
                    }

                    @Override
                    public void mouseExited(MouseEvent arg0) {
                    }

                    @Override
                    public void mousePressed(MouseEvent arg0) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent arg0) {
                    }
                });

        scrollPane.setViewportView(list);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setDropTarget(target);
    }

    private void setListLineBorder() {
        list.setCellRenderer(
                new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus) {
                        JLabel listCellRendererComponent =
                                (JLabel)
                                        super.getListCellRendererComponent(
                                                list, value, index, isSelected, cellHasFocus);
                        listCellRendererComponent.setBorder(
                                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

                        List<KJFile> fileList = DnDListFileProcessor.instance().getProcessedFileList();
                        if (index < fileList.size()) {

                            if (!fileList.get(index).getKeys().isEmpty()) {
                                if (!isSelected) {
                                    setBackground(new Color(11, 204, 114));
                                } else {
                                    setBackground(new Color(11, 236, 138));
                                }
                            } else {
                                if (!isSelected) {
                                    setBackground(new Color(153, 204, 255));
                                } else {
                                    setBackground(new Color(93, 230, 255));
                                }
                            }
                        }

                        return listCellRendererComponent;
                    }
                });
    }

    private void showKeysGUI(int index) {
        KJFile file = DnDListFileProcessor.instance().getProcessedFileList().get(index);
        List<Key> keys = file.getKeys();
        if (keys.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No key exists in the selected file.");
            return;
        }

        String carrageReturn = System.getProperty("line.separator");
        StringBuilder builder =
                new StringBuilder(
                        "[ip_version] [src] [dest [spi] [enc algorithm] [enc key] [auth algorithm] [auth key]"
                                + carrageReturn);
        for (Key key : keys) {
            builder.append(key.printKey()).append(carrageReturn);
        }

        JFrame childframe = new JFrame();
        childframe.setBounds(100, 100, 800, 500);
        childframe.setLocationRelativeTo(scrollPane.getTopLevelAncestor());
        childframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JScrollPane sp = new JScrollPane();
        JTextPane txtpnDdd = new JTextPane();
        txtpnDdd.setText(builder.toString());
        sp.setViewportView(txtpnDdd);
        childframe.getContentPane().add(sp);
        childframe.setVisible(true);
    }

    void addTo(JPanel mainPane, Object constraints) {
        mainPane.add(scrollPane, constraints);
    }
}
