package com.kj;

import java.util.List;

class JobThread extends Thread {
    private final List<KJFile> files;

    JobThread(List<KJFile> files) {
        this.files = files;
    }

    @Override
    public void run() {
        super.run();
        files.stream().map(Parser::new).forEach(Parser::parse);
    }
}
