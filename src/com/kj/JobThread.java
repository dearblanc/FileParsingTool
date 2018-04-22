package com.kj;

import java.util.List;

class JobThread extends Thread {
    private final JobResultListener listener;
    private final List<KJFile> files;

    JobThread(JobResultListener listener, List<KJFile> files) {
        this.listener = listener;
        this.files = files;
    }

    @Override
    public void run() {
        super.run();

        Parser parser = new Parser(files);
        parser.parse();

        listener.onJobDone(this, files);
    }
}
