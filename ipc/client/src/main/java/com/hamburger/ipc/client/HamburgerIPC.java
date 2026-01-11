package com.hamburger.ipc.client;

import android.app.Application;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.hamburger.ipc.HamburgerUtils;
import com.hamburger.ipc.bundle.BundleClientConverter;
import com.hamburger.ipc.log.Logger;

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
    private final ClientConverter converter;

    private HamburgerIPC(Builder builder) {
        if (TextUtils.isEmpty(builder.uri)) {
            throw new IllegalArgumentException("uri is empty.");
        }
        this.uri = builder.uri;
        ClientConverter conv = builder.converter;
        this.converter = conv == null ? new BundleClientConverter() : conv;
    }

    public <T> T create(Class<T> service) {
        String name = HamburgerUtils.getInterfaceName(service);
        return (T) Proxy.newProxyInstance(
                service.getClassLoader(),
                new Class[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Logger.getInterfaceLog().log("[request] className: " + name + ", method: " + method.getName() + ", args: " + Arrays.toString(args));
                        Object result = invokeInternal(name, method, args);
                        Logger.getInterfaceLog().log("[response] method: " + method.getName() + ", result: " + result);
                        return result;
                    }
                }
        );
    }

    private Object invokeInternal(String className, Method method, Object[] args) {
        Logger.getInternalLog().log("[methodToBundle] method: " + method + ", args: " + Arrays.toString(args));

        Bundle parameters = converter.parameters(method, args);
        String methodName = HamburgerUtils.getMethodName(method);
        Logger.getIpcLog().log("[request] className: " + className + ", methodName: " + methodName);
        Bundle response = getApplication().getContentResolver().call(
                Uri.parse("content://" + uri),
                className,
                HamburgerUtils.getMethodName(method),
                parameters
        );
        Class<?> returnType = method.getReturnType();
        Logger.getIpcLog().log("[response] method: " + method.getName() + " returnType: " + returnType + ", response: " + response);
        return converter.result(method, response);
    }

    public static final class Builder {

        private String uri = "";
        private ClientConverter converter = null;

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder converter(ClientConverter converter) {
            this.converter = converter;
            return this;
        }

        public HamburgerIPC build() {
            return new HamburgerIPC(this);
        }
    }
}
