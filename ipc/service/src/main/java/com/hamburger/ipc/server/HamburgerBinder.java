package com.hamburger.ipc.server;

import android.os.Binder;

public final class HamburgerBinder {
    private static final ThreadLocal<String> callingPackages = new ThreadLocal<>();

    static void setCallPackage(String callPackage) {
        callingPackages.set(callPackage);
    }

    public static int getCallingPid() {
        return Binder.getCallingPid();
    }

    public static int getCallingUid() {
        return Binder.getCallingUid();
    }

    public static String getCallPackage() {
        return callingPackages.get();
    }
}
