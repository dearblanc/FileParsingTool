package com.kj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class TaskAllocator implements JobResultListener {
    private static final int maximumThreadCnt = 4;
    private final JobResultListener listener;
    private final List<Thread> tasks = new ArrayList<>(maximumThreadCnt);
    private final List<File> rawFiles = new ArrayList<>();
    private final List<KJFile> files = new ArrayList<>();

    TaskAllocator(JobResultListener listener) {
        this.listener = listener;
    }

    public void giveTask(List<File> files) {
        if (files == null) {
            return;
        }
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
        if (rawFiles.size() < maximumThreadCnt) {
            rawFiles
                    .stream()
                    .map(KJFile::new)
                    .filter(KJFile::isIntendedFile)
                    .forEach(f -> tasks.add(new JobThread(TaskAllocator.this, List.of(f))));
        } else {
            int tasksForEach = rawFiles.size() / maximumThreadCnt;
            int leftTask = rawFiles.size() % maximumThreadCnt;

            for (int i = 0; i < 4; i++) {
                List<File> files = rawFiles.subList((i * tasksForEach), ((i + 1) * tasksForEach));
                if (tasksForEach * 4 + i < rawFiles.size()) {
                    files.add(rawFiles.get(tasksForEach * 4 + i));
                }
                Thread thread =
                        new JobThread(this, files.stream().map(KJFile::new).collect(Collectors.toList()));
                tasks.add(thread);
                thread.run();
            }
        }
    }

    private void addAllFiles(List<File> files) {
        files.forEach(this::addFileWithChildren);
    }

    private void addFileWithChildren(File file) {
        if (file == null) {
            return;
        }
        addAllFiles(getChildDirectories(file));
        addAllFiles(getChildFiles(file));
        rawFiles.add(file);
    }

    private void addFiles(List<File> files) {
        rawFiles.addAll(files);
    }

    private List<File> getChildDirectories(File parent) {
        List<File> files = getChildren(parent);
        files.removeIf(file -> !file.isDirectory());
        return files;
    }

    private List<File> getChildFiles(File parent) {
        List<File> files = getChildren(parent);
        files.removeIf(file -> (!file.isFile() || !file.canRead()));
        return files;
    }

    private List<File> getChildren(File parent) {
        List<File> list = new ArrayList<>();
        if (parent == null) {
            return list;
        }
        File[] files = parent.listFiles();
        if (files == null) {
            return list;
        }
        list.addAll(List.of(files));
        return list;
    }

    @Override
    public void onJobDone(Thread thread, List<KJFile> files) {
        this.files.addAll(files);
        synchronized (tasks) {
            tasks.remove(thread);

            if (tasks.isEmpty()) {
                listener.onJobDone(Thread.currentThread(), this.files);
            }
        }
    }
}
