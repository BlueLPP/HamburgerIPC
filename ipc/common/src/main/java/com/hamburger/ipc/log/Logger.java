package com.hamburger.ipc.log;

public final class Logger {

    private static final ILogger EMPTY = new ILogger() {
        @Override
        public void log(String msg) {
        }
    };

    private static ILogger ipcLog = EMPTY;
    private static ILogger interfaceLog = EMPTY;
    private static ILogger internalLog = EMPTY;

    public static ILogger getIpcLog() {
        return ipcLog;
    }

    public static void setIpcLog(ILogger ipcLog) {
        Logger.ipcLog = ipcLog == null ? EMPTY : ipcLog;
    }

    public static ILogger getInterfaceLog() {
        return interfaceLog;
    }

    public static void setInterfaceLog(ILogger interfaceLog) {
        Logger.interfaceLog = interfaceLog == null ? EMPTY : interfaceLog;
    }

    public static ILogger getInternalLog() {
        return internalLog;
    }

    public static void setInternalLog(ILogger internalLog) {
        Logger.internalLog = internalLog == null ? EMPTY : internalLog;
    }

    public interface ILogger {
        void log(String msg);
    }
}
