package com.kj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TaskAllocator implements JobResultListener {
    private final JobResultListener listener;
    private final List<KJFile> rawFileQ = new ArrayList<>();
    private final SynchronousQueue<KJFile> processdFileQ = new SynchronousQueue<>();
    private List<Thread> tasks = new ArrayList<>(4);

    TaskAllocator(JobResultListener listener) {
        this.listener = listener;
    }

    public void giveTask(File[] files) {
        addAllFiles(
                List.of(files)
                        .stream()
                        .flatMap(Stream::ofNullable)
                        .map(KJFile::new)
                        .collect(Collectors.toList()));
        doWork();
    }

    private void doWork() {
        if (rawFileQ.size() < 4) {
            rawFileQ.forEach(f -> tasks.add(new JobThread(List.of(f))));
        } else {

            int tasksForEach = rawFileQ.size() / 4;
            int leftTask = rawFileQ.size() % 4;

            for (int i = 0; i < 4; i++) {
                List<KJFile> file = rawFileQ.subList((i * tasksForEach), ((i * tasksForEach) - 1));
                if (tasksForEach * 4 + i - 1 < rawFileQ.size()) {
                    file.add(rawFileQ.get(tasksForEach * 4 + i));
                }
                tasks.add(new JobThread(file));
            }
        }
        tasks.forEach(Thread::run);
    }

    private void addAllFiles(List<KJFile> files) {
        files.stream().flatMap(Stream::ofNullable).forEach(this::addFile);
    }

    private void addFiles(List<KJFile> files) {
        rawFileQ.addAll(files);
    }

    private void addFile(KJFile file) {
        Optional.ofNullable(file.getChildDirectories()).ifPresent(this::addAllFiles);
        Optional.ofNullable(file.getChildFiles()).ifPresent(this::addFiles);
        rawFileQ.add(file);
    }

    @Override
    public void onJobDone(Thread thread, List<KJFile> files) {
        files.forEach(processdFileQ::offer);
        tasks.remove(thread);

        if (tasks.isEmpty()) {
            listener.onJobDone(Thread.currentThread(), List.of((KJFile[]) processdFileQ.toArray()));
        }
    }
}
