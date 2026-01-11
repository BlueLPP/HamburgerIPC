package com.hamburger.ipc.bundle;

import android.os.Bundle;

import com.hamburger.ipc.client.ClientConverter;

import java.lang.reflect.Method;

public class BundleClientConverter implements ClientConverter {
    @Override
    public Bundle parameters(Method method, Object[] parameters) {
        Bundle result = new Bundle();
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            BundleUtils.putToBundle(result, Integer.toString(i), parameterTypes[i], parameters[i]);
        }
        return result;
    }

    @Override
    public Object result(Method method, Bundle bundle) {
        Class<?> type = method.getReturnType();
        if (Void.class.isAssignableFrom(type)) {
            return null;
        }
        return BundleUtils.getFromBundle(bundle, BundleUtils.KEY_METHOD_RESULT, type);
    }
}
