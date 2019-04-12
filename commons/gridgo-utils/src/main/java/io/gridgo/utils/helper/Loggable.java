package io.gridgo.utils.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Loggable {

    default Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    default Logger getLogger(String loggerName) {
        return LoggerFactory.getLogger(loggerName);
    }
}
