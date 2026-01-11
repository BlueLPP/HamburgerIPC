package com.hamburger.ipc.server;

public class IPCException extends RuntimeException {
    public IPCException() {
        super();
    }

    public IPCException(String message) {
        super(message);
    }

    public IPCException(String message, Throwable cause) {
        super(message, cause);
    }

    public IPCException(Throwable cause) {
        super(cause);
    }
}
