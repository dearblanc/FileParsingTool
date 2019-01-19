package com.kj;

import com.kj.enc.Parser;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ListFileProcessor {
    private static final ListFileProcessor instance = new ListFileProcessor();
    private static final int NTHREAD = Runtime.getRuntime().availableProcessors() + 1;
    private static final ExecutorService exec = Executors.newFixedThreadPool(NTHREAD);
    private List<KJFile> processedFileLIst = new ArrayList<>();
    private DefaultListModel<String> model = new DefaultListModel<>();

    public static ListFileProcessor instance() {
        return instance;
    }

    public void process(Container container, List<File> list) {
        if (!(container instanceof JFrame)) {
            throw new IllegalArgumentException();
        }
        JFrame frame = (JFrame)container;

        JDialog dialog =
                new JDialog(
                        frame,
                        "Please wait...",
                        true);
        JLabel lblStatus =
                new JLabel(
                        "Working...");
        dialog.add(BorderLayout.NORTH, lblStatus);

        JProgressBar pbProgress = new JProgressBar(0, 100);
        dialog.add(BorderLayout.CENTER, pbProgress);

        dialog.setDefaultCloseOperation(
                JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setBounds(
                frame.getX() + (frame.getWidth() / 3),
                frame.getY() + (frame.getHeight() / 3),
                300,
                90);

        pbProgress.setValue(0);
        pbProgress.setEnabled(true);
        SwingProcessor processor =
                new SwingProcessor(dialog, pbProgress, list);
        processor.execute();
        dialog.setVisible(true);
    }

    public void setListDataModel(DefaultListModel<String> model) {
        this.model = model;
    }

    private class SwingProcessor extends SwingWorker {
        private final JDialog dlgProgress;
        private final JProgressBar progressBar;
        private final List<File> givenFileList;

        private long totalSizeOfFiles = 0L;
        private AtomicLong totalAmountOfProcess = new AtomicLong(0L);
        private CountDownLatch doneSignal = new CountDownLatch(NTHREAD);
        private static final int TIMEOUT = 10;

        SwingProcessor(JDialog dlgProgress, JProgressBar progressBar, List<File> list) {
            this.dlgProgress = dlgProgress;
            this.progressBar = progressBar;
            this.givenFileList = list;
        }

        @Override
        protected Object doInBackground() {
            FileTraversal traversal = new FileTraversal();
            traversal.loadFiles(givenFileList);
            totalSizeOfFiles = traversal.totalFileSize();
            System.out.println("totalsize : " + totalSizeOfFiles);

            List<KJFile> totalFiles = traversal.retrieveAllFiles();
            save(totalFiles);
            int totalCount = totalFiles.size();
            Runnable[] tasks = new Runnable[NTHREAD];

            if (totalCount < NTHREAD) {
                for (int i = 0; i < totalCount; i++) {
                    tasks[i] = createTask(List.of(totalFiles.get(i)));
                }
                for (int i = totalCount; i < NTHREAD; i++) {
                    tasks[i] = createTask(Collections.emptyList());
                }
            } else {
                int quotient = (totalCount / NTHREAD) + 1;
                for (int i = 0; i < NTHREAD - 1; i++) {
                    tasks[i] = createTask(totalFiles.subList(i * quotient, (i + 1) * quotient - 1));
                }
                tasks[NTHREAD - 1] = createTask(totalFiles.subList((NTHREAD - 1) * quotient, totalFiles.size() - 1));
            }

            for (Runnable r : tasks) {
                exec.execute(r);
            }

            try {
                doneSignal.await(TIMEOUT, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }

            save(totalFiles);

            return null;
        }

        @Override
        protected void done() {
            dlgProgress.dispose(); // close the modal dialog
        }

        private void onParsed(KJFile file) {
            totalAmountOfProcess.addAndGet(file.getFileSize());
            double quote = (double) (totalAmountOfProcess.get()) / totalSizeOfFiles;
            int rate = (int) (quote * 100);
            System.out.println("progress(" + rate + ")");
            SwingUtilities.invokeLater(() -> progressBar.setValue(rate));
        }

        private Runnable createTask(List<KJFile> list) {
            return () -> {
                Parser parser = new Parser();
                for (KJFile file : list) {
                    parser.parse(file);
                    onParsed(file);
                }
                doneSignal.countDown();
            };
        }
    }

    private void save(List<KJFile> files) {
        processedFileLIst =
                files.stream()
                        .sorted(Comparator.comparing(KJFile::getFileNameAbsolutePath))
                        .collect(java.util.stream.Collectors.toList());
        model.clear();
        SwingUtilities.invokeLater(
                () -> {
                    for (KJFile f : processedFileLIst) {
                        model.addElement(f.getFileNameAbsolutePath());
                    }
                });
        // WireShark.saveKeys(processedFileLIst);
    }

    public List<KJFile> getProcessedFileList() {
        return new ArrayList<>(processedFileLIst);
    }
}
