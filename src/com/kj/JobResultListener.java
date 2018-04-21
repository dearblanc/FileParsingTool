package com.kj;

import java.util.List;

interface JobResultListener {
    void onJobDone(Thread thread, List<KJFile> files);
}