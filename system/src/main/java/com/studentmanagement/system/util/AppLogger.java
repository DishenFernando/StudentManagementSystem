package com.studentmanagement.system.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {

    private static final Logger logger = LoggerFactory.getLogger("StudentManagementLogger");

    public static void info(String message) {
        logger.info("[INFO] " + message);
    }

    public static void warn(String message) {
        logger.warn("[WARN] " + message);
    }

    public static void error(String message) {
        logger.error("[ERROR] " + message);
    }
}
