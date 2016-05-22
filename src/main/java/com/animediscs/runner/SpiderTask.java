package com.animediscs.runner;

import java.io.IOException;

/**
 * Created by fuhaiwei on 16/5/21.
 */
public interface SpiderTask {
    int MAX_RETRY_COUNT = 3;

    void doConnect() throws IOException;

    void doExecute();

    boolean isContinue(Throwable t);

    String getText();

    int getTryCount();
}
