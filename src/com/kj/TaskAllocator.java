package com.kj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class TaskAllocator implements JobResultListener {
    private static final int maximumThreadCnt = 4;
    private final JobResultListener listener;
    private AllocThread ownThread = null;
    private final List<Thread> tasks = new ArrayList<>(maximumThreadCnt);
    private final List<File> rawFiles = new ArrayList<>();
    private final List<KJFile> files = new ArrayList<>();

    TaskAllocator(JobResultListener listener) {
        this.listener = listener;
    }

    class AllocThread extends Thread {
        private final List<File> files;

        AllocThread(List<File> files) {
            this.files = files;
        }

        @Override
        public void run() {
            super.run();
            runTask(files);
        }
    }

    void giveTask(List<File> files) {
        if (files == null) {
            return;
        }

        if (ownThread != null && ownThread.isAlive()) {
            ownThread.interrupt();
        }

        ownThread = new AllocThread(files);
        ownThread.start();
    }

    private void runTask(List<File> files) {
        prepareToWork();
        addAllFiles(files);
        doWork();
    }

    private void prepareToWork() {
        tasks.forEach(
                t -> {
                    if (t.isAlive()) {
                        t.interrupt();
                    }
                });
        tasks.clear();
        rawFiles.clear();
        files.clear();
    }

    private void doWork() {
        List<KJFile> files = rawFiles.stream().map(KJFile::new).collect(Collectors.toList());
        files.removeIf(f -> !f.isIntendedFile());

        if (files.size() < maximumThreadCnt) {
            files.forEach(
                    f -> {
                        Thread thread = new JobThread(TaskAllocator.this, List.of(f));
                        tasks.add(thread);
                    });
        } else {
            int tasksForEach = files.size() / maximumThreadCnt;

            for (int i = 0; i < maximumThreadCnt; i++) {
                List<KJFile> allocFiles =
                        new ArrayList<>(files.subList((i * tasksForEach), ((i + 1) * tasksForEach)));
                if (tasksForEach * maximumThreadCnt + i < files.size()) {
                    allocFiles.add(files.get(tasksForEach * maximumThreadCnt + i));
                }
                Thread thread = new JobThread(TaskAllocator.this, allocFiles);
                tasks.add(thread);
            }
        }
        files.clear();
        rawFiles.clear();
        tasks.forEach(Thread::start);
    }

    private void addAllFiles(List<File> files) {
        files.forEach(this::addFileWithChildren);
    }

    private void addFileWithChildren(File file) {
        if (file == null) {
            return;
        }
        if (file.isFile()) {
            rawFiles.add(file);
        }
        rawFiles.addAll(KJFile.getChildFiles(file));
        addAllFiles(KJFile.getChildDirectories(file));
    }

    @Override
    public void onJobDone(Thread thread, List<KJFile> files) {
        this.files.addAll(files);
        boolean isDone = false;
        synchronized (TaskAllocator.this) {
            tasks.remove(thread);
            isDone = tasks.isEmpty();
        }
        if (isDone) {
            listener.onJobDone(Thread.currentThread(), this.files);
        }
    }
}
