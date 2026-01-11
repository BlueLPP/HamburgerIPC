package com.hamburger.ipc;

import android.app.Application;
import android.net.Uri;
import android.os.Bundle;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class HamburgerIPC {

    public static Application getApplication() {
        try {
            Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
            Method method = ActivityThread.getMethod("currentActivityThread");
            Object currentActivityThread = method.invoke(ActivityThread);
            Method method2 = currentActivityThread.getClass().getMethod("getApplication");
            return (Application) method2.invoke(currentActivityThread);
        } catch (Exception e) {
            throw new RuntimeException("Invoke error.", e);
        }
    }

    private final String uri;

    private HamburgerIPC(Builder builder) {
        this.uri = builder.uri;
    }

    public <T> T create(Class<T> service) {
        String name = HamburgerUtils.getInterfaceName(service);
        return (T) Proxy.newProxyInstance(
                service.getClassLoader(),
                new Class[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Logger.getInterfaceLog().log("[request] uri: " + uri + ", className: " + name + ", method: " + method.getName() + ", args: " + Arrays.toString(args));
                        Object result = invokeInternal(name, method, args);
                        Logger.getInterfaceLog().log("[response] method: " + method.getName() + ", result: " + result);
                        return result;
                    }
                }
        );
    }

    private Object invokeInternal(String className, Method method, Object[] args) {
        Bundle request = HamburgerUtils.methodToBundle(method, args);
        Logger.getIpcLog().log("[request] uri: " + uri + ", className: " + className + ", request: " + request);
        Bundle response = getApplication().getContentResolver().call(
                Uri.parse("content://" + uri),
                className,
                HamburgerUtils.getMethodName(method),
                request
        );
        Class<?> returnType = method.getReturnType();
        Logger.getIpcLog().log("[response] method: " + method.getName() + " returnType: " + returnType + ", response: " + response);
        if (Void.class.isAssignableFrom(returnType)) {
            return null;
        }
        return HamburgerUtils.bundleToMethodResult(returnType, response);
    }

    public static final class Builder {

        private String uri = "";

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public HamburgerIPC build() {
            return new HamburgerIPC(this);
        }
    }
}
