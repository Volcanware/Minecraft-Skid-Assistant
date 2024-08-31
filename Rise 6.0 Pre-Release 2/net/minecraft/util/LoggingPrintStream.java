package net.minecraft.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;

public class LoggingPrintStream extends PrintStream {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String domain;

    public LoggingPrintStream(final String domainIn, final OutputStream outStream) {
        super(outStream);
        this.domain = domainIn;
    }

    public void println(final String p_println_1_) {
        this.logString(p_println_1_);
    }

    public void println(final Object p_println_1_) {
        this.logString(String.valueOf(p_println_1_));
    }

    private void logString(final String string) {
        final StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
        final StackTraceElement stacktraceelement = astacktraceelement[Math.min(3, astacktraceelement.length)];
        LOGGER.info("[{}]@.({}:{}): {}", new Object[]{this.domain, stacktraceelement.getFileName(), Integer.valueOf(stacktraceelement.getLineNumber()), string});
    }
}
