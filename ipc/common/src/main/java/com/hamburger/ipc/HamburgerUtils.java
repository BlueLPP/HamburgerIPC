package com.hamburger.ipc;

import java.lang.reflect.Method;

public final class HamburgerUtils {

    public static String getInterfaceName(Class<?> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        }
        Hamburger hamburger = service.getAnnotation(Hamburger.class);
        if (hamburger == null) {
            throw new IllegalArgumentException("API declarations must be use @Hamburger.");
        }
        return hamburger.value();
    }

    public static String getMethodName(Method method) {
        Hamburger hamburger = method.getAnnotation(Hamburger.class);
        return hamburger == null ? method.getName() : hamburger.value();
    }
}
