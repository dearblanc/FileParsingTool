package com.kj;

import java.util.List;

class ParsingTask extends Thread {
    private final TaskNotifier notifier;
    private final List<KJFile> files;

    ParsingTask(TaskNotifier notifier, List<KJFile> files) {
        this.notifier = notifier;
        this.files = files;
    }

    public void run() {
        Parser parser = new Parser();
        for (KJFile file : files) {
            parser.parse(file);
            notifier.onParsed(file);
        }
    }
}
