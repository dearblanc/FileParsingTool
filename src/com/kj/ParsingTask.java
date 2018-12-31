package com.kj;

class ParsingTask extends Thread {
    private final TaskNotifier notifier;

    ParsingTask(TaskNotifier notifier) {
        this.notifier = notifier;
    }

    public void run() {
        KJFile file = SharedFileList.getInstance().take();
        Parser parser = new Parser();
        while (file != null) {
            parser.parse(file);
            notifier.onParsed(file);
            file = SharedFileList.getInstance().take();
        }
    }
}
