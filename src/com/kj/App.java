package com.kj;

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
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static java.awt.Font.BOLD;
import static java.util.stream.Collectors.toList;

public class App {
    private final JFrame frame;
    private JPanel mainPane;
    private JList<String> list;
    private DefaultListModel<String> model;
    private List<KJFile> fileList;

    private App(JFrame frame) {
        this.frame = frame;
        SwingUtilities.invokeLater(this::initGUI);
    }

    public static void main(String[] args) {
        new App(new JFrame("KJ p-cap Parser"));
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
        frame.setBounds(width / 6, height / 6, width * 2 / 3, height * 2 / 3);
        frame.setIconImage(Toolkit.getDefaultToolkit().createImage("....png"));
    }

    private void initMainPane() {
        mainPane.setLayout(new BorderLayout());
        frame.setContentPane(mainPane);
    }

    private void initTopLabel() {
        JLabel labelTop = new JLabel();
        labelTop.setText("Drop down files");
        labelTop.setFont(new Font("verdana", BOLD, 13));
        labelTop.setHorizontalAlignment(SwingConstants.CENTER);
        mainPane.add(labelTop, BorderLayout.NORTH);
        mainPane.setBackground(new Color(255, 181, 41));
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
                                startTask(list);
                            }
                        }
                    });
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }

        model = new DefaultListModel<>();
        list.setModel(model);
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

        JScrollPane scrollPene = new JScrollPane(list);
        scrollPene.setBackground(Color.WHITE);
        mainPane.add(scrollPene, BorderLayout.CENTER);
        scrollPene.setDropTarget(target);
    }

    private void startTask(List<File> list) {
        openWaitingDialog(list);
    }

    private void openWaitingDialog(List<File> list) {
        model.clear();
        JDialog dlgProgress =
                new JDialog(
                        frame,
                        "Please wait...",
                        true); // true means that the dialog created is modal
        JLabel lblStatus =
                new JLabel(
                        "Working..."); // this is just a label in which you can indicate the state
        // of the processing

        JProgressBar pbProgress = new JProgressBar(0, 100);
        // pbProgress.setIndeterminate(true); // we'll use an indeterminate progress bar

        dlgProgress.add(BorderLayout.NORTH, lblStatus);
        dlgProgress.add(BorderLayout.CENTER, pbProgress);
        dlgProgress.setDefaultCloseOperation(
                JDialog.DO_NOTHING_ON_CLOSE); // prevent the user from closing the dialog
        dlgProgress.setBounds(
                frame.getX() + (frame.getWidth() / 3),
                frame.getY() + (frame.getHeight() / 3),
                300,
                90);

        DragAndDropEventProcessor processor =
                new DragAndDropEventProcessor(dlgProgress, pbProgress, list);
        pbProgress.setValue(0);
        pbProgress.setEnabled(true);
        processor.execute();
        dlgProgress.setVisible(true);
    }

    private class DragAndDropEventProcessor extends SwingWorker implements TaskNotifier {
        final JDialog dlgProgress;
        final JProgressBar progressBar;
        final List<File> list;
        final List<KJFile> parsedList = Collections.synchronizedList(new ArrayList<>());
        AtomicLong accum = new AtomicLong(0L);
        long totalSize = 0L;

        DragAndDropEventProcessor(JDialog dlgProgress, JProgressBar progressBar, List<File> list) {
            this.dlgProgress = dlgProgress;
            this.list = list;
            this.progressBar = progressBar;
        }

        @Override
        protected Object doInBackground() {
            final FileTraversal traversal = new FileTraversal();
            traversal.loadFiles(list);
            totalSize = traversal.getTotalFileSize();
            System.out.println("totalsize : " + totalSize);

            final int nThreads = Runtime.getRuntime().availableProcessors() + 1;
            final ParsingTask[] tasks = new ParsingTask[nThreads];
            final List<KJFile> sourceFiles = traversal.getAllFiles();
            final int totalCount = sourceFiles.size();

            if (totalCount < nThreads) {
                for (int i = 0; i < totalCount; i++) {
                    tasks[i] = new ParsingTask(this, List.of(sourceFiles.get(i)));
                }
                for (int i = totalCount; i < nThreads; i++) {
                    tasks[i] = new ParsingTask(this, Collections.emptyList());
                }
            } else {
                int quote = (totalCount / nThreads) + 1;
                for (int i = 0; i < nThreads - 1; i++) {
                    tasks[i] = new ParsingTask(this, sourceFiles.subList(i * quote, (i + 1) * quote - 1));
                }
                tasks[nThreads - 1] = new ParsingTask(this, sourceFiles.subList((nThreads - 1) * quote, sourceFiles.size() - 1));
            }

            for (ParsingTask task : tasks) {
                task.start();
            }
            for (ParsingTask task : tasks) {
                try {
                    task.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            save(parsedList);

            return null;
        }

        @Override
        protected void done() {
            dlgProgress.dispose(); // close the modal dialog
        }

        @Override
        public void onParsed(KJFile file) {
            parsedList.add(file);
            accum.addAndGet(file.getFileSize());
            double quote = (double) (accum.get()) / totalSize;
            int rate = (int) (quote * 100);
            System.out.println("progress(" + rate + ")");
            SwingUtilities.invokeLater(() -> progressBar.setValue(rate));
        }
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
        startTask(List.of(chooser.getSelectedFiles()));
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
        KJFile file = fileList.get(index);
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
        childframe.setLocationRelativeTo(frame);
        childframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JScrollPane sp = new JScrollPane();
        JTextPane txtpnDdd = new JTextPane();
        txtpnDdd.setText(builder.toString());
        sp.setViewportView(txtpnDdd);
        childframe.getContentPane().add(sp);
        childframe.setVisible(true);
    }

    private void save(List<KJFile> files) {
        fileList =
                files.stream()
                        .sorted(Comparator.comparing(KJFile::getFileNameAbsolutePath))
                        .collect(toList());
        SwingUtilities.invokeLater(
                () -> {
                    for (KJFile f : fileList) {
                        model.addElement(f.getFileNameAbsolutePath());
                    }
                });
        // WireShark.saveKeys(fileList);
    }
}
