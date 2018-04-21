package com.kj;

import java.util.List;

interface JobResultListener {
    void onJobDone(List<KJFile> files);
}