package com.hamburger.ipc;

import android.content.ContentProvider;
import android.os.Bundle;

import java.lang.reflect.Method;
import java.util.Collections;
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
            Service service = new Service(map.interfaceClass, map.impl);
            register.objectMap.put(service.className, service);
            Logger.getInternalLog().log("register class: " + service.className + " for " + map.authority);
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
        Logger.getIpcLog().log("[receive] authority: " + authority + ", callingPackage: " + callingPackage + ", method: " + method + ", arg: " + arg);
        Service service = getService(authority, method);
        if (service == null) {
            Logger.getIpcLog().log("[return] No impl class error. " + authority + ", " + method);
            throw new IllegalArgumentException("No impl class error. " + authority + ", " + method);
        }
        Method serviceMethod = service.methodMap.get(arg);
        if (serviceMethod == null) {
            Logger.getIpcLog().log("[return] No impl method error. " + authority + ", " + method);
            throw new IllegalArgumentException("No impl method error. " + authority + ", " + method);
        }
        try {
            HamburgerBinder.setCallPackage(callingPackage);
            Object obj = HamburgerUtils.invokeMethod(service.impl, serviceMethod, extras);
            Bundle bundle = HamburgerUtils.methodResultToBundle(obj);
            Logger.getIpcLog().log("[return] authority: " + authority + ", callingPackage: " + callingPackage + ", method: " + method + ", result: " + bundle);
            return bundle;
        } catch (RuntimeException e) {
            Logger.getIpcLog().log("[return] error: " + e.getMessage());
            throw e;
        }
    }

    private Service getService(String authority, String className) {
        Service service = null;
        Register register = registerMap.get(authority);
        if (register != null) {
            service = register.objectMap.get(className);
        }
        if (service == null) {
            return allRegister.objectMap.get(className);
        }
        return service;
    }

    public abstract List<IpcObjectMap> register();

    private static class Register {
        final String authority;
        final Map<String, Service> objectMap = new HashMap<>();

        Register(String authority) {
            this.authority = authority;
        }
    }

    private static class Service {
        final Class<?> interfaceClass;
        final Object impl;
        final String className;
        final Map<String, Method> methodMap;

        public Service(Class<?> interfaceClass, Object impl) {
            this.interfaceClass = interfaceClass;
            this.impl = impl;
            this.className = HamburgerUtils.getInterfaceName(interfaceClass);
            Logger.getInternalLog().log("Service: " + className + ", " + interfaceClass + ", " + impl);

            Map<String, Method> methods = new HashMap<>();
            for (Method method : interfaceClass.getMethods()) {
                String methodName = HamburgerUtils.getMethodName(method);
                Logger.getInternalLog().log(className + " add " + methodName);
                Method put = methods.put(methodName, method);
                if (put != null) {
                    Logger.getInternalLog().log("Duplicate method: " + methodName);
                    throw new IPCException("Duplicate method: " + methodName);
                }
            }
            this.methodMap = Collections.unmodifiableMap(methods);
        }
    }

    public static class IpcObjectMap {
        public final String authority;

        public final Class<?> interfaceClass;
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
        }
    }
}