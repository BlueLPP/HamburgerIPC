package com.hamburger.ipc;

import android.content.ContentProvider;
import android.os.Bundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IpcContentProvider extends ContentProvider {

    private final Map<String, Register> registerMap = new HashMap<>();
    private final Register allRegister = new Register("");

    @Override
    public boolean onCreate() {
        List<IpcObjectMap> list = register();
        for (IpcObjectMap map : list) {
            Register register = getRegister(map.authority);
            register.objectMap.put(map.className, map.impl);
            Logger.getInternalLog().log("register class: " + map.className + " for " + map.authority);
        }
        return true;
    }

    private Register getRegister(String authority) {
        if (authority.isEmpty()) {
            return allRegister;
        }
        Register register = registerMap.get(authority);
        if (register == null) {
            register = new Register(authority);
            registerMap.put(authority, register);
            Logger.getInternalLog().log("register authority: " + authority);
        }
        return register;
    }

    @Override
    public Bundle call(String authority, String method, String arg, Bundle extras) {
        String callingPackage = getCallingPackage();
        Logger.getIpcLog().log("[receive] authority: " + authority + ", callingPackage: " + callingPackage + ", method: " + method);
        Object impl = getImpl(authority, method);
        if (impl == null) {
            Logger.getIpcLog().log("[return] No impl error. " + authority + ", " + method);
            throw new IllegalArgumentException("No impl error. " + authority + ", " + method);
        }
        try {
            HamburgerBinder.setCallPackage(callingPackage);
            Object obj = HamburgerUtils.invokeMethod(impl, extras);
            Bundle bundle = HamburgerUtils.methodResultToBundle(obj);
            Logger.getIpcLog().log("[return] authority: " + authority + ", callingPackage: " + callingPackage + ", method: " + method + ", result: " + bundle);
            return bundle;
        } catch (RuntimeException e) {
            Logger.getIpcLog().log("[return] error: " + e.getMessage());
            throw e;
        }
    }

    private Object getImpl(String authority, String className) {
        Object obj = null;
        Register register = registerMap.get(authority);
        if (register != null) {
            obj = register.objectMap.get(className);
        }
        if (obj == null) {
            return allRegister.objectMap.get(className);
        }
        return obj;
    }

    public abstract List<IpcObjectMap> register();

    private static class Register {
        final String authority;
        final Map<String, Object> objectMap = new HashMap<>();

        Register(String authority) {
            this.authority = authority;
        }
    }

    public static class IpcObjectMap {
        public final String authority;

        public final Class<?> interfaceClass;
        public final String className;
        public final Object impl;

        public IpcObjectMap(Class<?> interfaceClass, Object impl) {
            this("", interfaceClass, impl);
        }

        public IpcObjectMap(String authority, Class<?> interfaceClass, Object impl) {
            if (authority == null) {
                throw new NullPointerException("authority must be not null.");
            }
            if (interfaceClass == null) {
                throw new NullPointerException("interfaceClass must be not null.");
            }
            if (impl == null) {
                throw new NullPointerException("impl object must be not null.");
            }
            this.authority = authority;
            this.interfaceClass = interfaceClass;
            this.impl = impl;
            this.className = HamburgerUtils.getInterfaceName(interfaceClass);
        }
    }
}